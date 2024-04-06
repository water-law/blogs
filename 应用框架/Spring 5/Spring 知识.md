## 编程性事务
注意.使用Spring自带的事，在抛RuntimeException时会自动回演，代码抛其他异常不会回滚。

国结批处理中若需要支持回滚,可使用以下方法

```java
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.Transaction5tatus:
import org.springfranework.transaction.support.DefaultTransactionDefinition;
import arg-springfranework.transaction.support.TransactionCallback;

import org.springfranework.transaction.support.TransactionTemplate;

PlatformTransactionManager platformTransactionManager = new DataSourceTransactionManager (template.getDataSource());
DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
trangactionDefinition.setTimeout (100);
TransactionTemplate transactionTemplate = new TransactionTemplate (platformTransactionManager,trangactionDefinition);

Boolean isSuccess = transactionTemplate.execute(new TransactionCallback<Boolean>()(
              @Override
              public Boolean doInTransaction(TransactionStatus status) {
                // TODO Auto-generated method stub try[

                // TODO Auto-generated catch block e.printStackTrace():
                logger.error(String.format(”境内非居民代发工资申报:insertBatchDfgzBatDataVOList,异常原因:(V#]”，e.getMessage()));
                return Boolean.TRUE;
              }
));
```



## 声明性事务

### 使用 TransactionProxyFactoryBean

```xml
<alias name="transactionManager" alias="txManager"/>

<bean id="baseTransactionProxy" 
	class="org.springframework.transaction.interceptor.TrangactionProxyFactoryBean” abstract="true">
	<property name="transactionManager" ref="txManager"/> 
	<property name="transactionAttributeSource"> 
      <bean
        class="org.snringfrsmework.transaction.interceptor.NameMatchTransactionAttributeSource"> 
        <property name="properties">
            <props> 
              <!-- --> 
              <prop key="insert*">PROPAGATION_REQUIRED</prop>
              <prop key="create*">PROPAGATION_REQUIRED</prop>
              <prop key="update*">PROPAGATION_REQUIRED</prop>
              <prop key="delete*">PROPAGATION REQUIRED</prop> 
              <prop key="save*">PROPAGATION_REQUIRED</prop>
              <prop key="remove*">PROPAGATION_REQUIRED</prop>
              <prop key-"invoke*">PROPAGATION_REQUIRED</prop> 
              <prop key="return*">PROPAGATION_REQUIRED</prop>
              <prop key="adjust*">PROPAGATION_REQUIRED</prop>
              <prop key="check*">PROPAGATION_REQUIRED</prop>
              <prop key="accept*">PROPAGATION_REQUIRED</prop>
              <prop key="commit*">PROPAGATION_REQUIRED</prop>
              <prop key="route*">PROPAGATION_REQUIRED</prop>
              <prop key="reject*">PROPAGATION_REQUIRED</prop>
              <prop key="cancel*">PROPAGATION_REQUIRED</prop>
              <prop key="end*">PROPAGATION_REQUIRED</prop>
              <prop key="retract*">PROPAGATION_REQUIRED</prop>
              <prop key="start*">PROPAGATION_REQUIRED</prop>
              <prop key="stop*">PROPAGATICN_REQUIRED</prop>
              <prop key="wake*">PROPAGAIICN_REQUIRED</prop>
              <prop key="send*">PROPAGATICN_REQUIRED</prop>
              <prop key="lock*">PROPAGATICN_BEQUIRED</prop>
              <prop key="unlock*">PROPAGATICN_REQUIRED</prop>
              <prop key="Handle*">PROPAGATICN_REQUIRED</prop>
              <prop key="txnew*">PROPAGATION_REQUIRES_NEW</prop>
              <prop key="*">PROPAGATION_REQUIRED,readOnly</prop>
              <!--
              <prop key="*">PROPAGATION_NEVER</prop>
              -->
             </props>
        </property>     
       </bean>
  </property>
</bean>
```


然后 使用 parent 属性继承 TransactionProxyFactoryBean 模板

```xml
<!--by zb增加事务代理-->

<bean id="mpsSwfGpiCommonBS" parent="baseTransactionProxy"> 
	<property nane="target">
    <bean class="com.hylandtec.mps.gpi.bs.imp1.GpiCommonBSImpl"> 
      <property name="basBS" ref="mpsBasBS"/> 
      <property name="msgTransformer" ref="mpsTrfMessageTransformer" />
      <property nane="basDAO" ret-"mpsBaseDAO/> 
      <property name="magBS" ref="mpsMsgBS"/>
      <property name="eimBS" ref="mpsEimGpiBS" />
    </bean> 
  </property> 
 </bean> 
```

