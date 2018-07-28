package sorm.util;
/**
 * 封装了字符串常用的操作
 * @author 隋鸿浩
 *
 */
public class StringUtils {
	/**
	 * 将目标字符串首字母变成大写
	 * 
	 * @return
	 */
	public static String firstChar2UpperCase(String str) {
		//abcd-->Abcd
		return str.toUpperCase().substring(0, 1)+str.substring(1);
	}
	
	
}
