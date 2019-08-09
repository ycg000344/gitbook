# 概述

**单例模式（Singleton）**，保证一个类仅有一个实例，并提供一个访问它的全局访问点。



# Talk is cheap，just coding

## Java

```java
class Singleton {

    private Singleton() {

    }
    // double check
    private volatile static Singleton instance;
    public static Singleton getInstance1() {
        if (null == instance) {
            synchronized (Singleton.class) {
                if (null == instance) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
	// 静态内部类模式
    private static class SingletonHoler {
        private static Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance2(){
        return SingletonHoler.INSTANCE;
    }
}
```

### 静态内部类的优点

外部类加载时并不需要立即加载内部类，内部类不被加载则不去初始化 `INSTANCE` ，故而不占内存。即当`Singleton` 第一次被加载时，并不需要去加载`SingletonHoler`，只有当`getInstance()`方法第一次被调用时，才会去初始化`INSTANCE`。第一次调用`getInstance()`方法会导致虚拟机加载`SingletonHoler`类，这种方法不仅能确保线程安全，也能保证单例的唯一性，同时也延迟了单例的实例化。



## go

> todo

