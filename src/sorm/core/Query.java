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
 * �����ѯ�����ṩ���ĵ���
 * ģ�巽��ģʽ�ع���
 * @author ����
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
			
			
			//ֻ��Ҫ��д��һ�����־ͺ���
			return back.doExecute(con, ps, rs);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			DBManager.close(ps, con);
		}
	
	
	}
	
	/**
	 * ִ��DML���
	 * @param sql
	 * @param params
	 * @return ִ��sql����Ӱ���¼������
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
	 * ������洢 �����ݿ���
	 * �Ѷ���
	 * @param obj Ҫ�洢�Ķ���
	 */
	public void insert(Object obj){

		//obj-->���� insert into ���� (id, name,pwd) values (?,?,?)
		Class c = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("insert into "+ti.getName()+" (");
		Field[] fs = c.getDeclaredFields();
		List<Object> params = new ArrayList<Object>();//��Ų���
		
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
	 * ɾ��clazz���Ӧ�ı��еļ�¼(ָ������ֵid�ļ�¼)
	 * @param clazz �����Ӧ����Ķ���
	 * @param id ������ֵ
	 */
	public void delete(Class clazz, Object id){

		//Emp.class,2 --> delete form emp where id =2
		
		//һֱ������tableInfo
		TableInfo ti = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = ti.getOnlyPriKey();
		
		String sql = "delete from "+ti.getName()+" where "+onlyPriKey.getName()+"=? ";
		executeDML(sql, new Object[] {id});
	
	}
	
	/**
	 * ɾ�����ݿ��еĶ�Ӧ��¼ 
	 * @param obj
	 */
	public void delete(Object obj){

		Class clazz = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(clazz);
		ColumnInfo onlyPriKey = ti.getOnlyPriKey();
		
		//ͨ��������� �������Զ�Ӧ��set get����
	
		
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		delete(clazz, priKeyValue);
		
	
	}
	
	/**
	 * ���ݶ����Ӧ�ļ�¼,����ֻ����ָ���ֶε�ֵ
	 * @param obj
	 * @param fieldNames
	 * @return
	 */
	public int update(Object obj, String[] fieldNames){

		//obj (name,pwd)-->update tablename set name=?,pwd=? where id=?
		Class c = obj.getClass();
		TableInfo ti = TableContext.poClassTableMap.get(c);
		StringBuilder sql = new StringBuilder("update "+ti.getName()+" set ");
		List<Object> params = new ArrayList<Object>();//��Ų���
		ColumnInfo priKey = ti.getOnlyPriKey();
		
		for(String f : fieldNames) {
			Object  fvalue = ReflectUtils.invokeGet(f, obj);
			params.add(fvalue);//�����Ը�ֵ
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
	 * ��ѯ���ض��м�¼ ����ÿ�м�¼��װ��clazzָ������Ķ�����ȥ
	 * @param sql	��ѯ���
	 * @param clazz	��װ���ݵ�javabean�Ķ���
	 * @param params sql�Ĳ���
	 * @return
	 */
	
	public List queryRows(final String sql, final Class clazz, final Object[] params){


		
		return (List) executeQueryTemplate(sql, params, clazz, new CallBack() {
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData rsmd = rs.getMetaData();
					//����
					while(rs.next()) {
						if(list == null) {
							list = new ArrayList();
						}
					
						Object rowObj = clazz.newInstance();//����JavaBean���޲���������
						
						//����
						for(int i=0;i<rsmd.getColumnCount();i++) {
							String columnName = rsmd.getColumnLabel(i+1);
							Object columnValue = rs.getObject(i+1);
							//����rowObj�����setUsername�ķ���,��columnValue��ֵ���õ�JavaBean����
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
	 * ��ѯ���ص��м�¼ �������м�¼��װ��clazzָ������Ķ�����ȥ
	 * @param sql	��ѯ���
	 * @param clazz	��װ���ݵ�javabean�Ķ���
	 * @param params sql�Ĳ���
	 * @return
	 */
	public Object queryUniqueRow(String sql, Class clazz, Object[] params){

		List list = queryRows(sql, clazz, params);
		return list == null ? null : list.get(0);
	
	}
	/**
	 * ��ѯ����һ��ֵ ������ֵ����
	 * @param sql	��ѯ���
	 * @param clazz	��װ���ݵ�javabean�Ķ���
	 * @param params sql�Ĳ���
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
	 * ��ѯ����һ��ֵ ������ֵ����
	 * @param sql	��ѯ���
	 * @param clazz	��װ���ݵ�javabean�Ķ���
	 * @param params sql�Ĳ���
	 * @return
	 */
	public Number queryNumber(String sql, Object[] params){

		return (Number) queryValue(sql, params);
	
	}
	/**
	 * ��ҳ��ѯ
	 * @param pageName
	 * @param size
	 * @return
	 */
	public abstract Object queryPagenate(int pageName,int size);
	
}
