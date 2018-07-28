package sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import po.Emp;
import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.util.JDBCUtil;
import sorm.util.ReflectUtils;
import sorm.util.StringUtils;
import vo.EmpVO;
/**
 * mysql数据库的查询
 * @author 隋鸿浩
 *
 */
public class MysqlQuery extends Query{

	@Override
	public Object queryPagenate(int pageName, int size) {
		// TODO Auto-generated method stub
		return null;
	}

}
