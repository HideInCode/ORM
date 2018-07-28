package sorm.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ��װ�˷��䳣�õĲ���
 * @author ����
 *
 */
public class ReflectUtils {
	
	/**
	 * ����obj�����Ӧ����������get����
	 * @param c
	 * @param fieldname
	 * @param obj
	 * @return
	 */
	public static Object invokeGet(String fieldname, Object obj) {
		//ͨ��������� �������Զ�Ӧ��set get����
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
