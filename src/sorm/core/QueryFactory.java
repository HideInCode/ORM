package sorm.core;

/**
 * 查询工厂，使用原型模式创建Query实例
 */
public class QueryFactory {
    private static Query prototypeObj;

    static {
        try {
            Class c = Class.forName(DBManager.getConf().getQueryClass());
            prototypeObj = (Query) c.newInstance();
        } catch (Exception e) {
            System.err.println("加载Query实现类失败: " + DBManager.getConf().getQueryClass());
            e.printStackTrace();
        }
    }

    private QueryFactory() {

    }

    public static Query createQuery() {
        try {
            return (Query) prototypeObj.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}