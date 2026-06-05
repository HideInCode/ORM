package sorm.bean;

import java.util.List;
import java.util.Map;

/**
 * 存储表信息
 *
 * @author shh
 */
public class TableInfo {
    /**
     * 表名
     */
    private String name;

    private Map<String, ColumnInfo> columns;

    /**
     * 唯一主键(目前只能处理表中只有一个主键的情况)
     */
    private ColumnInfo onlyPriKey;
    /**
     * 联合主键
     */
    private List<ColumnInfo> priKeys;

    public List<ColumnInfo> getPriKeys() {
        return priKeys;
    }


    public void setPriKeys(List<ColumnInfo> priKeys) {
        this.priKeys = priKeys;
    }


    public TableInfo() {
    }


    public TableInfo(String name, List<ColumnInfo> priKeys, Map<String, ColumnInfo> columns) {
        super();
        this.name = name;
        this.columns = columns;
        this.priKeys = priKeys;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ColumnInfo> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, ColumnInfo> columns) {
        this.columns = columns;
    }

    public ColumnInfo getOnlyPriKey() {
        return onlyPriKey;
    }

    public void setOnlyPriKey(ColumnInfo onlyPriKey) {
        this.onlyPriKey = onlyPriKey;
    }

}
