### 类加载

<img src="https://i.loli.net/2019/11/30/ODth2f8Gq41QnoE.png" alt="类加载.png" style="zoom: 80%;" />

#### **1、加载**

”加载“是”类加机制”的第一个过程，在加载阶段，虚拟机主要完成三件事：

（1）通过一个类的**全限定名**来获取其定义的**二进制字节流**

（2）将这个字节流所代表的的静态存储结构转化为**方法区**的**运行时数据结构**

（3）在**堆**中生成一个代表这个类的 **Class 对象**，作为方法区中这些数据的访问入口。

#### **2、验证**

验证的主要作用就是确保被加载的类的正确性。也是连接阶段的第一步。说白了也就是我们加载好的 .class 文件不能对我们的虚拟机有危害，所以先检测验证一下。他主要是完成四个阶段的验证：

（1）**文件格式**的验证：验证.class文件字节流是否符合class文件的格式的规范，并且能够被当前版本的虚拟机处理。这里面主要对魔数、主版本号、常量池等等的校验（魔数、主版本号都是.class文件里面包含的数据信息、在这里可以不用理解）。

（2）**元数据**验证：主要是对字节码描述的信息进行语义分析，以保证其描述的信息符合 java 语言规范的要求，比如说验证这个类是不是有父类，类中的字段方法是不是和父类冲突等等。

（3）**字节码**验证：这是整个验证过程最复杂的阶段，主要是通过数据流和控制流分析，确定程序语义是合法的、符合逻辑的。在元数据验证阶段对数据类型做出验证后，这个阶段主要对类的方法做出分析，保证类的方法在运行时不会做出威海虚拟机安全的事。

（4）**符号引用**验证：它是验证的最后一个阶段，发生在虚拟机将符号引用转化为直接引用的时候。主要是对类自身以外的信息进行校验。目的是确保解析动作能够完成。

#### **3、准备**

**准备阶段主要为类变量分配内存并设置初始值**。这些内存都在方法区分配。在这个阶段我们只需要注意两点就好了，也就是类变量和初始值两个关键词：

（1）类变量（static）会分配内存，但是实例变量不会，实例变量主要随着对象的实例化一块分配到 java 堆中，

（2）这里的初始值指的是数据类型默认值，而不是代码中被显示赋予的值。比如

public static int value = 1; //在这里准备阶段过后的value值为0，而不是1。赋值为1的动作在初始化阶段。

当然还有其他的默认值。

