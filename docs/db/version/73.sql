insert into t_app_version_change_log(version) values (73);
update t_app_version set version=73;

-- 添加修改开始键截止时间权限
drop procedure if exists add_system_field_start_end_time;
DELIMITER ;;
CREATE PROCEDURE add_system_field_start_end_time()
BEGIN
	DECLARE _object_type_id int;
	DECLARE s int DEFAULT 0;
	DECLARE cur1 CURSOR FOR select id from t_object_type where id not  in (1001,1002);
DECLARE CONTINUE HANDLER FOR NOT FOUND SET s=1;
open cur1;
fetch cur1 into _object_type_id;
while s<>1 do
        if NOT EXISTS (select 1 from `t_permission` WHERE id = concat('task_edit_startend_date_',_object_type_id)) then
 		    INSERT INTO `t_permission` (`id`, `type`, `name`, `parent_id`, `is_data_permission`, `is_member_permission`, `company_version`, `object_type`, `sort_weight`, `create_time`, `update_time`)
            VALUES
	        (concat('task_edit_startend_date_',_object_type_id), 1, '修改开始截止时间',concat('task_',_object_type_id) , 0, 1, 0, _object_type_id, 15,now(),now());
        end if;
fetch cur1 into _object_type_id;
end while;
close cur1;
END;;

DELIMITER ;

call add_system_field_start_end_time();