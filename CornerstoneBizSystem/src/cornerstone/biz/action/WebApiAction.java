package cornerstone.biz.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Strings;
import cornerstone.biz.ConstDefine;
import cornerstone.biz.action.CommAction.CommActionImpl;
import cornerstone.biz.annotations.ApiDefine;
import cornerstone.biz.cmdb.JavaScriptCmdb;
import cornerstone.biz.dao.BizDAO;
import cornerstone.biz.domain.*;
import cornerstone.biz.domain.CmdbApplication.CmdbApplicationInfo;
import cornerstone.biz.domain.CmdbInstance.CmdbInstanceInfo;
import cornerstone.biz.domain.CmdbMachine.CmdbMachineInfo;
import cornerstone.biz.domain.CompanyMember.CompanyMemberInfo;
import cornerstone.biz.domain.DataTable.DataTableInfo;
import cornerstone.biz.domain.GithubWebhook.GithubWebhookBody.GithubWebhookCommit;
import cornerstone.biz.domain.GitlabWebhookBody.GitlabWebhookCommit;
import cornerstone.biz.domain.ProjectArtifact.ProjectArtifactInfo;
import cornerstone.biz.domain.ProjectMember.ProjectMemberInfo;
import cornerstone.biz.domain.ProjectSubSystem.ProjectSubSystemInfo;
import cornerstone.biz.domain.Task.TaskInfo;
import cornerstone.biz.domain.WebApi.WebApiInfo;
import cornerstone.biz.domain.WebApi.WebApiQuery;
import cornerstone.biz.srv.BizService;
import cornerstone.biz.srv.WebApiService;
import cornerstone.biz.util.*;
import cornerstone.biz.webapi.JavaScriptWebApi;
import cornerstone.biz.webapi.WebApiReq;
import jazmin.core.app.AppException;
import jazmin.core.app.AutoWired;
import jazmin.driver.jdbc.Transaction;
import jazmin.driver.jdbc.smartjdbc.QueryWhere;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.server.rpc.RpcService;
import jazmin.util.DumpUtil;
import jazmin.util.JSONUtil;

/**
 * WebApi Action
 *
 * @author cs
 */

@ApiDefine("WebApi 接口")
public interface WebApiAction {

    /**
     * 运行cmdb api
     */
    String runCmdbApi(String apiKey, Map<String, String> queryMap, String host);

    List<CmdbInstanceInfo> getCmdbInstanceList(String apiKey);

    List<CmdbApplicationInfo> getCmdbApplicationList(String apiKey);

    List<CmdbMachineInfo> getCmdbMachineList(String apiKey);

    List<CompanyMemberInfo> getCompanyMemberList(String apiKey);

    List<ProjectMemberInfo> getProjectMemberList(String apiKey);

    List<ProjectSubSystemInfo> getProjectSubSystemList(String apiKey);

    /***/
    String getArtifactUuid(String name, String version);

    /***/
    ProjectArtifactInfo getProjectArtifactByUuid(String uuid);

    // SCM相关

    /***/
    String svnPreCommitHook(String companyUuid);

    String svnPostCommitHook(String companyUuid);

    String svnPreCommitHookWin(String companyUuid);

    String svnPostCommitHookWin(String companyUuid);

    /**
     * svn提交
     */
    void svnCommit(String token, List<String> content);

    /***/
    String gitlabPostCommitHook(String companyUuid);

    /**
     * gitlab push hook
     */
    void gitlabPostReceive(String token, List<String> items);

    /***/
    String svnPreCommit(String token, String comment);

    /***/
    void gitlabWebhook(String token, GitlabWebhook hook);

    /***/
    void githubWebhook(String token, GithubWebhook hook);

    //webapi

    /**
     * 通过ID查询webApi
     */
    WebApiInfo getWebApiById(String token, int id);

    /**
     * 查询webApi列表和总数
     */
    Map<String, Object> getWebApiList(String token, WebApiQuery query);

    /**
     * 新增webApi
     */
    int addWebApi(String token, WebApiInfo bean);

    /**
     * 编辑webApi
     */
    void updateWebApi(String token, WebApiInfo bean);

    /**
     * 启用禁用webApi
     */
    void updateWebApiStatus(String token, int id, int status);

