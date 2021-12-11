/**
 * 
 */
package cornerstone.biz.websocket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import cornerstone.biz.domain.Account;
import cornerstone.biz.util.DateUtil;
import cornerstone.biz.util.DumpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import jazmin.core.Jazmin;
import jazmin.core.Server;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.misc.InfoBuilder;
import jazmin.misc.io.IOWorker;
import jazmin.util.JSONUtil;

/**
 * 
 * @author cs
 *
 */
public class WebEventServer extends Server{
	private static Logger logger=LoggerFactory.get(WebEventServer.class);
	//
	private int port;
	private  EventLoopGroup bossGroup;
	private  EventLoopGroup workerGroup;
	private int defaultConnectTimeout;
	private AtomicLong channelCounter;
	//
	public static Map<Integer,List<WebEventChannel>> channelMap;
	//
	public WebEventServer(int port) {
		channelCounter=new AtomicLong();
		channelMap=new ConcurrentHashMap<>();
		this.port=port;
		defaultConnectTimeout=5000;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the defaultConnectTimeout
	 */
	public int getDefaultConnectTimeout() {
		return defaultConnectTimeout;
	}
	/**
	 * @param defaultConnectTimeout the defaultConnectTimeout to set
	 */
	public void setDefaultConnectTimeout(int defaultConnectTimeout) {
		this.defaultConnectTimeout = defaultConnectTimeout;
	}
	//
	public void addChannel(WebEventChannel channel){
		Account account=channel.account;
		if(account==null) {
			return;
		}
		channel.id=channelCounter.incrementAndGet()+"";
		List<WebEventChannel> channels=channelMap.get(account.companyId);
		if(channels==null) {
			channels=new ArrayList<>();
			channelMap.put(account.companyId, channels);
		}
		channels.add(channel);
		logger.info("WebEventServer addChannel{} accountId:{} companyId:{}",channel,account.id,account.companyId);
	}
	//
	public void removeChannel(WebEventChannel channel){
		Account account=channel.account;
		if(account==null) {
			return;
		}
		List<WebEventChannel> channels=channelMap.get(account.companyId);
		if(channels==null) {
			logger.error("channels==null,account.companyId:{}",account.companyId);
			return;
		}
		boolean removeChannel=channels.remove(channel);
		if(logger.isInfoEnabled()) {
			logger.info("WebEventServer removeChannel {} removeSuccess:{} "
					+ "accountId:{} lastAccessTime:{}",
					channel.getChannelId(),removeChannel,account.id,
					DateUtil.formatDate(channel.lastAccessTime));
		}
	}
	//
	public List<WebEventChannel> getChannels(int companyId){
		if(!channelMap.containsKey(companyId)) {
			return new ArrayList<>();
		}
		return channelMap.get(companyId);
	}
	//
	public void boardcastAsync(WebEvent event) {
		Jazmin.execute(()->{
			boardcast(event);
		});
	}
	//广播
	public void boardcast(WebEvent event) {
		long startTime=System.currentTimeMillis();
		List<WebEventChannel> list=getChannels(event.companyId);
		for (WebEventChannel e : list) {
			try {
				e.sendMessageToClient(JSONUtil.toJson(event));
			} catch (Exception ex) {
				logger.error(ex.getMessage(),ex);
			}
		}
		logger.info("WebEventServer boardcastWebEvent using:{} channels:{} event:{} ",
				System.currentTimeMillis()-startTime,
				list.size(),
				DumpUtil.dump(event));
	}
	//
	private void createWSListeningPoint(boolean wss) throws InterruptedException {
	    final ServerBootstrap b = new ServerBootstrap();
        b.group(this.bossGroup, this.workerGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(final SocketChannel ch)  {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("idleStateHandler",new WebEventIdleStateHandler(3600,3600,0));
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebEventSocketHandler(WebEventServer.this,wss));
            }
        })
        .option(ChannelOption.SO_BACKLOG, 128)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.TCP_NODELAY, true);
        //
        ChannelFuture future=b.bind(port).sync(); 
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future)  {
                logger.info("WebEventServer channel{} is closed",future.channel());
            }
        });
    }
	//--------------------------------------------------------------------------
	@Override
	public void init()  {
	}
	//
	@Override
	public void start() throws InterruptedException {
		IOWorker ioWorker=new IOWorker("WebSocketServerWorker",
    			Runtime.getRuntime().availableProcessors()*2+1);
		bossGroup=new NioEventLoopGroup(1,ioWorker);
		workerGroup=new NioEventLoopGroup(0,ioWorker);
		createWSListeningPoint(false);
		//
		Thread t=new Thread(this::dumpChannel);
		t.setName("WebEventServerTicket");
		t.start();
		//
		logger.info(info());
	}
	@Override
	public void stop()  {
		if(bossGroup!=null){
			bossGroup.shutdownGracefully();
		}
		if(workerGroup!=null){
			workerGroup.shutdownGracefully();
		}
	}
	//
	void dumpChannel(){
		while(true){
			if(logger.isInfoEnabled()) {
				logger.info(dumpChannels());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.catching(e);
			}
		}
	}
	//
	private String dumpChannels() {
		StringBuilder channels=new StringBuilder();
		channels.append("\n==========================================\n");
		channels.append("WebEventServer webssh sessions[serverStartTime:"+DateUtil.formatDate(Jazmin.getStartTime())+")]\n");
		int total=0;
		for (Map.Entry<Integer,List<WebEventChannel>> entry : channelMap.entrySet()) {
			int companyId=entry.getKey();
			List<WebEventChannel> channel=entry.getValue();
			channels.append("company[").append(companyId).append("]\n");
			channels.append(String.format("%-20s%-15s%-20s%-25s%-25s", "account","channelId",
					"ip","createTime","lastAccessTime")).append("\n");
			for (WebEventChannel e : channel) {
				if(e.account==null) {
					continue;
				}
				channels.append(e.dump()).append("\n");
				total++;
			}
			channels.deleteCharAt(channels.length()-1);
			channels.append("\n");
		}
		channels.append("\ntotal:").append(total).append("\n");
		channels.append("==========================================\n");
		return channels.toString();
	}
	//
	@Override
	public String info() {
		InfoBuilder ib=InfoBuilder.create();
		ib.section("info")
		.format("%-30s:%-30s\n")
		.print("port",getPort())
		.print("defaultConnectTimeout",getDefaultConnectTimeout());
		return ib.toString();
	}
}
