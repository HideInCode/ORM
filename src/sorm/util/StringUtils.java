package sorm.util;
/**
 * ��װ���ַ������õĲ���
 * @author ����
 *
 */
public class StringUtils {
	/**
	 * ��Ŀ���ַ�������ĸ��ɴ�д
	 * 
	 * @return
	 */
	public static String firstChar2UpperCase(String str) {
		//abcd-->Abcd
		return str.toUpperCase().substring(0, 1)+str.substring(1);
	}
	
	
}