    /**
     * 删除webApi
     */
    void deleteWebApi(String token, int id);

    /**
     * 刷新apiKey
     */
    void refreshWebApiKey(String token, int id);

    /**
     * 调用webApi
     */
    String runWebApi(String apiKey, WebApiReq req);


    //
    @RpcService
    class WebApiActionImpl extends CommActionImpl implements WebApiAction {
        //
        private static Logger logger = LoggerFactory.get(WebApiActionImpl.class);
        //
        @AutoWired
        BizDAO dao;
        @AutoWired
        BizService bizService;
        @AutoWired
        WebApiService webApiService;

        @Override
        public List<CmdbInstanceInfo> getCmdbInstanceList(String apiKey) {
            return bizService.getCmdbInstanceList(apiKey);
        }

        @Override
        public List<CmdbApplicationInfo> getCmdbApplicationList(String apiKey) {
            return bizService.getCmdbApplicationList(apiKey);

        }

        @Override
        public List<CmdbMachineInfo> getCmdbMachineList(String apiKey) {
            return bizService.getCmdbMachineList(apiKey);
        }

        @Override
        public List<CompanyMemberInfo> getCompanyMemberList(String apiKey) {
            return bizService.getCompanyMemberList(apiKey);
        }

        @Override
        public List<ProjectMemberInfo> getProjectMemberList(String apiKey) {
            return bizService.getProjectMemberList(apiKey);
        }

        @Override
        public List<ProjectSubSystemInfo> getProjectSubSystemList(String apiKey) {
            return bizService.getProjectSubSystemList(apiKey);
        }

        @Override
        public String runCmdbApi(String apiKey, Map<String, String> queryMap, String host) {
            CmdbApi old = dao.getExistedCmdbApi(apiKey);
            JavaScriptCmdb engine = new JavaScriptCmdb(bizService);
            engine.put("$query", queryMap);
            engine.put("$host", host);
            engine.apiKey = apiKey;
            return engine.run(old.code);
        }

        @Override
        public String svnPreCommitHook(String companyUuid) {
            Company company = dao.getCompanyInfoByUuid(companyUuid);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_SVN);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_SVN;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/sh\n");
            sb.append("export LANG='en_US.UTF-8'\n");
            sb.append("REPOS=\"$1\"\n");
            sb.append("TXN=\"$2\"\n");
            sb.append("svnlook_log=`svnlook log $REPOS -t $TXN`\n");
            sb.append("author=`svnlook author -t $TXN $REPOS`\n");
            sb.append("tmpcontent=$author' '$svnlook_log\n");
            sb.append("content=`echo \"\"\"${tmpcontent}\"\"\"|base64`\n");
            sb.append("ret=`curl --silent -d \"\"\"${content}\"\"\"  -H 'Content-Type: application/text' " + GlobalConfig.webUrl + "p/webapi/svn_pre_commit/" + scmToken.token + "`\n");
            sb.append("if [ $ret = 'SUCCESS' ];then\n");
            sb.append("\texit 0;\n");
            sb.append("fi\n");
            sb.append("echo -e $ret 1>&2\n");
            sb.append("exit -1");
            return sb.toString();
        }

