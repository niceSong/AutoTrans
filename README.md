# AutoTrans
类型自动转换工具，支持手动定义转换行为。轻松处理DAO和DTO对象之间转换。
## 功能
* 支持深层转换，即类中还有多层类。
* 支持集合转换，即类中含有集合。
* 支持手动指定转换结果。
## 使用
### 自动转换
```java
Object autoTarget = BeanTrans.converter(source, Target.class).startTrans();
```
### 手动转换
```java
Object diyTarget = BeanTrans.converter(source, Target.class)
        .mapping("str", "new ss")
        .mapping("str", ()->{return  "new ss:" + fun;})
        .startTrans();
```
* 第一个`mapping`函数表示：将目标类中`str`属性的值，手动设置为"new ss"。
* 第二个`mapping`函数表示：将目标类中`str`属性的值，手动设置为`lambda`的返回值。