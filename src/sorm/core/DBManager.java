package sorm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import sorm.bean.Configuration;
import sorm.pool.DBConnPool;

/**
 * ����������Ϣ,ά�����Ӷ���Ĺ���
 * ����������ļ�ͨ�����������
 * @author ����
 *
 */
public class DBManager {
	private static Configuration conf;
	private static DBConnPool dbcp;
	
	static {//��̬�����ִֻ��һ��
		Properties pros = new Properties();
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		conf = new Configuration();
		conf.setDriver(pros.getProperty("driver"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setQueryClass(pros.getProperty("queryClass"));
		conf.setPoolMaxSize(Integer.parseInt(pros.getProperty("poolMaxSize")));
		conf.setPoolMinSize(Integer.parseInt(pros.getProperty("poolMinSize")));
		
		//���ر����Ϣ
		System.out.println(TableContext.class);
	}
	
	public static Connection getConn(){
		if(dbcp == null) {
			dbcp = new DBConnPool();
		}
		return dbcp.getConnection();
	}
	
	public static Connection createConn(){
		try {
			Class.forName(conf.getDriver());
			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());
	
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void close(ResultSet rs,Statement ps,Connection conn){
		try {
			if(rs!=null){
				rs.close();
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			if(ps!=null){
				ps.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		dbcp.close(conn);
		}
		public static void close(Statement ps,Connection conn){
			try {
					if(ps!=null){
						ps.close();
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
//				if(conn!=null){
//					conn.close();
//				}
				dbcp.close(conn);
		
		}
		public static void close(Connection conn){
			dbcp.close(conn);
		}
		
		public static Configuration getConf(){
			return conf;
		}
}
