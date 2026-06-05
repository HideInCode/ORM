package sorm.core;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.util.JavaFileUtils;
import sorm.util.StringUtils;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构。
 *
 * @author shh
 */
public class TableContext {

    /**
     * 表名为key，表信息对象为value
     */
    public static Map<String, TableInfo> tables = new HashMap<>();
    /**
     * 将po的class对象和表信息对象关联起来，便于重用！
     */
    public static Map<Class, TableInfo> poClassTableMap = new HashMap<>();

    private TableContext() {

    }

    static {
        try {
            //初始化获得表的信息
            Connection connection = DBManager.getConn();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, new String[]{"TABLE"});

            while (tables.next()) {
                //获取表
                String tableName = (String) tables.getObject("TABLE_NAME");
                TableInfo tableInfo = new TableInfo(tableName, new ArrayList<>(), new HashMap<>());
                TableContext.tables.put(tableName, tableInfo);
                ResultSet set = metaData.getColumns(null, "%", tableName, "%");
                //查询表中的所有字段
                while (set.next()) {
                    ColumnInfo columnInfo = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
                    tableInfo.getColumns().put(set.getString("COLUMN_NAME"), columnInfo);
                }

                ResultSet primaryKeys = metaData.getPrimaryKeys(null, "%", tableName);
                //查询表中的主键
                while (primaryKeys.next()) {
                    ColumnInfo priKey = tableInfo.getColumns().get(primaryKeys.getObject("COLUMN_NAME"));
                    priKey.setKeyType(1);
                    //设置为主键类型
                    tableInfo.getPriKeys().add(priKey);
                }
                //取唯一主键。。方便使用。如果是联合主键。则为空！
                if (tableInfo.getPriKeys().size() > 0) {
                    tableInfo.setOnlyPriKey(tableInfo.getPriKeys().get(0));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //每次启动都包含最新的属性
        updateJavaPOFile();

        //加载po包下的所有的类 便于复用
        loadPOTables();
    }

    /**
     * 根据表结构 更新po包下的java类
     * 实现了表结构到类结构 新属性会更新到类里面去
     */
    public static void updateJavaPOFile() {
        Map<String, TableInfo> map = TableContext.tables;

        for (TableInfo ti : map.values()) {
            JavaFileUtils.createJavaPOFile(ti, MysqlTypeConvertor.getInst());
        }
    }

    /**
     * 加载po包下的类
     */
    public static void loadPOTables() {
        String srcPath = DBManager.getConf().getSrcPath();
        String packagePath = DBManager.getConf().getPoPackage().replace(".", "\\");
        String poDirPath = srcPath + "\\" + packagePath;

        for (TableInfo table : tables.values()) {
            try {
                String className = DBManager.getConf().getPoPackage() + "." + StringUtils.firstChar2UpperCase(table.getName());
                String javaFilePath = poDirPath + "\\" + StringUtils.firstChar2UpperCase(table.getName()) + ".java";

                // 编译生成的Java文件
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                int result = compiler.run(null, null, null, javaFilePath);
                if (result != 0) {
                    System.err.println("编译失败: " + javaFilePath);
                }

                // 加载编译后的类
                URLClassLoader loader = URLClassLoader.newInstance(new URL[]{new File(srcPath).toURI().toURL()});
                Class<?> clazz = loader.loadClass(className);
                poClassTableMap.put(clazz, table);
                JavaFileUtils.delete(poDirPath + "\\" + StringUtils.firstChar2UpperCase(table.getName()) + ".class");
            } catch (ClassNotFoundException e) {
                System.err.println("加载类失败: " + StringUtils.firstChar2UpperCase(table.getName()));
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Map<String, TableInfo> tables = TableContext.tables;
        System.out.println(tables);
    }

}