package cornerstone.biz.poi;

import java.lang.annotation.*;

/**
 * 
 * @author cs
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelCell {

	String name() default "";
	
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";
	
	boolean hyperLink() default false;
}
