package cornerstone.biz.util;

import jazmin.core.app.AppException;
import jazmin.log.Logger;
import jazmin.log.LoggerFactory;
import jazmin.util.JSONUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * 
 * @author cs
 *
 */
public class BeanUtil {
	private static Logger logger = LoggerFactory.get(BeanUtil.class);

	//
	public static <T> List<T> copyToList(Class<T>toClass,List<?>fromBeanList){
		List<T>result=new ArrayList<>();
		if(fromBeanList==null) {
			return result;
		}
		for(Object o:fromBeanList){
			result.add(copyTo(toClass,o));
		}
		return result;
	}
	//
	public static  <T> T copyTo(Class<T>toClass,Object from){
		try {
			T to=null;
			to =  toClass.newInstance();
			Field[] ff = getFields(from.getClass());
			for(Field f:ff){
				if(Modifier.isFinal(f.getModifiers())){
					continue;
				}
				try{
					Field toField = getField(toClass, f.getName());
					if(null==toField){
						continue;
					}
					f.setAccessible(true);
					Object fv=f.get(from);
					toField.setAccessible(true);
					toField.set(to,fv);
				}catch (Exception e) {
					logger.error("copyTo ERROR",e);
				}
			}
			return to;
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	//
	//
	public static Field[] getFields(Class<?>clazz){
		List<Field> fieldList = new ArrayList<Field>();
		for(;clazz != Object.class;clazz = clazz.getSuperclass()) {
			Field[] fields = clazz.getDeclaredFields();
			fieldList.addAll(Arrays.asList(fields));
		}
		return fieldList.toArray(new Field[0]);
	}
	//
	public static Field getField(Class<?>clazz,String fieldName)  {
		Field field = null ;  
        for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {  
            try {  
                field = clazz.getDeclaredField(fieldName) ;  
                return field ;  
            } catch (Exception e) {
            	logger.warn("get Field warn :{}",e.getMessage());
            }   
        }
        return field;
	}
	
	/**
	 * 
	 * @param f
	 * @param strValue
	 * @return
	 */
	public static Object fromJson(Field f,String strValue) {
		Object value=null;
		Class<?> fieldType = f.getType();
		if(strValue!=null){
			Type genericType=f.getGenericType();
			if ( genericType instanceof ParameterizedType ) {  
				 Type[] typeArguments = ((ParameterizedType)genericType).getActualTypeArguments();  
				 if(typeArguments.length==1) {
					 if(List.class.isAssignableFrom(fieldType) && (typeArguments[0] instanceof Class)) {
						 value=JSONUtil.fromJsonList(strValue,(Class<?>) typeArguments[0]);
					 }
				 }
			 }else {
				 value=JSONUtil.fromJson(strValue,fieldType);
			 }
		}
		return value;
	}
}
