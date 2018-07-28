package sorm.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * ��װjdbc��ѯ���õĲ���
 * @author ����
 *
 */
public class JDBCUtil {
	/**
	 * ��sql���ò���
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
