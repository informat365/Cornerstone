<style scoped>
    .link{
        color:#0094FB;
        text-decoration: underline;
        cursor: pointer;
    }
    code{
        margin-right:5px;
        margin-left:5px;
    }
</style>
<i18n>
    {
    "en": {
    "使用代码集成，可以将代码提交和任务进行关联，并且统计每个用户的代码提交信息。":"Using code integration, you can associate code submissions with tasks and count each user's code submission information.",
    "提交代码时在commit message中输入#任务ID, 例如： ":"Enter the #task ID in the commit message when submitting the code, for example:",
    "选择代码仓库类型":"Code Repository",
    "本地SVN":"SVN",
    "配置SVN hook (Linux)":"SVN hook (Linux)",
    "配置SVN hook (Windows)":"SVN hook (Windows)",
    "为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置svn hook":" In order for CORNERSTONE to be able to associate code submissions with the system, follow these steps to configure the svn hook",
    "请先cd 到svn hooks的目录，在svn服务器上面执行以下命令。":"Goto svn hooks dir,then input command blow:",
    "如果您希望提交的时候检查任务ID的正确性，请再执行以下脚本。":"If you want to check the correctness of the task ID when submitting, please execute the following script.",
    "如果您希望提交的时候检查任务ID的正确性，请下载以下脚本。":"If you want to check the correctness of the task ID when submitting, please execute the following script.",
    "svn hook脚本依赖Java环境，请确保svn服务器所在的机器安装了jdk1.8以上版本":"The svn hook script depends on the Java environment. Please ensure that the machine where the svn server is installed has jdk1.8 or higher.",
    "下载以下脚本到svn服务器的hooks目录":"Download the following script to the hooks directory of the svn server",
    "下载svnhook.jar到hooks目录":"Download svnhook.jar to the hooks directory",
    "为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置webhook":"In order for CORNERSTONE to be able to associate code submissions with the system, follow these steps to configure",
    "请进入GitHub项目Setting页面，点击Webhooks选项卡，配置以上参数":"Please go to the GitHub project Settings page, click the Webhooks tab, configure the above parameters.",
    "Content type 选择 application/json":"Content type choose application/json",
    "event选择":"event choose Just the push event",
    "请进入GitLab的System Hooks页面，配置以上参数":"Please go to GitLab's System Hooks page and configure the above parameters,and choose the push events as trigger",
    "修复bug":"fix bug",
    "配置GitHub webhook":"Config gitHub webhook",
    "若需要配置Gitlab代码量统计，需配置如下自定义hook脚本":"If you want to statis gitlab code committed,so you should to config a custom gitlab hook shell script named post-receive in global or project custom-hook directory"
    },
    "zh_CN": {
    "使用代码集成，可以将代码提交和任务进行关联，并且统计每个用户的代码提交信息。":"使用代码集成，可以将代码提交和任务进行关联，并且统计每个用户的代码提交信息。",
    "提交代码时在commit message中输入#任务ID, 例如： ":"交代码时在commit message中输入#任务ID, 例如： ",
    "选择代码仓库类型":"选择代码仓库类型",
    "本地SVN":"本地SVN",
    "配置SVN hook (Linux)":"配置SVN hook (Linux)",
    "配置SVN hook (Windows)":"配置SVN hook (Windows)",
    "为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置svn hook":"为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置svn hook",
    "请先cd 到svn hooks的目录，在svn服务器上面执行以下命令。":"请先cd 到svn hooks的目录，在svn服务器上面执行以下命令。",
    "如果您希望提交的时候检查任务ID的正确性，请再执行以下脚本。":"如果您希望提交的时候检查任务ID的正确性，请再执行以下脚本。",
    "svn hook脚本依赖Java环境，请确保svn服务器所在的机器安装了jdk1.8以上版本":"svn hook脚本依赖Java环境，请确保svn服务器所在的机器安装了jdk1.8以上版本",
    "下载以下脚本到svn服务器的hooks目录":"下载以下脚本到svn服务器的hooks目录",
    "如果您希望提交的时候检查任务ID的正确性，请下载以下脚本。":"如果您希望提交的时候检查任务ID的正确性，请下载以下脚本。",
    "下载svnhook.jar到hooks目录":"下载svnhook.jar到hooks目录",
    "为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置webhook":"为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置webhook",
    "请进入GitHub项目Setting页面，点击Webhooks选项卡，配置以上参数":"请进入GitHub项目Setting页面，点击Webhooks选项卡，配置以上参数",
    "Content type 选择 application/json":"Content type 选择 application/json",
    "event选择":"event 选择 Just the push event",
    "请进入GitLab的System Hooks页面，配置以上参数":"请进入GitLab的System Hooks页面，配置以上参数，并选择Trigger为push events",
    "修复bug":"修复bug",
    "配置GitHub webhook":"配置GitHub webhook",
    "若需要配置Gitlab代码量统计，需配置如下自定义hook脚本":"如果需要进行Gitlab代码量统计，需在gitlab服务端配置如下自定义全局或项目hook脚本post-receive"
    }
    }
