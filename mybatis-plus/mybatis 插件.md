### Mybatis 拦截器

Signature 配置拦截器要拦截的类和方法，Mybatis 中一共只有四个类对象可以被拦截器替换，分别是 ParameterHandler、R esultSetHandler、StatementHandler、Executor。

```java
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * <p>
 * 分页拦截器
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }) })
public class PaginationInterceptor implements Interceptor {

      /* 方言类型 */
      private String dialectType;

      /* 方言实现类 */
      private String dialectClazz;
			public Object intercept(Invocation invocation) throws Throwable {
				Object target = invocation.getTarget();
        if (target instanceof StatementHandler) {
          StatementHandler statementHandler = (StatementHandler) target;
          MetaObject metaStatementHandler = SystemMetaObject.forObject(statementHandler);
          RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");

          /* 不需要分页的场合 */
          if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
          }

          /* 定义数据库方言 */
          IDialect dialect = null;
          if (dialectType != null && !"".equals(dialectType)) {
            dialect = DialectFactory.getDialectByDbtype(dialectType);
          } else {
            if (dialectClazz != null && !"".equals(dialectClazz)) {
              try {
                Class<?> clazz = Class.forName(dialectClazz);
                if (IDialect.class.isAssignableFrom(clazz))
                  dialect = (IDialect) clazz.newInstance();
              } catch (ClassNotFoundException e) {
                throw new MybatisPlusException("Class :" + dialectClazz + " is not found");
              }
            }
          }

          /* 未配置方言则抛出异常 */
          if (dialect == null) {
            throw new MybatisPlusException("The value of the dialect property in mybatis configuration.xml is not defined.");
          }

          /* 禁用内存分页 */
          BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");

          /* 禁用内存分页 */
          String originalSql = (String) boundSql.getSql();
          String paginationSql = dialect.buildPaginationSql(originalSql, rowBounds.getOffset(), 			 rowBounds.getLimit());
          metaStatementHandler.setValue("delegate.boundSql.sql", paginationSql);

          /* 禁用内存分页 */
          metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
          metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);

          /* 判断是否需要查询总记录条数 */
          if (rowBounds instanceof Pagination) {
            Pagination pagination = (Pagination) rowBounds;
            if (pagination.getTotal() == 0) {
              MappedStatement mappedStatement = (MappedStatement) metaStatementHandler
                  .getValue("delegate.mappedStatement");
              Connection connection = (Connection) invocation.getArgs()[0];
              count(originalSql, connection, mappedStatement, boundSql, pagination);
            }
          }
        }
        return invocation.proceed();
			}
			
      public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
          return Plugin.wrap(target, this);
        }
        return target;
      }
        
      public void setProperties(Properties prop) {
        String dialectType = prop.getProperty("dialectType");
        String dialectClazz = prop.getProperty("dialectClazz");
        if (dialectType != null && !"".equals(dialectType)) {
          this.dialectType = dialectType;
        }
        if (dialectClazz != null && !"".equals(dialectClazz)) {
          this.dialectClazz = dialectClazz;
        }
      }
}
```



setProperties 方法可以往拦截器类中注入属性，拦截器拦截到目标方法时，会将操作转接到 intercept 方法，plugin: 拦截器类可以选择实现该方法，该方法可以输出一个对象来替换输入参数传入的目标对象，默认调用 Plugin.wrap(target, this) 即可。



MetaObject 是 Mybatis 封装好的反射方法，可以获取到实例的属性，如

```java
RowBounds rowBounds = (RowBounds) metaStatementHandler.getValue("delegate.rowBounds");
```

获取 metaStatementHandler 的实例属性 delegate 中的 rowBounds 属性。



Mybatis 中一共只有四个类对象可以被拦截器替换，分别是 ParameterHandler、ResultSetHandler、StatementHandler、Executor。

1、Executor：mybatis 的内部执行器，作为调度核心负责调用 StatementHandler 操作数据库，并把结果集通过 ResultSetHandler 进行自动映射；

2、StatementHandler： 封装了 JDBC Statement 操作，是 sql 语法的构建器，负责和数据库进行交互执行 sql 语句；

3、ParameterHandler：作为处理 sql 参数设置的对象，主要实现读取参数和对 PreparedStatement 的参数进行赋值;

4、ResultSetHandler：处理 Statement 执行完成后返回结果集的接口对象，mybatis 通过它把 ResultSet 集合映射成实体对象;

在 mybatis 中，不同类型的拦截器按照下面的顺序执行：
**Executor -> StatementHandler -> ParameterHandler -> ResultSetHandler**

以执行 **query** 方法为例对流程进行梳理，整体流程如下：

```java
1、Executor 执行 query() 方法，创建一个 StatementHandler 对象

2、StatementHandler 调用 ParameterHandler 对象的 setParameters() 方法

3、StatementHandler 调用 Statement 对象的 execute() 方法

4、StatementHandler 调用 ResultSetHandler 对象的 handleResultSets() 方法，返回最终结果
```



### 插件注册

```xml
    <plugins>
	    <!-- 
	     | 分页插件配置 
	     | 插件提供二种方言选择：1、默认方言 2、自定义方言实现类，两者均未配置则抛出异常！
	     | dialectType 数据库方言  
	     |             默认支持  mysql  oracle  hsql  sqlite  postgre
	     | dialectClazz 方言实现类
	     |              自定义需要实现 com.baomidou.mybatisplus.plugins.pagination.IDialect 接口
	     | -->
        <plugin interceptor="com.baomidou.mybatisplus.plugins.PaginationInterceptor">
            <property name="dialectType" value="mysql" />
        </plugin>
    </plugins>
```