### 使用 BeanNameAutoProxyFactoryBean 

根据 bean name自动生成事务代理的方式,直接利用spring的AOP框架配置事务代理的方式  

```xml
<bean id="transactionInterceptor" class="org.springframewark.cransaction.interceptor.TransactionInterceptor"> 
	<property name-"transactionManager" ref="txManager"/>
  <property name="transactionAttributeSource"> 
   	<bean 
 			class="org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource"> 
 			<property name-"properties"> 
 				<props> 
            <----> 
            <prop key="insert*">PROPAGATION_REQUIRED</prop>
            <prop key="create*">PROPAGATION_REQUIRED</prop>
            <prop key="update*">PROPAGATION_REQUIRED</prop>
            <prop key="delete*">PROPAGATION REQUIRED</prop> 
            <prop key="save*">PROPAGATION_REQUIRED</prop>
            <prop key="remove*">PROPAGATION_REQUIRED</prop>
            <prop key-"invoke*">PROPAGATION_REQUIRED</prop> 
            <prop key="return*">PROPAGATION_REQUIRED</prop>
            <prop key="adjust*">PROPAGATION_REQUIRED</prop>
            <prop key="check*">PROPAGATION_REQUIRED</prop>
            <prop key="accept*">PROPAGATION_REQUIRED</prop>
            <prop key="commit*">PROPAGATION_REQUIRED</prop>
            <prop key="route*">PROPAGATION_REQUIRED</prop>
            <prop key="reject*">PROPAGATION_REQUIRED</prop>
            <prop key="cancel*">PROPAGATION_REQUIRED</prop>
            <prop key="end*">PROPAGATION_REQUIRED</prop>
            <prop key="retract*">PROPAGATION_REQUIRED</prop>
            <prop key="start*">PROPAGATION_REQUIRED</prop>
            <prop key="stop*">PROPAGATICN_REQUIRED</prop>
            <prop key="wake*">PROPAGAIICN_REQUIRED</prop>
            <prop key="send*">PROPAGATICN_REQUIRED</prop>
            <prop key="lock*">PROPAGATICN_REQUIRED</prop>
            <prop key="unlock*">PROPAGATICN_REQUIRED</prop>
            <prop key="Handle*">PROPAGATICN_REQUIRED</prop>
            <prop key="txnew*">PROPAGATION_REQUIRES_NEW</prop>
            <prop key="*">PROPAGATION_REQUIRED,readOnly</prop>
            <!--
            <prop key="*">PROPAGATION_NEVER</prop>
            -->
         </props>
        </property>     
       </bean>
  </property>
</bean>
                         
<bean class-"org.springframework.aop.framework.autoproxy.BeanNaneAutoProxyCreator"> 
    <!--使用cghib代理--> 
    <property nane="proxyTargetClass" value="true"></property> 
    <!--指定对满足bean name 的bean自动生成代理> 
    <property name="beanNames"> 
      <list> 
          <value>persondao</value> 
      </list>
    </property> 
  	<!-- BeanNaneAutoProxyCreator定义所需要的事务拦截器--> 
 
   	<property nane="interceptorNames"> 
      <list> 
          <value> 
            transactionInterceptor 
          </value> 
      </list> 
  </property> 
</bean> 

<bean id="persondao" class="lee.PersonDaoHibernate">
  <property nane="sessionFactory"> 
  	<ref local="sessionfactory"/> 
  </property>
</bean>
```



### 使用 DefaultAdvinorAutoProxyFactoryBean 


也是直接 利用 spring 的 AOP 框架.，不过配置方式的可读性不如 BeanNaneAutoProxyFactoryBean 



## Spring常用接口

### ApplicationContextAware 

实现 ApplicationContextAware 可获取 ApplicationContext上下文 


```java
package util; 

import org.anringfranework.beans.BeanaException;
import aig.springlramcwork-context.ApplicationContext;
import grg anringtranework. context.ApplicationContextAware;

public clasc ContextUtil implements ApplicationContextAware { 
	public static ApplicationContext context;
}
```


application.xml 中注入 ContextUtil

```xml
<bean class="util.ContextUtil"></bean>
```

也可以在 ApplicationContextAware 中执行一些方法 com.hylandtec.bup.base.common.cache.service.impl.CacheServiceImpl

```java
public class CacheServiceImpl implemente ICacheService,ApplicationContextAware {

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
        this.validateCache();
        this.preloadCaches();
    }
}
```



### 加载 properties 文件