</i18n>
<template>
    <div class="admin-page">
            <Alert>
                {{$t('使用代码集成，可以将代码提交和任务进行关联，并且统计每个用户的代码提交信息。')}}
                <br> {{$t('提交代码时在commit message中输入#任务ID, 例如： ')}} <code>#000001 {{$t('修复bug')}}</code>
            </Alert>

            <Form label-position="top">
                    <FormItem :label="$t('选择代码仓库类型')">
                        <div>
                            <RadioGroup v-model="scmType">
                                <Radio label="svnlinux">{{$t('本地SVN')}} (Linux)</Radio>
                                <Radio label="svnwin">{{$t('本地SVN')}} (Windows)</Radio>
                                <Radio label="gitlab">Gitlab</Radio>
                                <Radio label="github">Github</Radio>
                                <Radio v-if="false" label="git">本地git</Radio>
                            </RadioGroup>
                        </div>
                    </FormItem>

                    <FormItem v-show="scmType=='git1'" label="配置git hook">
                        <div>为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置git hook</div>

                        <div>{{$t('请先cd 到svn hooks的目录，在svn服务器上面执行以下命令。')}}</div>
                        <Alert>
                            curl -o post-commit {{host}}/p/webapi/svn_post_commit_hook/{{companyUUID}}<br>
                            chmod +x post-commit;
                        </Alert>

                        <div class="mt10">{{$t('如果您希望提交的时候检查任务ID的正确性，请再执行以下脚本。')}}</div>
                        <Alert>
                            curl -o pre-commit {{host}}/p/webapi/svn_pre_commit_hook/{{companyUUID}}<br>
                            chmod +x pre-commit;
                        </Alert>
                    </FormItem>

                    <FormItem v-show="scmType=='svnlinux'" :label="$t('配置SVN hook (Linux)')">
                        <div>{{$t('为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置svn hook')}}</div>

                        <div>{{$t('请先cd 到svn hooks的目录，在svn服务器上面执行以下命令。')}}</div>
                        <Alert>
                            curl -o post-commit {{host}}/p/webapi/svn_post_commit_hook/{{companyUUID}}<br>
                            chmod +x post-commit;
                        </Alert>

                        <div class="mt10">{{$t('如果您希望提交的时候检查任务ID的正确性，请再执行以下脚本。')}}</div>
                        <Alert>
                            curl -o pre-commit {{host}}/p/webapi/svn_pre_commit_hook/{{companyUUID}}<br>
                            chmod +x pre-commit;
                        </Alert>
                    </FormItem>

                    <FormItem v-show="scmType=='svnwin'" :label="$t('配置SVN hook (Windows)')">
                        <div>{{$t('为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置svn hook')}}</div>
                        <div style="font-weight:bold">{{$t('svn hook脚本依赖Java环境，请确保svn服务器所在的机器安装了jdk1.8以上版本')}}</div>
                        <div>{{$t('下载以下脚本到svn服务器的hooks目录')}}</div>
                        <Alert>
                            <span class="link" @click="downloadWinPostCommit">post-commit.bat</span>
                        </Alert>

                        <div class="mt10">{{$t('如果您希望提交的时候检查任务ID的正确性，请下载以下脚本。')}}</div>
                        <Alert>
                            <span class="link" @click="downloadWinPreCommit">pre-commit.bat</span>
                        </Alert>

                        <div class="mt10">{{$t('下载svnhook.jar到hooks目录')}}</div>
                        <Alert>
                            <span class="link" @click="downloadSvhHook">svnhook.jar</span>
                        </Alert>

                        <Alert type="warning">
                            下载完成后请修改pre-commit.bat和post-commit.bat中的<code>java</code> <code>svnlook</code> <code>svnhook.jar</code>
                            为正确的路径地址。例如java安装在 <code>c:\Program Files\Java\bin\java.exe</code>  则需要将java 修改成<code> "c:\Program Files\Java\bin\java.exe"</code>
                        </Alert>
                    </FormItem>


                    <FormItem  v-show="scmType=='github'" :label="$t('配置GitHub webhook')">
                        <div>{{$t('为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置webhook')}}</div>

                        <Alert>
                            <div>Payload URL:{{host}}/p/webapi/github_webhook?token={{githubToken}}</div>
                            <div>Secret:{{githubToken}}</div>
                        </Alert>
                        <div>{{$t('请进入GitHub项目Setting页面，点击Webhooks选项卡，配置以上参数')}}</div>
                        <div>{{$t('Content type 选择 application/json')}}</div>
                        <div>{{$t('event选择')}}</div>

                    </FormItem>

                     <FormItem  v-show="scmType=='gitlab'" label="配置GitLab webhook">
                        <div>{{$t('为了让CORNERSTONE能够将代码提交和系统关联，请按照以下步骤配置webhook')}}</div>
                        <Alert>
                            <div>URL:{{host}}/p/webapi/gitlab_webhook</div>
                            <div>Secret Token:{{gitlabToken}}</div>
                        </Alert>
                         <div>{{$t('请进入GitLab的System Hooks页面，配置以上参数')}}</div>

                         <div>{{$t('若需要配置Gitlab代码量统计，需配置如下自定义hook脚本')}}</div>
                         <Alert>
