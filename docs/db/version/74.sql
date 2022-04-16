ALTER TABLE `t_category`
    ADD COLUMN `is_hide` tinyint(1) NULL COMMENT '是否隐藏' AFTER `sort_weight`;

update t_category set `is_hide` = 0 limit 1000;
update t_category set `is_hide` = 0 limit 1000;
update t_category set `is_hide` = 0 limit 1000;
update t_category set `is_hide` = 0 limit 1000;
update t_category set `is_hide` = 0;
ALTER TABLE `t_category`
    MODIFY COLUMN `is_hide` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏' AFTER `sort_weight`;

INSERT INTO `t_config` (`name`, `value`, `value_type`, `description`) VALUES ('system.category.hideSwitch', 'false','Boolean', '分类隐藏开关');
INSERT INTO `t_config` (`name`, `value`, `value_type`, `description`) VALUES ('task.retainFormOnCreate', 'false','Boolean', '保留任务创建数据');

insert into t_app_version_change_log(version) values (74);
update t_app_version set version=74;
