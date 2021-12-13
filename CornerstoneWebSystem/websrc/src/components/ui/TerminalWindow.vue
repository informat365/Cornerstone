<style>
    .window-style .window{
        border-radius: 0 !important;
        background:#fff !important;
    }
    .window-style .title{
        user-select: none;
        color:#666;
        font-weight: bold;
        padding-top:2px;
    }
    .window-style .window{
        z-index:2 !important;
    }
    .window-style .titlebar{
        background: #fff !important;
        border-bottom: 1px solid #eee;
    }
    .terminal-wrap{
        display: flex;
        flex-direction: column;
        height: 100%;
    }
    .terminal-box{
        flex:1;
        position: relative;
        overflow: hidden;
        background-color: #F7F7F7;
    }
    .status-box{ 
        background-color: #F7F7F7;
        border-top:1px solid #DEDEDE;
        padding:5px;
    }
</style>
<i18n>
{
    "en": {
        "文件管理":"Files",
        "已连接":"connected",
        "连接中":"connecting",
        "连接成功":"connected",
        "连接已关闭":"closed",
        "正在上传":"uploading",
        "上传成功":"upload success",
        "连接错误：":"error："
    },
    "zh_CN": {
        "文件管理":"文件管理",
        "已连接":"已连接",
        "连接中":"连接中",
        "连接成功":"连接成功",
        "连接已关闭":"连接已关闭",
        "正在上传":"正在上传",
        "上传成功":"上传成功",
        "连接错误：":"连接错误："
    }
}
</i18n>
 <template>
    <hsc-window-style-metal v-show="dialogVisible" class="window-style">
        <hsc-window  :title="windowTitle"  :minWidth="650" :minHeight="450" 
        :resizable="true" 
        :isScrollable="false"
        @resize-end="onWindowResize"
        :closeButton="true" :isOpen.sync="showDialog">
          <div class="terminal-wrap">
                <div ref="terminal" class="terminal-box">    
                    
                </div>
              
                <div class="status-box">    
                    <IconButton  @click="showFileDialog" icon="md-document"  :tips="$t('文件管理')" ></IconButton>  
                </div>
          </div>
        
        </hsc-window>
    </hsc-window-style-metal>
</template>


<script>
import {Terminal} from 'xterm'
import {SearchAddon} from 'xterm-addon-search'
import {FitAddon} from 'xterm-addon-fit'
import 'xterm/css/xterm.css'

export default {
        name:"TerminalWindow",
        mixins: [componentMixin],
        data () {
            return {
                showDialog:false,
                dialogVisible:true,
                loginSuccess:false,
                uploadViewVisible:false,
                title:"",
                accountList:[],
                firstMessage:true,
            }
        },
        computed:{
            windowTitle(){
                return this.title +" "+ (this.loginSuccess?this.$t("已连接"):this.$t("连接中"));
            }
        },
        beforeDestroy(){
            this.ws.close();
        },
        methods: {
            pageLoad(){
                this.title=this.args.info.machineName;
                this.$nextTick(()=>{
                    this.initTerm();
                    this.login();
                })
            },
            pageMessage(type,content){
                if(type=='module.change'){
                    if(content=='devops'){
                        this.dialogVisible=true;
                    }else{
                        this.dialogVisible=false;
                    }
                }
            },
            onWindowResize(){
                this.$nextTick(()=>{
                    this.fitAddon.fit();
                })
            },
            initTerm(){
                const terminal = new Terminal({
                    fontSize: 12,
                    rendererType:'dom',
                    fontWeight:'normal',
                    fontFamily:'Consola,FreeMono, Menlo, Terminal, monospace'
                });
                let fitAddon = new FitAddon();
                this.fitAddon=fitAddon;
                terminal.loadAddon(fitAddon);
                terminal.loadAddon(new SearchAddon());
                this.fitAddon.fit();
                //
                terminal.onData(send => {
                    if(this.loginSuccess){
                        this.ws.send("0" + send);
                    }
                });
                terminal.onResize((dim) => {
                    if(this.loginSuccess){
                        this.ws.send("1"+dim.cols+","+dim.rows);
                    }
                });
                //
                terminal.open(this.$refs["terminal"]);
                this.term=terminal;
            },
            login(){
                //
                var httpsEnabled = window.location.protocol == "https:";
                var host=window.location.hostname;
                var httpPort=window.location.port;
                var url="";
                if(httpsEnabled){
                    url='wss://'+host+'/webssh/'+this.args.info.token;
                }else{
                    if(httpPort==80){
                        url='ws://'+host+'/webssh/'+this.args.info.token;
                    }else{
                        url='ws://'+host+":"+this.args.info.websocketPort+'/webssh/'+this.args.info.token;
                    }
                }
                //
                var protocols = ["webssh"];
                var ws = new WebSocket(url, protocols);
                var term=this.term;
                //
                ws.onopen = (event)=>{
                    app.toast(this.$t('连接成功'));
                    this.loginSuccess=true;
                    this.fitAddon.fit();
                };
                //
                ws.onmessage = (event)=>{
                    if(this.receiveCurrentPath==true){
                        this.currentPath=event.data;
                        this.receiveCurrentPath=false;
                    }
                    this.term.writeUtf8(event.data);
                    if(this.firstMessage){
                        this.fitAddon.fit();
                        this.firstMessage=false;
                    }

                }
                //
                ws.onclose = (event)=> {
                    this.term.setOption("cursorBlink", false);
                }
                //
                ws.onerror = (error)=>{
                    this.term.setOption("cursorBlink", false);
                }
                //
                this.ws=ws;
            },
            showFileDialog:function(){
                app.showDialog(TerminalFileDialog,{
                    info:this.args.info
                })
            }
        }
    }
</script>