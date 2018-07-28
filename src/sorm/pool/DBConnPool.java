package sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sorm.core.DBManager;

/**
 * ���ӳص���
 * @author ����
 *
 */
public class DBConnPool {
	/**
	 * �������ӳ�
	 */
	public static List<Connection> pool;
	/**
	 * ���������
	 */
	public static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
	/**
	 * ��С������
	 */
	public static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();
	
	
	public void initPool() {
		if(pool == null) {
			pool = new ArrayList<Connection>();
		}
		while(pool.size()<DBConnPool.POOL_MIN_SIZE) {
			pool.add(DBManager.createConn());
			System.out.println("���ӳ���"+pool.size());
		}
	}
	/**
	 * �ӳ���ȡ��һ������
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
