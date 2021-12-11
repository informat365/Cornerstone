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
public @interface DomainRelation {

	Class<?> domainClass();
	
	String fieldName();
	
	String relationRightFieldName() default "";
	
	Class<?> targetDomainClass() default void.class;
	
	String targetFieldName() default "id";
	
	Class<?> targetQueryDomainClass() default void.class;
	
}
