# ORM 框架使用指南

## 项目说明
轻量级 Java ORM 框架，实现数据库表与 Java PO 类的自动映射和 CRUD 操作。

## 配置说明

### 1. 配置文件位置
`src/jdbc.properties`

### 2. 配置项说明

```properties
# JDBC 驱动类名
driver=com.mysql.cj.jdbc.Driver

# 数据库连接 URL
url=jdbc:mysql://localhost:3306/your_database?useUnicode=true&characterEncoding=utf8mb4&serverTimezone=GMT%2B8&useSSL=false

# 数据库用户名
user=root

# 数据库密码
pwd=root

# 数据库类型
usingDB=mysql

# 项目源码路径 (用于生成 PO 类)
srcPath=D:/code/java/orm/ORM

# PO 类包名
poPackage=po

# 查询实现类
queryClass=sorm.core.MysqlQuery

# 连接池最小连接数
poolMinSize=10

# 连接池最大连接数
poolMaxSize=100
```

## 依赖配置

### 必需依赖
将 MySQL JDBC 驱动添加到项目中：
- **位置**: `src/resource/mysql-connector-java-8.0.17.jar`
- **版本**: mysql-connector-java-8.0.17 或更高

### IDEA 配置
1. File -> Project Structure -> Libraries
2. 点击 `+` -> Java
3. 选择 `src/resource/mysql-connector-java-8.0.17.jar`
4. Apply -> OK

### 运行时配置
确保编译输出包含 MySQL 驱动 jar：
- Project Structure -> Artifacts
- 将 mysql-connector-java-8.0.17.jar 添加到 Output Layout

## 使用示例

### 1. 初始化框架
```java
// 框架在首次使用时会自动初始化，读取数据库表结构并生成 PO 类
```

### 2. 获取 Query 对象
```java
Query query = QueryFactory.createQuery();
```

### 3. 插入数据
```java
Emp emp = new Emp();
emp.setEmpname("张三");
emp.setSalary(5000.0);
query.insert(emp);
```

### 4. 删除数据
```java
// 根据类和主键删除
query.delete(Emp.class, 1);

// 根据对象删除
query.delete(emp);
```

### 5. 更新数据
```java
emp.setSalary(6000.0);
query.update(emp, new String[]{"salary"});
```

### 6. 查询数据
```java
// 查询单行
Emp emp = (Emp) query.queryUniqueRow("SELECT * FROM emp WHERE id=?", Emp.class, new Object[]{1});

// 查询多行
List<Emp> list = query.queryRows("SELECT * FROM emp", Emp.class, null);

// 查询单个值
Object count = query.queryValue("SELECT COUNT(*) FROM emp", null);

// 分页查询
List<Emp> pageList = query.queryPagenate(Emp.class, 1, 10);
```

## 注意事项

1. **主键限制**: 每张表只能有一个主键
2. **自增主键**: 只支持自增主键
3. **包装类**: PO 类请使用包装类（Integer、Long 等），不要使用基本数据类型
4. **字段映射**: 数据库字段名与 Java 属性名需一致
5. **自动生成**: PO 类会根据表结构自动生成，手动修改会被覆盖
