package sorm.core;
/**
 * ����java�������ͺ����ݿ��������͵��໥ת��
 * @author ����
 *
 */
public interface TypeConvertor {
	
	/**
	 * �����ݿ���������ת��ΪJava����������
	 * @param columnType ���ݿ��ֶε���������
	 * @return Java����������
	 */
	public String datebaseType2JavaType(String columnType);
	
	/**
	 * ��java��������ת�������ݿ���������
	 * @param javaDataType
	 * @return
	 */
	public String javaType2DatabaseType(String javaDataType);
}
