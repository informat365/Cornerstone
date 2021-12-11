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
@Target(ElementType.TYPE)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented
@Inherited  
public @interface DomainDefineValid {
	//
	public @interface UniqueKey {
		  public String[] fields() default {};
	}
	/**注释*/
	String comment();
	
	/**授权查询*/
	Class<?>[] authorizeForQuery() default {};
	
	/**唯一索引列表*/
	UniqueKey[] uniqueKeys() default {};//uniqueKeys={@UniqueKey(fields={"userId"})}
	
	
}