        @Override
        public String svnPostCommitHook(String companyUuid) {
            Company company = dao.getCompanyInfoByUuid(companyUuid);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_SVN);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_SVN;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/sh\n");
            sb.append("export LANG='en_US.UTF-8'\n");
            sb.append("REPOS=\"$1\"\n");
            sb.append("REV=\"$2\"\n");
            sb.append("svnrepos=`echo $REPOS|base64`\n");
            sb.append("svnrev=`echo $REV|base64`\n");
            sb.append("svnlook_comment=`svnlook log -r $REV $REPOS | base64`\n");
            sb.append("svnlook_author=`svnlook author -r $REV $REPOS |base64`\n");
            sb.append("svnlook_changed=`svnlook changed -r $REV $REPOS |base64`\n");
            sb.append("svnlook_diff=`echo ''|base64`\n");//不要diff否则太长
            sb.append("addline=`svnlook diff -r $REV $REPOS|grep '^+' |grep -v '^+++' |sed 's/^.//'|sed '/^$/d' |wc -l|base64`\n");
            sb.append("decreaseline=`svnlook diff -r $REV $REPOS|grep '^-' |grep -v '^---' |sed 's/^.//'|sed '/^$/d' |wc -l|base64`\n");
            sb.append("svnlook_log=\"$svnrepos#$svnrev#$svnlook_author#$svnlook_comment#$svnlook_diff#$svnlook_changed#$addline#$decreaseline\"\n");
            sb.append("curl -d \"${svnlook_log}\"  -H 'Content-Type: application/text' " + GlobalConfig.webUrl + "p/webapi/svn_commit/" + scmToken.token + "\n");
            sb.append("exit 0");
            return sb.toString();
        }

        @Override
        public String svnPreCommitHookWin(String companyUuid) {
            Company company = dao.getCompanyInfoByUuid(companyUuid);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_SVN);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_SVN;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("@echo off\r\n" +
                    "setlocal\r\n" +
                    "set REPOS=%1\r\n" +
                    "set TXN=%2\r\n" +
                    "for /f \"usebackq delims==\" %%i in (`java -jar svnhook.jar pre svnlook \"%REPOS%\" \"%TXN%\" " + GlobalConfig.webUrl + "p/webapi/svn_pre_commit/" + scmToken.token + "`) do (\r\n" +
                    "echo  %%i 1>&2\r\n" +
                    "if \"%%i\" == \"SUCCESS\" ( exit 0 )\r\n" +
                    ")\r\n" +
                    "exit 1");
            return sb.toString();
        }

        @Override
        public String svnPostCommitHookWin(String companyUuid) {
            Company company = dao.getCompanyInfoByUuid(companyUuid);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_SVN);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_SVN;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("@echo off\r\n" +
                    "setlocal\r\n" +
                    "set REPOS=%1\r\n" +
                    "set REV=%2\r\n" +
                    "for /f \"usebackq delims==\" %%i in (`java -jar svnhook.jar post svnlook \"%REPOS%\" \"%REV%\" " + GlobalConfig.webUrl + "p/webapi/svn_commit/" + scmToken.token + "`) do (\r\n" +
                    "echo  %%i 1>&2\r\n" +
                    ")\r\n" +
                    "exit 0");
            return sb.toString();
        }

        private int parseSvnCommitInt(String value) {
            if (value != null) {
                value = value.trim();
            }
            if (StringUtil.isEmpty(value)) {
                return 0;
            }
            try {
                return Integer.valueOf(value);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return 0;
        }

        /**
         * svn commit
         */
        @Transaction
        @Override
        public void svnCommit(String token, List<String> items) {
            if (items.size() < 6) {
                logger.error("content fomat error.{}", DumpUtil.dump(items));
                return;
            }
            ScmToken scmToken = dao.getExistedScmTokenByToken(token);
            ScmCommitLog log = new ScmCommitLog();
            log.companyId = scmToken.companyId;
            log.scmTokenId = scmToken.id;
            String repo = items.get(0);
            log.repo = repo;
            log.branch = repo.substring(repo.lastIndexOf("/") + 1);
            log.version = items.get(1);
            log.author = items.get(2);
            log.comment = items.get(3);
            log.diff = items.get(4);
            log.changed = items.get(5);
            if (items.size() >= 8) {
                log.addLineNum = parseSvnCommitInt(items.get(6));
                log.decreaseLineNum = parseSvnCommitInt(items.get(7));
            }
            //
            if (log.author != null) {
                log.author = log.author.trim();
            }
            if (!StringUtil.isEmpty(log.diff)) {
                String[] lines = log.diff.split("\n");
                for (String line : lines) {
                    if (line != null) {
                        if (line.startsWith("+") && (!line.startsWith("+++"))) {
                            log.addLineNum++;
                        }
                        if (line.startsWith("-") && (!line.startsWith("---"))) {
                            log.decreaseLineNum++;
                        }
                    }
                }
            }
            if (log.diff != null && log.diff.length() > 1024) {
                log.diff = log.diff.substring(0, 1024);
            }
            log.uuid = BizUtil.randomUUID();
            dao.add(log);
            //
            addTaskScmCommitLogs(log);
        }


        /**
         * 校验是否对分支进行关联
         */
        private boolean checkScmBranch(int companyId, String branch, String projectName) {
            //分支限定
            List<ScmBranch> branches = dao.getScmBranchListByCompanyId(companyId);
            if (!BizUtil.isNullOrEmpty(branches)) {
                Optional<ScmBranch> scmOptional = branches.stream().filter(k -> k.branch.equals(branch) && k.project.equals(projectName)).findFirst();
                if (!scmOptional.isPresent()) {
                    logger.info("the branch :{} is not in config branch list", branch);
                    return false;
                } else {
                    ScmBranch scm = scmOptional.get();
                    if (!scm.isAssociate) {
                        logger.info("the branch :{} config forbid associate", branch);
                        return false;
                    }
                }
            }
            return true;
        }

        @Transaction
        @Override
        public void gitlabWebhook(String token, GitlabWebhook hook) {
            ScmToken scmToken = dao.getExistedScmTokenByToken(token);
            if(BizUtil.isNullOrEmpty(hook.body)){
                return;
            }
            if (BizUtil.isNullOrEmpty(hook.body.commits)) {
                return;
            }
            String pushName = hook.body.user_name;
            String userEmail =hook.body.user_email;

            String ref = hook.body.ref;
            String branch = null;
            if (!Strings.isNullOrEmpty(ref)) {
                branch =ref.substring(ref.lastIndexOf("/") + 1);
            }
//            hook.body.project.default_branch;
            String projectName = hook.body.project.name;

            if (!checkScmBranch(scmToken.companyId, branch, projectName)) {
                return;
            }

            for (GitlabWebhookCommit commit : hook.body.commits) {
                //修复gitlab触发重复统计的问题
                if (BizUtil.isNullOrEmpty(commit.author) ) {
                    continue;
                }
                //去掉邮箱的校验改为name，部分gitlab版本邮箱字段无数据
               if( BizUtil.isNullOrEmpty(commit.author.name) ){
                   continue;
               }
                if (!(commit.author.name.equals(pushName)||(!BizUtil.isNullOrEmpty(commit.author.email)&&commit.author.email.equals(userEmail)))) {
                    continue;
                }
                ScmCommitLog commitLog = dao.getScmCommitLogByVersionAndCompanyId(scmToken.companyId, commit.id);
                if (null != commitLog) {
                    continue;
                }
                ScmCommitLog log = new ScmCommitLog();
                log.branch = branch;
                log.email = commit.author.email;
                log.scmTokenId = scmToken.id;
                log.companyId = scmToken.companyId;
                log.uuid = BizUtil.randomUUID();
                log.changed = getChanged(commit);
                if (hook.body.repository != null) {
                    log.repo = hook.body.repository.name;
                } else {
                    log.repo = "";
                }

                log.comment = commit.message;
                if (commit.author != null) {
                    log.author = commit.author.name;
                }
                log.version = commit.id;

                log.commitTime = DateUtil.formatDate(DateUtil.parseUTCDate(commit.timestamp));
                dao.add(log);
                //
                addTaskScmCommitLogs(log);
            }
            //
        }

        //
        @Override
        public String gitlabPostCommitHook(String companyUuid) {
            Company company = dao.getCompanyInfoByUuid(companyUuid);
            ScmToken scmToken = dao.getScmTokenByCompanyIdType(company.id, ScmToken.TYPE_GITLAB);
            if (scmToken == null) {
                scmToken = new ScmToken();
                scmToken.companyId = company.id;
                scmToken.type = ScmToken.TYPE_GITLAB;
                scmToken.token = BizUtil.randomUUID();
                dao.add(scmToken);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("#!/bin/sh\n");
            sb.append("export LANG='en_US.UTF-8'\n");
            sb.append("while read old new ref\n");
            sb.append("do\n");
            sb.append("\tuserName=`git log -1 --format=format:%cn HEAD`\n");
            sb.append("\tversionUuid=`git log -1 --format=format:%H HEAD`\n");
            sb.append("\tdiff=`git diff --shortstat $old $new`\n");
            sb.append("\tchanged=`git diff --name-status $old $new`\n");
            sb.append("\tcomment=`git log --pretty=%B -n 1 \"$new\"`\n");
            sb.append("\tcontent=\"`echo $ref|base64`#`echo $userName|base64`#`echo $comment|base64`#`echo $diff|base64`#`echo $changed|base64`#`echo $new|base64`\"\n");
            sb.append("\tcurl -s -d \"${content}\"  -H 'Content-Type: application/text' " + GlobalConfig.webUrl + "p/webapi/gitlab_post_receive/" + scmToken.token + "\n");
            sb.append("done\n");
            sb.append("exit 0");
            return sb.toString();
        }

        //
        @Override
        @Transaction
        public void gitlabPostReceive(String token, List<String> items) {
            ScmToken scmToken = dao.getExistedScmTokenByToken(token);
            try {
                String version = items.get(5);
                String diff = items.get(3);
                ScmCommitLog commitLog = dao.getScmCommitLogByVersionAndCompanyId(scmToken.companyId, version);
                if (null != commitLog) {
                    //更新代码统计行数
                    if (!StringUtil.isEmpty(diff)) {//1 file changed, 3 insertions(+), 1 deletion(-)
                        commitLog.addLineNum = getGitAddLineNum(diff);
                        commitLog.decreaseLineNum = getGitDeleteLineNum(diff);
                    }
                    dao.updateGitlabScmCommitLogNum(commitLog);
                }else{
                    //2020年10月19日 需要加上重试机制，可能出现在统计时关联数据未生成的情况
                    ScmRetry scmRetry = new ScmRetry();
                    scmRetry.companyId = scmToken.companyId;
                    scmRetry.times=1;
                    scmRetry.items = items;
                    webApiService.toQueue(scmRetry);
                }
//                else {
//                    ScmCommitLog log = new ScmCommitLog();
//                    log.companyId = scmToken.companyId;
//                    log.scmTokenId = scmToken.id;
//                    log.repo = items.get(0);
//                    log.author = items.get(1);
//                    log.comment = items.get(2);
//                    log.diff = diff;
//                    //换行处理
//                    log.changed = getChanged(items.get(4));
//                    log.version = version;
//                    if (!StringUtil.isEmpty(log.diff)) {//1 file changed, 3 insertions(+), 1 deletion(-)
//                        log.addLineNum = getGitAddLineNum(log.diff);
//                        log.decreaseLineNum = getGitDeleteLineNum(log.diff);
//                    }
//                    if (log.diff != null && log.diff.length() > 1024) {
//                        log.diff = log.diff.substring(0, 1024);
//                    }
//                    log.uuid = BizUtil.randomUUID();
//                    dao.add(log);
//                    //
//                    addTaskScmCommitLogs(log);
//                }
            } catch (Exception e) {
                logger.error("gitlab post receive ERROR", e);
            }
        }

        private int getGitAddLineNum(String diff) {
            if (StringUtil.isEmpty(diff)) {
                return 0;
            }
            String[] contents = diff.split(",");
            if (contents.length < 2) {
                return 0;
            }
            if (contents[1] == null) {
                return 0;
            }
            contents = contents[1].trim().split(" ");
            if (contents.length < 1) {
                return 0;
            }
            return Integer.valueOf(contents[0].trim());
        }

        private int getGitDeleteLineNum(String diff) {
            if (StringUtil.isEmpty(diff)) {
                return 0;
            }
            String[] contents = diff.split(",");
            if (contents.length < 3) {
                return 0;
            }
            if (contents[2] == null) {
                return 0;
            }
            contents = contents[2].trim().split(" ");
            if (contents.length < 1) {
                return 0;
            }
            return Integer.valueOf(contents[0].trim());
        }

        //
        private void addTaskScmCommitLogs(ScmCommitLog log) {
            try {
                log.diff = "";
                if (!StringUtil.isEmpty(log.comment)) {
                    List<String> ids = PatternUtil.matchs(log.comment, "\\#([0-9]{1,10})");
                    logger.info("ScmCommitLogId:{} ids:{}", log.id, ids);
                    if (!BizUtil.isNullOrEmpty(ids)) {
                        for (String serialNo : ids) {
                            TaskInfo task = dao.getExistedTaskBySerialNo(log.companyId, serialNo);
                            if (task == null || task.isDelete) {
                                continue;
                            }
                            //20200731 结束状态的任务不推送关联代码通知
                            if (task.statusType == ProjectStatusDefine.TYPE_结束状态) {
                                continue;
                            }
                            TaskScmCommit bean = new TaskScmCommit();
                            bean.companyId = task.companyId;
                            bean.scmCommitLogId = log.id;
                            bean.taskId = task.id;
                            dao.add(bean);
                            //
                            Account account = new Account();
                            account.email = log.email;
                            account.companyId = task.companyId;
                            account.name = log.author != null ? log.author.trim() : "";//name or email
                            if (!StringUtil.isEmpty(account.name)) {
                                if (!BizUtil.isNullOrEmpty(account.name)) {
                                    Account account2 = dao.getCompanyAccountByUserNameOrEmail(task.companyId, account.name);
                                    if (null == account2) {
                                        if (!BizUtil.isNullOrEmpty(account.email)) {
                                            account2 = dao.getCompanyAccountByUserNameOrEmail(task.companyId, account.email);
                                        }
                                    }
                                    if (account2 != null && account2.companyId == task.companyId) {
                                        account = account2;
                                    }
                                }
                            }
                            //
                            bizService.addChangeLog(account, 0, task.id,
                                    ChangeLog.TYPE_新增SCM提交记录, JSONUtil.toJson(log));

                            SvnCommitChangeLog svnCommitLog = SvnCommitChangeLog.createSvnCommitLog(task, log);
                            bizService.addChangeLog(account, task.projectId, 0,//项目动态
                                    ChangeLog.TYPE_新增SCM提交记录, JSONUtil.toJson(svnCommitLog));
                            Map<String, Object> map = new HashMap<>();
                            map.put("scmCommitLog", log);

                            bizService.sendNotificationForTask(account,
                                    task,
                                    AccountNotificationSetting.TYPE_对象提交代码,
                                    "对象提交代码",
                                    map);
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        @Override
        public String svnPreCommit(String token, String comment) {
            if (token == null) {
                return "token cannot be null";
            }
            ScmToken bean = dao.getDomain(ScmToken.class, QueryWhere.create().where("token", token));
            if (bean == null) {
                return "token expired";
            }
            if (StringUtil.isEmpty(comment)) {
                return "commit message format ：#TASKID";
            }
            if (comment.indexOf("--no-check") != -1) {//不检查
                return "SUCCESS";
            }
            if (comment.indexOf(" ") == -1) {
                logger.warn("comment.indexOf(\" \")==-1 comment:{}", comment);
                return "can not find account";
            }
            String author = comment.substring(0, comment.indexOf(" ")).trim();
            Account account = dao.getAccountByUserNameOrEmail(author);
            if (account == null) {
                logger.warn("account not found author:{}", author);
                return "can not find account";
            }
            List<String> ids = PatternUtil.matchs(comment, "\\#([0-9]{1,10})");
            logger.info("ScmCommitLogId:ids:{} author:{}", ids, author);
            if (ids == null || ids.size() == 0) {
                logger.warn("task not found comment:{}", comment);
                return "commit message format ：#TASKID";
            }
            String serialNo = ids.get(0);
            Task task = dao.getTaskByCompanyIdSerialNo(account.companyId, serialNo);
            if (task == null || task.isDelete) {
                logger.warn("task not found or delete companyId:{} serialNo:{}", account.companyId, serialNo);
                return "can not find task #" + serialNo + "";
            }
            if (task.isFinish) {
                logger.warn("task isFinish taskId:{}", task.id);
                return "task #" + serialNo + " already finished";
            }
            Project project = dao.getExistedById(Project.class, task.projectId);
            if (project.status != Project.STATUS_运行中) {
                logger.warn("task project is not running{}", project.id);
                return "task #" + serialNo + " project is not running";
            }
            if (!BizUtil.contains(task.ownerAccountIdList, account.id)) {
                logger.warn("task owner is not you taskId:{} accountId:{}",
                        task.id, account.id);
                return account.userName + " is not owner of task #" + serialNo;
            }
            return "SUCCESS";
        }

        @Override
        public String getArtifactUuid(String name, String version) {
            return bizService.getArtifactUuid(name, version);
        }

        @Override
        public ProjectArtifactInfo getProjectArtifactByUuid(String uuid) {
            return dao.getExistedProjectArtifactInfoByUuid(uuid);
        }


        @Transaction
        @Override
        public void githubWebhook(String token, GithubWebhook hook) {
            ScmToken scmToken = dao.getExistedScmTokenByToken(token);
            if (hook.body.commits == null || hook.body.commits.size() == 0) {
                return;
            }
            String mySignature = "sha1=" + HmacSha1.hmacSha1(hook.reqBody, token);
            if (!mySignature.equalsIgnoreCase(hook.signature)) {
                logger.warn("signature not match token:{} mySignature:{} signature:{} reqBody:{}",
                        token, mySignature, hook.signature, hook.reqBody);
                return;
            }
            String userEmail = hook.body.head_commit.committer.email;
            String projectName = hook.body.repository.name;
            String branch = null;
            String ref = hook.body.ref;
            if (!BizUtil.isNullOrEmpty(ref)) {
                branch = ref.substring(ref.lastIndexOf("/") + 1);
            }
            if (!BizUtil.isNullOrEmpty(branch)) {
                if (!checkScmBranch(scmToken.companyId, branch, projectName)) {
                    return;
                }
            }
            //
            for (GithubWebhookCommit e : hook.body.commits) {
                ScmCommitLog log = new ScmCommitLog();
                log.email = userEmail;
                log.branch = branch;
                log.scmTokenId = scmToken.id;
                log.companyId = scmToken.companyId;
                log.uuid = BizUtil.randomUUID();
                log.changed = getChanged(e);
                log.repo = hook.body.repository.name;
                log.comment = e.message;
                log.author = e.author.name;
                log.version = e.id;
                log.commitTime = DateUtil.formatDate(DateUtil.parseUTCDate(e.timestamp));
                dao.add(log);
                //
                addTaskScmCommitLogs(log);
            }
            //
        }

        //
        private String getChanged(GitlabWebhookCommit e) {
            StringBuilder sb = new StringBuilder();
            if (e.added != null && e.added.size() > 0) {
                for (String add : e.added) {
                    sb.append("A\t" + add).append("\n");
                }
            }
            if (e.modified != null && e.modified.size() > 0) {
                for (String modified : e.modified) {
                    sb.append("U\t" + modified).append("\n");
                }
            }
            if (e.removed != null && e.removed.size() > 0) {
                for (String removed : e.removed) {
                    sb.append("D\t" + removed).append("\n");
                }
            }
            return sb.toString();
        }

        private String getChanged(GithubWebhookCommit e) {
            StringBuilder sb = new StringBuilder();
            if (e.added != null && e.added.size() > 0) {
                for (String add : e.added) {
                    sb.append("A\t" + add).append("\n");
                }
            }
            if (e.modified != null && e.modified.size() > 0) {
                for (String modified : e.modified) {
                    sb.append("U\t" + modified).append("\n");
                }
            }
            if (e.removed != null && e.removed.size() > 0) {
                for (String removed : e.removed) {
                    sb.append("D\t" + removed).append("\n");
                }
            }
            return sb.toString();
        }

        //gitlab diff日志格式化 M page1.vue M page2.vue A page3.java
        private static String getChanged(String diff) {
            if (BizUtil.isNullOrEmpty(diff)) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            boolean cat = false;
            char[] chars = diff.toCharArray();
            char temp = '0';
            for (char ch : chars) {
                if ('A' == ch) {
                    if (cat) {
                        sb.append(temp);
                    }
                    temp = ch;
                    cat = true;
                } else if ('M' == ch) {
                    if (cat) {
                        sb.append(temp);
                    }
                    temp = ch;
                    cat = true;
                } else if ('U' == ch) {
                    if (cat) {
                        sb.append(temp);
                    }
                    temp = ch;
                    cat = true;
                } else if ('D' == ch) {
                    if (cat) {
                        sb.append(temp);
                    }
                    temp = ch;
                    cat = true;
                } else {
                    if (cat) {
                        if (Character.isSpaceChar(ch)) {
                            if (first) {
                                sb.append(temp);
                                first = false;
                            } else {
                                sb.append("\n").append(temp);
                            }
                            sb.append(ch);
                        } else {
                            sb.append(temp).append(ch);
                        }
                    } else {
                        sb.append(ch);
                    }
                    cat = false;
                }
            }
            return sb.toString();
        }


        /**
         * 通过ID查询webApi
         */
        @SuppressWarnings("unused")
        @Override
        public WebApiInfo getWebApiById(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            return dao.getById(WebApiInfo.class, id);
        }

        /**
         * 查询webApi列表和总数
         */
        @Override
        public Map<String, Object> getWebApiList(String token, WebApiQuery query) {
            Account account = bizService.getExistedAccountByToken(token);
            setupQuery(account, query);
            return createResult(dao.getList(query), dao.getListCount(query));
        }

        /**
         * 新增webApi
         */
        @Transaction
        @Override
        public int addWebApi(String token, WebApiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置);
            bean.createAccountId = account.id;
            bean.companyId = account.companyId;
            bean.status = DataTableInfo.STATUS_有效;
            bean.apiKey = BizUtil.randomUUID();
            BizUtil.checkValid(bean);
            JavaScriptWebApi webApi = new JavaScriptWebApi(bean.script);
            bean.webApiDefine = webApi.getWebApiDefine();
            dao.add(bean);
            bizService.addOptLog(account, bean.id, bean.name, OptLog.EVENT_ID_新增WEBAPI, "name:" + bean.name);
            return bean.id;
        }

        /**
         * 编辑webApi
         */
        @Transaction
        @Override
        public void updateWebApi(String token, WebApiInfo bean) {
            Account account = bizService.getExistedAccountByToken(token);
            WebApi old = dao.getExistedByIdForUpdate(WebApi.class, bean.id);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置, old.companyId);
            old.name = bean.name;
            //
//			if(!old.script.equals(bean.script)) {//变更记录
//				DataTableLog log=new DataTableLog();
//				log.dataTableId=bean.id;
//				log.companyId=bean.companyId;
//				log.name=bean.name;
//				log.group=bean.group;
//				log.roles=bean.roles;
//				log.enableRole=bean.enableRole;
//				log.status=bean.status;
//				log.isDelete=bean.isDelete;
//				log.script=bean.script;
//				log.remark=bean.remark;
//				log.dataTableDefine=bean.dataTableDefine;
//				log.createAccountId=account.id;
//				dao.add(log);
//			}
            //
            old.script = bean.script;
            old.remark = bean.remark;
            old.group = bean.group;
            old.updateAccountId = account.id;
            BizUtil.checkValid(old);
            //
            JavaScriptWebApi webApi = new JavaScriptWebApi(bean.script);
            old.webApiDefine = webApi.getWebApiDefine();
            dao.update(old);
            //
            bizService.addOptLog(account, old.id, old.name,
                    OptLog.EVENT_ID_编辑WEBAPI, "name:" + old.name);
        }

        @Transaction
        @Override
        public void updateWebApiStatus(String token, int id, int status) {
            Account account = bizService.getExistedAccountByToken(token);
            WebApi old = dao.getExistedByIdForUpdate(WebApi.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置, old.companyId);
            old.updateAccountId = account.id;
            old.status = status;
            BizUtil.checkValid(old);
            dao.update(old);
        }

        /**
         * 删除webApi
         */
        @Transaction
        @Override
        public void deleteWebApi(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WebApi old = dao.getExistedByIdForUpdate(WebApi.class, id);
            if (old.isDelete) {
                return;
            }
            bizService.checkCompanyPermission(account, Permission.ID_系统设置, old.companyId);
            old.isDelete = true;
            old.updateAccountId = account.id;
            dao.update(old);
            //
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除WEBAPI, "name:" + old.name);
        }

        @Override
        @Transaction
        public String runWebApi(String apiKey, WebApiReq req) {
            WebApi old = dao.getWebApiByApiKey(apiKey);
            if (old.isDelete || old.status != WebApi.STATUS_有效) {
                throw new AppException("接口不存在或已删除");
            }
            JavaScriptWebApi webApi = new JavaScriptWebApi(old.script);
            webApi.req = req;
            return webApi.run(ConstDefine.GLOBAL_KEY);
        }

        @Transaction
        @Override
        public void refreshWebApiKey(String token, int id) {
            Account account = bizService.getExistedAccountByToken(token);
            WebApi old = dao.getExistedByIdForUpdate(WebApi.class, id);
            bizService.checkCompanyPermission(account, Permission.ID_系统设置, old.companyId);
            old.apiKey = BizUtil.randomUUID();
            dao.update(old);
            bizService.addOptLog(account, old.id, old.name, OptLog.EVENT_ID_删除WEBAPIKEY, "name:" + old.name);
        }
    }

}
