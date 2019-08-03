package sorm.core;

public class QueryFactory {
    private static QueryFactory prototypeObj;

    static {
        try {
            Class c = Class.forName(DBManager.getConf().getQueryClass());

            prototypeObj = (QueryFactory) c.newInstance();
        } catch (Exception e) {
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
