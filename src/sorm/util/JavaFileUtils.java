package sorm.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sorm.bean.ColumnInfo;
import sorm.bean.JavaFieldGetSet;
import sorm.bean.TableInfo;
import sorm.core.DBManager;
import sorm.core.MysqlTypeConvertor;
import sorm.core.TableContext;
import sorm.core.TypeConvertor;

/**
 * ��װ��java�ļ����ò���
 * @author ����
 *
 */
public class JavaFileUtils {
	
	/**
	 * �����ֶ���Ϣ����java������Ϣ:varchar username --> private String name;�Լ���Ӧ��setget����
	 * @param column �ֶ���Ϣ
	 * @param convertor ����ת����
	 * @return Java���Ժ�setget����Դ��
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column, TypeConvertor convertor) {
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		
		String javaFieldType = convertor.datebaseType2JavaType(column.getDataType());
		//ͨ��ƴ���ַ�������java����
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");
		
		//public String getUsername(){return username;}
		StringBuilder getSrc = new StringBuilder();
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName()+"(){\n"));
		getSrc.append("\t\treturn "+column.getName()+";\n"+"\t}\n");
		jfgs.setGetInfo(getSrc.toString());
		
		//public void setUsername(String username){this.username = username;}
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"(");
		setSrc.append(javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n"+"\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		
		return jfgs;
	} 
	
	/**
	 * ���ݱ���Ϣ����java���Դ����
	 * @param ti ����Ϣ
	 * @param tc �������ת����
	 * @return java���Դ����
	 */
	public static String createJavaSrc(TableInfo ti, TypeConvertor tc){

		Map<String,ColumnInfo> map = ti.getColumns();
		List<JavaFieldGetSet> lists = new ArrayList<JavaFieldGetSet>();
		for(ColumnInfo c : map.values()) {
			lists.add(createFieldGetSetSRC(c, tc));
		}
		
		//ƴ�Ӹ������
		StringBuilder src = new StringBuilder();
		//package
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		//import
		
		src.append("import java.sql.*;\n");
		src.append("import java.util.*;\n\n");
		//���������
		src.append("public class "+StringUtils.firstChar2UpperCase(ti.getName()+" {\n\n"));
		
		
		//�����б�
		for(JavaFieldGetSet f : lists) {
			src.append(f.getFieldInfo());
		}
		src.append("\n\n");
		
		//set get ����
		for(JavaFieldGetSet f : lists) {
			src.append(f.getGetInfo());
		}
		src.append("\n\n");
		
		for(JavaFieldGetSet f : lists) {
			src.append(f.getSetInfo());
		}
		src.append("\n\n");
		
		
		//����
		src.append("}\n");
		//System.out.println(src);
		return src.toString();
	}
	
	public static void createJavaPOFile(TableInfo ti, TypeConvertor tc) {
		String src = createJavaSrc(ti,tc);
		BufferedWriter bw = null;
		String srcPath = DBManager.getConf().getSrcPath()+"\\";
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "\\\\");
		
		File f = new File(srcPath + packagePath);
		//System.out.println(f.getAbsolutePath());
		if(!f.exists()) {//ָ��Ŀ¼������ ��һ��
			f.mkdirs();
		}
		
		try {
			bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstChar2UpperCase(ti.getName())+".java"));
			bw.write(src);
			System.out.println("������"+ti.getName()+"��Ӧ��java��"+StringUtils.firstChar2UpperCase(ti.getName())+".java");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(bw != null) {
					bw.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("id", "int", 0);
//		JavaFieldGetSet jfgs = createFieldGetSetSRC(ci,new MysqlTypeConvertor());
//		System.out.println(jfgs);
		
		Map<String,TableInfo> map = TableContext.tables;
		for(TableInfo ti : map.values()) {
			
			createJavaPOFile(ti,new MysqlTypeConvertor());
		}
		
	}
}
