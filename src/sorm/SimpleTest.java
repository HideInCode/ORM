package sorm;

import sorm.core.Query;
import sorm.core.QueryFactory;
import sorm.po.Test;

import java.util.List;

/**
 * 简单测试类
 */
public class SimpleTest {
    public static void main(String[] args) {
        try {
            Query query = QueryFactory.createQuery();
            System.out.println("Query对象创建成功: " + query.getClass().getName());

            // 测试查询
            Object result = query.queryValue("SELECT * from test", null);
            System.out.println("查询结果: " + result);
            List<Test> list = query.queryRows("SELECT * FROM test", Test.class, null);
            System.out.println(list);
            System.out.println("测试完成！");
        } catch (Exception e) {
            System.err.println("测试失败:");
            e.printStackTrace();
        }
    }
}