spring 框架中， spring.framework.beans.factory.config.PropertyPlaceholderConfigurer 类可以将 properties (key/ value)文件中, 动态设定的值绑定到 XML 配置的 bean 属性中。
config.properties



Spring 的 PropertyPlaceholderConfigurer 接口专门用于处理 properties 文件解析

```java
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
** 对配置文件做加密处理
**/
public class EncryptePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer (
  private String identifier ="password”;
  private static Log log = LogFactory.getlog(EncryptePropertyPlaceholderConfigurer.class);
  
  @Override
  protected String resolvePlaceholder(String placeholder, Properties props){
    String value = super.resolvePlaceholder(placeholder, props);
    if (placeholder.endsWith(identifier)) {
      // 解密
    }
    return value;
  }
  
  public String getIdentifier() [
  	return identifier;
  }
    
	public void setIdentifier(String identifier)[
		this.identifier = identifier;
  }
}
```



配置完后，${jdbc.url] 会被 properties 中对应的值所替代

### 动态加载 XM 解析为 Bean

```java
package com.longtop.intelliweb.sample.factory;

import org.apache.commons.logging.Log;import org.apache.commons.logging.LogFactory;
import org.springframework.beans,factory.access.BeanFactorylocator;
import org.springframework.beans.factory.access.BeanFactoryReference
import org.springframework.beans.factory.access.SingletonBeanFactorylocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;


public class SpringBeanFactory {
  private static log log = logFactory.getlog(SpringBeanFactory.class);
  private static ApplicationContext appContext = null;
  private static String beanRefFactoryGroupId = "defaultGroupBeanId";
  public static String beanRefFactoryFile = "/config/fims/beanRefFactory.xml";
  
	static {
      try {
        BeanFactorylocator locator = SingletonBeanFactorylocator.getInatance(beanRefFactoryFile);
        BeanFactoryReference bfr  = locator.useBeanFactory(beanRefFactoryGroupId);
        appContext = (ApplicationContext) bfr.getFactory();
      } catch (Throwable e){
        e.printStackTrace():
        log.error("",e);
      }
  }

  // 根据beanId取得实例
  /** @param beanId
  ** @return object
  **/
  public static Object getBean(String beanId){return appContext.getBean(beanId)};
  public static ApplicationContext getAContext() return appContext;

}
```



```xml
<?xml version="1.0” encoding="UTE-8”2>
<!DOCTYPE beans PUBLIC "-//SPRING//DID BEAN//EN" "http://w.springframework.orq/dtd/spring-beans.dtd">
<beans default-lazy-init="falge" default-autowire-"no" default-dependency-check="none">
    <bean id="defaultGroupBeanId" class="org.springframework.context.support.ClassPathXmlApplicationonContext"1azy-init="default" autowire="default" dependency-check>
		<constructor-arg>
      <list>
      	<value>/config/fims/dataAccessContext.xml</value>
      </list>
    </constructor-arg>
    </bean>
</beans>
```

### 属性注入和 InitializingBean

往 SchedulerFactoryBean 电注入属性 configLocation

```xml
<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
<property name="configLocation">
  <value>classpath:/quartz.properties</value>
</property>
<property name="dataSource" ref="dataSource">
</property>
<property name="triggers">
  <list>
    <ref bean="myIrigger"/>
  </list>
</property>
</bean>
```

SchedulerFactoryBean 类实现了 Spring 的 InitializingBean 接口，接口中有一个 afterPropertiesSet(方法，可以在属性注入后对属性进行解折或者处理
public interface InitializingBean {

}

### Spring 属性编辑工具类

org.springframework.core.io.support.PropertiesLoaderUtils#fillProperties
fillProperties(Properties props, Resource resource)a
将 resource 下的 K-V 键值对放入 props
org.springframework.util.CollectionUtilsfmergePropertiesIntoMap
mergePropertiesIntoMap(Properties props, Map map) :将 props 放入 map

## FactoryBean
## ApplicationListener
```java
public class ConfigManagerImpl implements ConfigManager, ApplicationListener {

  public void onApplicationEvent (ApplicationEvent event){

    if (event instanceof ContextRefreshedEvent) {
      try {
        checkReload();
      }catch (Throwable e) {
        logger.error(e.getMessage(), e);
      }
    }
  }
}
```

Spring 加载时默认会触发一达

### Lifecycle 与 DefaultLifecycleProcessor

org.springframework.context.support.DefaultLifecycleProcessor实现了 Iifecycle 接口的 bean，启动时会调用 start() 方法，停止时会调用 stop()方法



