### 自定义异常

```java
public class MybatisPlusException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MybatisPlusException(String message) {
		super(message);
	}

	public MybatisPlusException(Throwable throwable) {
		super(throwable);
	}

	public MybatisPlusException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
```



### 工厂模式

定义一个公有的接口

```java
public interface IDialect {

	/**
	 * 组装分页语句
	 * 
	 * @param originalSql
	 *            原始语句
	 * @param offset
	 *            偏移量
	 * @param limit
	 *            界限
	 * @return 分页语句
	 */
	String buildPaginationSql(String originalSql, int offset, int limit);
}
```



实现接口

```java
/**
 * <p>
 * MYSQL 数据库分页语句组装实现
 * </p>
 * 
 * @author hubin
 * @Date 2016-01-23
 */
public class MySqlDialect implements IDialect {

	public String buildPaginationSql(String originalSql, int offset, int limit) {
		StringBuilder sql = new StringBuilder(originalSql);
		sql.append(" LIMIT ").append(offset).append(",").append(limit);
		return sql.toString();
	}

}
```



工厂类

```java
public class DialectFactory {

	/**
	 * <p>
	 * 根据数据库类型选择不同分页方言
	 * </p>
	 * 
	 * @param dbtype
	 *            数据库类型
	 * @return
	 * @throws Exception
	 */
	public static IDialect getDialectByDbtype(String dbtype) throws Exception {
		if ("mysql".equalsIgnoreCase(dbtype)) {
			return new MySqlDialect();
		} else if ("oracle".equalsIgnoreCase(dbtype)) {
			return new OracleDialect();
		} else if ("hsql".equalsIgnoreCase(dbtype)) {
			return new HSQLDialect();
		} else if ("sqlite".equalsIgnoreCase(dbtype)) {
			return new SQLiteDialect();
		} else if ("postgre".equalsIgnoreCase(dbtype)) {
			return new PostgreDialect();
		} else {
			return null;
		}
	}

}
```



### 责任链模式

在有些场景下，一个目标对象可能需要经过多个对象的处理。例如，我们要筹办一场校园晚会，需要针对演员进行如下的准备工作。
· 给演员发送邮件，告知晚会的时间、地点，该工作由邮件发送员负责。
· 根据演员性别为其准备衣服，该工作由物资管理员负责。
· 如果演员未成年，则为其安排校车接送，该工作由对外联络员负责。

这一过程，每个演员都要和三个工作人员打交道。

而责任链模式将多个处理器组装成一个链条，被处理对象被放置到链条的起始端后，会自动在整个链条上传递和处理。这样被处理对象不需要和每个处理器打交道，也不需要了解整个链条的传递过程，于是便实现了被处理对象和单个处理器的解耦。
为实现责任链模式，首先创建一个处理器抽象类 Handler。

```java
public abstract class Handler {
    //当前处理器的下一个处理器 private Handler nextHandler;
    /***当前处理器的处理逻辑，交给子类实现*@param performer 被处理对象 ***/
    public abstract void handle(Performer performer);
    /**
    * 触发当前处理器，并在处理结束后将被处理对象传给后续处理器*@param performer被处理对象
    */
    public void triggerProcess(Performer performer){
        handle(performer);
        if(nextHandler!=null){
        nextHandler.triggerProcess(performer);
    }

    /**
    *设置当前处理器的下一个处理器*@param nextHandler 下一个处理器*@return 下一个处理器
    */
    public Handler setNextHandler(Handler nextHandler){
      	this.nextHandler=nextHandler; 
      	return nextHandler;
    }
      
    public static void main(String[] args) {
        Handler handlerChain=new MailSender();
        handlerChain.setNextHandler(new MaterialManager()).setNextHandler(new ContactOfficer())

        //依次处理每个参与者
        for (Performer performer:performerList){
            System.out.println("process"+performer.getName()+":"); handlerChain.triggerProcess(performer); 	
        }
    }
}
```

在调用时，需要先组装好整个责任链，然后将被处理对象交给责任链处理即可。这样，每个演员不需要和工作人员直接打交道，也不需要关心责任链上到底有多少个工作人员。
责任链模式不仅降低了被处理对象和处理器之间的耦合度，还使得我们可以更为灵活地组建处理过程。例如，我们可以很方便地向责任链中增、删处理器或者调整处理器的顺序。