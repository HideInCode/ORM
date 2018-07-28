package sorm.core;
/**
 * 负责java数据类型和数据库数据类型的相互转换
 * @author 隋鸿浩
 *
 */
public interface TypeConvertor {
	
	/**
	 * 将数据库数据类型转化为Java的数据类型
	 * @param columnType 数据库字段的数据类型
	 * @return Java的数据类型
	 */
	public String datebaseType2JavaType(String columnType);
	
	/**
	 * 将java数据类型转化成数据库数据类型
	 * @param javaDataType
	 * @return
	 */
	public String javaType2DatabaseType(String javaDataType);
}
