package sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sorm.core.DBManager;

/**
 * 连接池的类
 * @author 隋鸿浩
 *
 */
public class DBConnPool {
	/**
	 * 建立连接池
	 */
	public static List<Connection> pool;
	/**
	 * 最大连接数
	 */
	public static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
	/**
	 * 最小连接数
	 */
	public static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();
	
	
	public void initPool() {
		if(pool == null) {
			pool = new ArrayList<Connection>();
		}
		while(pool.size()<DBConnPool.POOL_MIN_SIZE) {
			pool.add(DBManager.createConn());
			System.out.println("连接池数"+pool.size());
		}
	}
	/**
	 * 从池中取出一个连接
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size()-1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		return conn;
	}
	
	public synchronized void close(Connection conn) {
		if(pool.size()>=POOL_MAX_SIZE) {
			try {
				
				if(conn != null) {
					
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		pool.add(conn);
	}
	
	public DBConnPool(){
		initPool();
	}
}
