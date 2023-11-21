## Mybatis 中的常用知识

### 打开资源管理器

```java
		/**
		 * 自动打开生成文件的目录
		 * <p>
		 * 根据 osName 执行相应命令
		 * </p>
		 */
		try {
			String osName = System.getProperty("os.name");
			if (osName != null) {
				if (osName.contains("Mac")) {
					Runtime.getRuntime().exec("open " + config.getSaveDir());
				} else if (osName.contains("Windows")) {
					Runtime.getRuntime().exec("cmd /c start " + config.getSaveDir());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
```

从这里我们学到了 mac 系统中 open 指令可以打开资源管理器，而 windows 中的指令是 cmd /c start



### File

```java
File mapperFile = new File(this.getFileName("mapper"), mapperName + ".java");
```

File(parent, child): 可以在 parent 目录下创建 child 文件



### 文件写入

```java
BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mapperFile), "utf-8"));
bw.write("package " + config.getMapperPackage() + ";");
bw.newLine();
bw.write("}");
bw.flush();
bw.close();
```



### 获取类下的所有字符列表

```java
	/**
	 * 获取该类的所有字符列表
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			result.add(field);
		}

		Class<?> superClass = clazz.getSuperclass();
		if (superClass.equals(Object.class)) {
			return result;
		}
		result.addAll(getAllFields(superClass));
		return result;
	}

	/**
	 * 获取该类的所有字符列表，排查 Transient 类型的字段
	 * 
	 * @param clazz
	 *            反射类
	 * @return
	 */
	public static List<Field> getAllFieldsExcludeTransient(Class<?> clazz) {
		List<Field> result = new LinkedList<Field>();
		List<Field> list = getAllFields(clazz);
		for (Field field : list) {
			if (Modifier.isTransient(field.getModifiers())) {
				continue;
			}
			result.add(field);
		}
		return result;
	}
```



# Java中的Type类型

在 Java 编程语言中，`Type`是所有类型的父接口。包括：

1. 原始类型（raw types），对应`Class`实现类
2. 参数化类型（parameterized types），对应`ParameterizedType`接口
3. 泛型数组类型（array types），对应`GenericArrayType`接口
4. 类型变量（type variables），对应`TypeVariable`接口
5. 基本类型（primitive types），对应`Class`实现类
6. 通配符类型（wildcard types），对应`WildcardType`接口



```java
public class Main {

    public static void main(String[] args) throws NoSuchMethodException, SecurityException {
        Method method = Main.class.getMethod("testType",
                List.class, List.class, List.class, List.class, List.class, Map.class);

        // 按照声明顺序返回`Type对象`的数组
        Type[] types = method.getGenericParameterTypes();

        for (int i = 0; i < types.length; i++) {
            // 最外层都是ParameterizedType
            ParameterizedType pType = (ParameterizedType) types[i];
            // 返回表示此类型【实际类型参数】的`Type对象`的数组
            Type[] actualTypes = pType.getActualTypeArguments();
            for (int j = 0; j < actualTypes.length; j++) {
                Type actualType = actualTypes[j];
                System.out.print("(" + i + ":" + j + ")  类型【" + actualType + "】");
                if (actualType instanceof Class) {
                    System.out.println(" -> 类型接口【" + actualType.getClass().getSimpleName() + "】");
                } else {
                    System.out.println(" -> 类型接口【" + actualType.getClass().getInterfaces()[0].getSimpleName() + "】");
                }
            }
        }
    }

    public <T> void testType(List<String> a1,
                             List<ArrayList<String>> a2,
                             List<T> a3,
                             List<? extends Number> a4,
                             List<ArrayList<String>[]> a5,
                             Map<String, Integer> a6) {
    }
}
```



```shell
(0:0)  类型【class java.lang.String】 -> 类型接口【Class】
(1:0)  类型【java.util.ArrayList<java.lang.String>】 -> 类型接口【ParameterizedType】
(2:0)  类型【T】 -> 类型接口【TypeVariable】
(3:0)  类型【? extends java.lang.Number】 -> 类型接口【WildcardType】
(4:0)  类型【java.util.ArrayList<java.lang.String>[]】 -> 类型接口【GenericArrayType】
(5:0)  类型【class java.lang.String】 -> 类型接口【Class】
(5:1)  类型【class java.lang.Integer】 -> 类型接口【Class】
```

[Java中的Type类型更多知识](https://www.jianshu.com/p/a84e485f8077)