![](https://pics2.baidu.com/feed/962bd40735fae6cdf616ed21e7a1f42043a70fe3.png?token=531148998856e5d92d0b437d9f9131b2&s=1AAA7423131A4DC8585DB1CB0300C0B1)

 注意，在上面 value 是被 static 所修饰的准备阶段之后是0，但是如果同时被 final 和 static 修饰准备阶段之后就是1了。我们可以理解为 static final 在编译器就将结果放入调用它的类的常量池中了。 

#### **4、解析**

解析阶段主要是虚拟机将常量池中的符号引用转化为直接引用的过程。什么是符号应用和直接引用呢？

符号引用：以一组符号来描述所引用的目标，可以是任何形式的字面量，只要是能无歧义的定位到目标就好，就好比在班级中，老师可以用张三来代表你，也可以用你的学号来代表你，但无论任何方式这些都只是一个代号（符号），这个代号指向你（符号引用）直接引用：直接引用是可以指向目标的指针、相对偏移量或者是一个能直接或间接定位到目标的句柄。和虚拟机实现的内存有关，不同的虚拟机直接引用一般不同。解析动作主要针对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用点限定符7类符号引用进行。

#### **5、初始化**

这是类加载机制的最后一步，在这个阶段，java程序代码才开始真正执行。我们知道，在准备阶段已经为类变量赋过一次值。在初始化阶端，程序员可以根据自己的需求来赋值了。一句话描述这个阶段就是执行类构造器< clinit >()方法的过程。

在初始化阶段，主要为类的静态变量赋予正确的初始值，JVM负责对类进行初始化，主要对类变量进行初始化。在Java中对类变量进行初始值设定有两种方式：

①声明类变量是指定初始值

②使用静态代码块为类变量指定初始值

JVM 初始化步骤

1、假如这个类还没有被加载和连接，则程序先加载并连接该类

2、假如该类的直接父类还没有被初始化，则先初始化其直接父类

3、假如类中有初始化语句，则系统依次执行这些初始化语句

类初始化时机：只有当对类的主动使用的时候才会导致类的初始化，类的主动使用包括以下六种：

创建类的实例，也就是 new 的方式访问某个类或接口的静态变量，或者对该静态变量赋值调用类的静态方法反射（如 Class.forName(“com.shengsiyuan.Test”)）初始化某个类的子类，则其父类也会被初始化Java虚拟机启动时被标明为启动类的类（ JavaTest），直接使用 java.exe命令来运行某个主类好了，到目前为止就是类加载机制的整个过程，但是还有一个重要的概念，那就是类加载器。在加载阶段其实我们提到过类加载器，说是在后面详细说，在这就好好地介绍一下类加载器。

### **类加载器**

虚拟机设计团队把加载动作放到JVM外部实现，以便让应用程序决定如何获取所需的类。

#### **1、Java 语言系统自带有三个类加载器:**

Bootstrap ClassLoader ：最顶层的加载类，主要加载核心类库，也就是我们环境变量下面 %JRE_HOME%\lib 下的 rt.jar、resources.jar、charsets.jar 和class等。另外需要注意的是可以通过启动 jvm 时指定 -Xbootclasspath和路径来改变 Bootstrap ClassLoader 的加载目录。比如 java -Xbootclasspath/a:path 被指定的文件追加到默认的 bootstrap 路径中。我们可以打开我的电脑，在上面的目录下查看，看看这些jar包是不是存在于这个目录。

Extention ClassLoader ：扩展的类加载器，加载目录 %JRE_HOME%\lib\ext 目录下的 jar 包和 class文件。还可以加载 -D java.ext.dirs 选项指定的目录。

Appclass Loader：也称为 SystemAppClass。 加载当前应用的 classpath 的所有类。

#### **2、类加载的三种方式**

认识了这三种类加载器，接下来我们看看类加载的三种方式。

（1）通过命令行启动应用时由 JVM 初始化加载含有 main() 方法的主类。

（2）通过 Class.forName() 方法动态加载，会默认执行初始化块（static{}），但是

​          Class.forName(name,initialize,loader) 中的 initialze 可指定是否要执行初始化块。

（3）通过 ClassLoader.loadClass() 方法动态加载，不会执行初始化块。

#### **3、双亲委派原则**

他的工作流程是： 当一个类加载器收到类加载任务，会先交给其父类加载器去完成，因此最终加载任务都会传递到顶层的启动类加载器，只有当父类加载器无法完成加载任务时，才会尝试执行加载任务。

#### **4、自定义类加载器**

在这一部分第一小节中，我们提到了 java 系统为我们提供的三种类加载器，还给出了他们的层次关系图，最下面就是自定义类加载器，那么我们如何自己定义类加载器呢？这主要有两种方式

（1）遵守双亲委派模型：继承 ClassLoader，重写 findClass() 方法。

（2）破坏双亲委派模型：继承 ClassLoader,重写 loadClass() 方法。 通常我们推荐采用第一种方法自定义类加载器，最大程度上的遵守双亲委派模型。

### Java SPI 规范

[SPI 规范讲解](https://www.jianshu.com/p/46b42f7f593c)

数据库驱动加载接口实现类的加载, 日志门面接口实现类加载, Spring 中大量使用了 SPI, 比如：对 servlet3.0 规范对 ServletContainerInitializer 的实现、自动类型转换 Type Conversion SPI(Converter SPI、Formatter SPI)等.

Dubbo 中也大量使用 SPI 的方式实现框架的扩展, 不过它对 Java 提供的原生 SPI 做了封装，允许用户扩展实现Filter 接口

### Applet 应用更新

早起主要用于 applet 应用更新， 每次更新时从服务器中加载 java 字节码到本地

### Tomcat

tomcat 加载 web application jar

### 其他博客

[SPI 规范讲解](https://www.jianshu.com/p/46b42f7f593c)

[Java 类加载器](https://www.ibm.com/developerworks/cn/java/j-lo-classloader/)