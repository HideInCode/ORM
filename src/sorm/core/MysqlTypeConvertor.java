package sorm.core;

/**
 * mysql数据库类型和java数据类型的转换
 *
 * @author shh
 */
public class MysqlTypeConvertor implements TypeConvertor {
    private static volatile MysqlTypeConvertor inst;

    private MysqlTypeConvertor() {

    }

    public static MysqlTypeConvertor getInst() {
        if (inst == null) {
            synchronized (MysqlTypeConvertor.class) {
                if (inst == null) {
                    inst = new MysqlTypeConvertor();
                }
            }
        }
        return inst;
    }

    @Override
    public String datebaseType2JavaType(String columnType) {
        //varchar-->String
        if ("varchar".equalsIgnoreCase(columnType) || "char".equalsIgnoreCase(columnType)) {
            return "String";
        } else if ("int".equalsIgnoreCase(columnType) ||
                "tinyint".equalsIgnoreCase(columnType) ||
                "smallint".equalsIgnoreCase(columnType) ||
                "integer".equalsIgnoreCase(columnType)

        ) {
            return "Integer";
        } else if ("bigint".equalsIgnoreCase(columnType)) {
            return "Long";
        } else if ("double".equalsIgnoreCase(columnType) || "float".equalsIgnoreCase(columnType)) {
            return "Double";
        } else if ("clob".equalsIgnoreCase(columnType)) {
            return "java.sql.Clob";
        } else if ("blob".equalsIgnoreCase(columnType)) {
            return "java.sql.Blob";
        } else if ("date".equalsIgnoreCase(columnType)) {
            return "java.sql.Date";
        } else if ("time".equalsIgnoreCase(columnType)) {
            return "java.sql.Time";
        } else if ("timestamp".equalsIgnoreCase(columnType)) {
            return "java.sql.Timestamp";
        }

        return null;
    }

    @Override
    public String javaType2DatabaseType(String javaDataType) {
        if ("String".equalsIgnoreCase(javaDataType)) {
            return "VARCHAR(255)";
        } else if ("Integer".equalsIgnoreCase(javaDataType)) {
            return "INT";
        } else if ("Long".equalsIgnoreCase(javaDataType)) {
            return "BIGINT";
        } else if ("Double".equalsIgnoreCase(javaDataType)) {
            return "DOUBLE";
        } else if ("Float".equalsIgnoreCase(javaDataType)) {
            return "FLOAT";
        } else if ("Boolean".equalsIgnoreCase(javaDataType)) {
            return "BOOLEAN";
        } else if ("java.sql.Date".equalsIgnoreCase(javaDataType)) {
            return "DATE";
        } else if ("java.sql.Time".equalsIgnoreCase(javaDataType)) {
            return "TIME";
        } else if ("java.sql.Timestamp".equalsIgnoreCase(javaDataType)) {
            return "TIMESTAMP";
        } else if ("java.sql.Clob".equalsIgnoreCase(javaDataType)) {
            return "TEXT";
        } else if ("java.sql.Blob".equalsIgnoreCase(javaDataType)) {
            return "BLOB";
        }
        return "VARCHAR(255)";
    }

}
