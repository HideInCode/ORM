package sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.util.JDBCUtil;
import sorm.util.ReflectUtils;

/**
 * 负责查询对外提供核心的类
 * 模板方法模式重构过
 * @author 隋鸿浩
 *
 */
public abstract class Query implements Cloneable{
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back) {


		Connection con = DBManager.getConn();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = con.prepareStatement(sql);
			JDBCUtil.handleParams(ps, params);
//			System.out.println(ps);
			rs = ps.executeQuery();
			
			
			//只需要改写这一个部分就好了
			return back.doExecute(con, ps, rs);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DBManager.close(ps, con);
		}
	
	
	}
	
	/**
	 * 执行DML语句
	 * @param sql
	 * @param params
	 * @return 执行sql语句后影响记录的行数
	 */
	public int executeDML(String sql, Object[] params) {

		Connection con = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(sql);
			
			JDBCUtil.handleParams(ps, params);
			
			count = ps.executeUpdate();			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBManager.close(ps, con);
		}
		return count;
	
	}
	
	/**
	 * 将对象存储 到数据库中
	 * 把对象
	 * @param obj 要存储的对象
	 */
	public void insert(Object obj){

		//obj-->表中 insert into 表明 (id, name,pwd) values (?,?,?)
		Class c = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("insert into "+ti.getName()+" (");
		Field[] fs = c.getDeclaredFields();
		List<Object> params = new ArrayList<Object>();//存放参数
		
		for(Field elem : fs) {
			String fieldName = elem.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
			
			if(fieldValue != null) {
				sql.append(fieldName+",");
				params.add(fieldValue);
			}
			
		}
		
		sql.setCharAt(sql.length()-1, ')');
		sql.append("values (");
		for(int i=0;i<params.size();i++) {
			sql.append("?,");
		}
		sql.setCharAt(sql.length()-1, ')');
		executeDML(sql.toString(), params.toArray());
	
	}
	
	/**
	 * 删除clazz类对应的表中的记录(指定主键值id的记录)
	 * @param clazz 跟表对应的类的对象
	 * @param id 主键的值
	 */
	public void delete(Class clazz, Object id){

		//Emp.class,2 --> delete form emp where id =2
		
		//一直对象找tableInfo
		TableInfo ti = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = ti.getOnlyPriKey();
		
		String sql = "delete from "+ti.getName()+" where "+onlyPriKey.getName()+"=? ";
		executeDML(sql, new Object[] {id});
	
	}
	
	/**
	 * 删除数据库中的对应记录 
	 * @param obj
	 */
	public void delete(Object obj){

		Class clazz = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = ti.getOnlyPriKey();
		
		//通过反射机制 调用属性对应的set get方法
	
		
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		delete(clazz, priKeyValue);
		
	
	}
	
	/**
	 * 根据对象对应的记录,并且只更新指定字段的值
	 * @param obj
	 * @param fieldNames
	 * @return
	 */
	public int update(Object obj, String[] fieldNames){

		//obj (name,pwd)-->update tablename set name=?,pwd=? where id=?
		Class c = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("update "+ti.getName()+" set ");
		List<Object> params = new ArrayList<Object>();//存放参数
		ColumnInfo priKey = ti.getOnlyPriKey();
		
		for(String f : fieldNames) {
			Object  fvalue = ReflectUtils.invokeGet(f, obj);
			params.add(fvalue);//给属性赋值
			sql.append(f+"=?,");
		}
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));
		
		sql.setCharAt(sql.length()-1, ' ');
		sql.append("where ");
		sql.append(priKey.getName()+"=? ");
		//System.out.println(sql.toString());
		return executeDML(sql.toString(), params.toArray());
	
	}
	
	/**
	 * 查询返回多行记录 并将每行记录封装到clazz指定的类的对象中去
	 * @param sql	查询语句
	 * @param clazz	分装数据的javabean的对象
	 * @param params sql的参数
	 * @return
	 */
	
	public List queryRows(final String sql, final Class clazz, final Object[] params){


		
		return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData rsmd = rs.getMetaData();
					//多行
					while(rs.next()) {
						if(list == null) {
							list = new ArrayList();
						}
					
						Object rowObj = clazz.newInstance();//调用JavaBean的无参数构造器
						
						//多列
						for(int i=0;i<rsmd.getColumnCount();i++) {
							String columnName = rsmd.getColumnLabel(i+1);
							Object columnValue = rs.getObject(i+1);
							//调用rowObj对象的setUsername的方法,将columnValue的值设置到JavaBean里面
							ReflectUtils.invokeSet(rowObj, columnName, columnValue);
							
						}
						list.add(rowObj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return list; 
				
			}
		});
	
	}
	
	/**
	 * 查询返回单行记录 并将这行记录封装到clazz指定的类的对象中去
	 * @param sql	查询语句
	 * @param clazz	分装数据的javabean的对象
	 * @param params sql的参数
	 * @return
	 */
	public Object queryUniqueRow(String sql, Class clazz, Object[] params){

		List list = queryRows(sql, clazz, params);
		return list == null ? null : list.get(0);
	
	}
	/**
	 * 查询返回一个值 并将该值返回
	 * @param sql	查询语句
	 * @param clazz	分装数据的javabean的对象
	 * @param params sql的参数
	 * @return
	 */
	public Object queryValue(String sql, Object[] params){
		
		return executeQueryTemplate(sql, params, null, new CallBack() {
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while(rs.next()) {
						value = rs.getObject(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}
		});
	}
	/**
	 * 查询返回一个值 并将该值返回
	 * @param sql	查询语句
	 * @param clazz	分装数据的javabean的对象
	 * @param params sql的参数
	 * @return
	 */
	public Number queryNumber(String sql, Object[] params){

		return (Number) queryValue(sql, params);
	
	}
	/**
	 * 分页查询
	 * @param pageName
	 * @param size
	 * @return
	 */
	public abstract Object queryPagenate(int pageName,int size);
	
}
