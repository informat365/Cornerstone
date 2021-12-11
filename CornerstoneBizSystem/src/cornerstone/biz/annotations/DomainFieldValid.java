package cornerstone.biz.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author cs
 *
 */
@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented
@Inherited  
public @interface DomainFieldValid {
	/**注释*/
	public String comment();
	
	/**必选*/
	public boolean required() default false;
	
	/**最小值(如果是字符串就是最小长度)*/
	public int minValue() default -1;
	
	/**最大值(如果是字符串就是最大长度)*/
	public int maxValue() default -1;
	
	/**能否更新(boss后台更新)*/
	public boolean canUpdate() default false;
	
	/**数据字典*/
	public String dataDict() default "";
	
	/**是否需要trim*/
	public boolean needTrim() default false;
	
	/**是否需要敏感词过滤*/
	public boolean needSensiFilter() default false;
}
