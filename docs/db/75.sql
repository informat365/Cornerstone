insert into t_app_version_change_log(version) values (75);
update t_app_version set version=75;

INSERT INTO `t_config` (`name`, `value`, `value_type`, `description`) VALUES ('sso.config', '{"successStatus":"200","method":"POST","mappings":{"info": "mobileNo","successCodeKey":"code","successCode":"200","resultKey":"mobileNo"}, "url":"","headers":{"Content-Type":"application/json"}}','String', 'sso登录配置');
INSERT INTO `t_config` (`name`, `value`, `value_type`, `description`) VALUES ('sso.loginField', 'MobileNo','String', '登录字段的key值');