<!--                             <div v-html="gitlabCodeHook"></div>-->
                             <span class="link" @click="downloadGitlabHook">post-receive</span>
                         </Alert>
                        <p style="font-weight: 600">全局配置</p>
                         <ol style="padding-left: 1rem;">
                             <li>在gitlab创建自定义hook存放目录custom_hooks(默认gitlab路径为/opt/gitlab/embedded/service/gitlab-shell，请以实际路径为准)</li>
                             <li>修改 /etc/gitlab/gitlab.rb  配置文件中的配置项：gitlab_shell['custom_hooks_dir'] = "/opt/gitlab/embedded/service/gitlab-shell/custom_hooks"</li>
                             <li>生效配置文件 sudo gitlab-ctl reconfigure</li>
                             <li>在自定义的 custom_hooks_dir 目录(即我们的custom_hooks目录)下可创建post-receive.d文件夹</li>
                             <li>拷贝post-receive文件到post-receive.d中</li>
                             <li>赋予hook执行权限，chmod -R +x post-receive</li>
                             <li>如果需要排除某些项目，则要添加例外文件。创建/opt/gitlab/embedded/service/gitlab-shell/custom_hooks/excludes/excludes.txt文件，在文件中添加需要排除的git地址即可</li>
                         </ol>
                         <p style="font-weight: 600">项目单独配置(推荐)</p>
                         <ol style="padding-left: 1rem;">
                             <li>拷贝post-receive文件到项目hooks目录下(/project.git/hooks/，请以实际项目路径(project.git)为准)</li>
                             <li>赋予hook执行权限，chmod a+x post-receive</li>
                         </ol>

                    </FormItem>

            </Form>
    </div>
</template>

<script>

export default {
    mixins: [componentMixin],
    data(){
        return {
            scmType:"svnlinux",
            host:"",
            companyUUID:"",
            gitlabToken:"",
            githubToken:"",
            gitlabCodeHook:""
        }
    },
    methods:{
        pageLoad(){
            this.companyUUID=app.company.uuid;
            this.host=app.getHost();
            app.invoke('BizAction.gitlabWebhookToken',[app.token],(info)=>{
                this.gitlabToken=info;
            })
            app.invoke('BizAction.githubWebhookToken',[app.token],(info)=>{
                this.githubToken=info;
            })
        },
        downloadWinPostCommit(){
            window.open(this.host+"/p/webapi/svn_post_commit_hook_win/"+this.companyUUID);
        },
        downloadWinPreCommit(){
            window.open(this.host+"/p/webapi/svn_pre_commit_hook_win/"+this.companyUUID);
        },
        downloadSvhHook(){
             window.open(this.host+"/svnhook.jar");
        },
        downloadGitlabHook(){
            window.open(this.host+"/p/webapi/gitlab_web_post_hook_shell/"+this.companyUUID);
        },
        getGitlabCodeHookShell(){
            var _this =this;
            try {
                ajaxInvoke(app.serverAddr,'/p/webapi/gitlab_web_post_hook_shell/'+this.companyUUID,[app.token],(info)=>{
                    _this.gitlabCodeHook=info.ret;
                });
            }catch (e) {
                console.error(e)
            }
            console.log(_this.gitlabCodeHook)
        }
    }
}
</script>
