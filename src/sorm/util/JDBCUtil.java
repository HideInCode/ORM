package sorm.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装jdbc查询常用的操作
 * @author 隋鸿浩
 *
 */
public class JDBCUtil {
	/**
	 * 给sql设置参数
	 * @param ps
	 * @param params
	 */
	public static void handleParams(PreparedStatement ps,Object[] params) {
		if(params != null) {
			for(int i=0; i<params.length; i++) {
				try {
					ps.setObject(i+1, params[i]);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
