package sorm.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 封装了反射常用的操作
 * @author 隋鸿浩
 *
 */
public class ReflectUtils {
	
	/**
	 * 调用obj对象对应的属性名的get方法
	 * @param c
	 * @param fieldname
	 * @param obj
	 * @return
	 */
	public static Object invokeGet(String fieldname, Object obj) {
		//通过反射机制 调用属性对应的set get方法
				try {
					Class c = obj.getClass();
					Method m = c.getDeclaredMethod("get"+StringUtils.firstChar2UpperCase(fieldname), null);
					return m.invoke(obj, null);
					
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				} 
	}
	
	public static void invokeSet(Object obj, String columnName,Object columnValue) {
		try {
			Class clazz = obj.getClass();
			Method m = clazz.getDeclaredMethod("set"+StringUtils.firstChar2UpperCase(columnName), columnValue.getClass());
			m.invoke(obj, columnValue);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
