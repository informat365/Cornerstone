/**
 * 
 */
package cornerstone.biz.ssh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.handler.timeout.IdleStateHandler;
import jazmin.core.Jazmin;
import jazmin.core.Server;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.misc.InfoBuilder;
import jazmin.misc.io.IOWorker;
import jazmin.server.console.ConsoleServer;

/**
 * WebSshServer is a simple proxy server that let you login ssh server via web applications.
 * see also https://github.com/yudai/gotty
 * @author yama
 * 26 Aug, 2015
 */
public class WebSshServer extends Server{
	private static Logger logger=LoggerFactory.get(WebSshServer.class);
	//
	private int port;
	private  EventLoopGroup bossGroup;
	private  EventLoopGroup workerGroup;
	private  Map<String, SshChannel>channels;
	private int defaultSshConnectTimeout;
	private AtomicLong channelCounter;
	//
	public WebSshServer() {
		channelCounter=new AtomicLong();
		channels=new ConcurrentHashMap<String, SshChannel>();
		port=9101;
		defaultSshConnectTimeout=5000;
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
	 * @return the defaultSshConnectTimeout
	 */
	public int getDefaultSshConnectTimeout() {
		return defaultSshConnectTimeout;
	}

	/**
	 * @param defaultSshConnectTimeout the defaultSshConnectTimeout to set
	 */
	public void setDefaultSshConnectTimeout(int defaultSshConnectTimeout) {
		this.defaultSshConnectTimeout = defaultSshConnectTimeout;
	}

	//
	public void addChannel(SshChannel c){
		if(c.endpoint==null){
			throw new IllegalArgumentException("endpoint can not be null");
		}
		c.id=channelCounter.incrementAndGet()+"";
		channels.put(c.id, c);
	}
	//
	void removeChannel(String id){
		channels.remove(id);
	}
	//
	public List<SshChannel>getChannels(){
		return new ArrayList<SshChannel>(channels.values());
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
                pipeline.addLast("idleStateHandler",new IdleStateHandler(3600,3600,0));
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(65536));
                pipeline.addLast(new WebSocketServerCompressionHandler());
                pipeline.addLast(new WebSshWebSocketHandler(WebSshServer.this,wss));
            }
        })
        .option(ChannelOption.SO_BACKLOG, 128)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.TCP_NODELAY, true);
        //
        b.bind(port).sync();    	
    }
	//
	void updateChannelTicket(){
		while(true){
			channels.forEach((id,channel)->{
				try{
					channel.updateTicket();
				}catch(Exception e){
					logger.catching(e);
				}
			});
			try {
				Thread.sleep(1000);
				if(logger.isDebugEnabled()) {
//					logger.debug("channels:{}",channels.size());
				}
			} catch (InterruptedException e) {
				logger.catching(e);
			}
		}
	}
	//--------------------------------------------------------------------------
	@Override
	public void init() {
		ConsoleServer cs = Jazmin.getServer(ConsoleServer.class);
		if (cs != null) {
			cs.registerCommand(WebSshServerCommand.class);
		}
	}
	//
	@Override
	public void start() throws InterruptedException {
		IOWorker ioWorker=new IOWorker("WebSshServerWorker",
    			Runtime.getRuntime().availableProcessors()*2+1);
		bossGroup=new NioEventLoopGroup(1,ioWorker);
		workerGroup=new NioEventLoopGroup(0,ioWorker);
		createWSListeningPoint(false);
		Thread ticketThread=new Thread(this::updateChannelTicket);
		ticketThread.setName("WebSshServerTicket");
		ticketThread.start();
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
	@Override
	public String info() {
		InfoBuilder ib=InfoBuilder.create();
		ib.section("info")
		.format("%-30s:%-30s\n")
		.print("port",getPort())
		.print("defaultSshConnectTimeout",getDefaultSshConnectTimeout());
		return ib.toString();
	}
}
