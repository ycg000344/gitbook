# 概述
数组是一种线性表数据结构，用一组连续的内存空间，来存储一组具有相同类型的数据。
## 关键点
1. 线性表
2. 连续的内存空间和相同类型的数据结构
# 随机访问
## 寻址公式
```java
a[i]_address = base_address + i * data_type_size
```
数组是适合查询操作，但是查找到时间复杂度并不少O(1)，采用二分查找的时间复杂度为O(logn)。

所以正确的表述应该为：数组支持随机访问，根据下表随机访问的时间复杂度为O(1)。
# 插入
将value插入到array的第K个位置上去，快速的方法是将array[k]搬移到数组的最后，将value放置在第K个位置上去，时间复杂度为O(1)。

# 删除
每次删除的操作并非是真正的搬移数据，只是记录数据已经删除。当数组没有更多空间存储数据的时候，触发一次真正意义上的数据删除操作，大大减少了删除操作导致的数据搬移。

# 内容小结

数组用一块连续的内存空间来存储一组具有相同类型的数据，最大的特点是支持随机访问，但是插入、删除的效率低，平均时间复杂度为O(n)。在平时的业务开发可以采用容器，底层开发直接使用数组可能更合适。

# 课后思考

## JVM 标记清楚算法

大多数主流虚拟机采用可达性分析算法来判断对象是否存活，在标记阶段，会遍历所有 GC ROOTS，将所有 GC ROOTS 可达的对象标记为存活。只有当标记工作完成后，清理工作才会开始。

不足：1.效率问题。标记和清理效率都不高，但是当知道只有少量垃圾产生时会很高效。2.空间问题。会产生不连续的内存空间碎片。

## 二维数组内存寻址

对于 m * n 的数组，a [ i ][ j ] (i < m,j < n)的地址为：

```
address = base_address + ( i * n + j) * type_size
```

# code

## Array.java

```java
/**
 * 1. 数组中的数据类型为int
 * 2. 数组的插入、删除、根据下表随机访问
 *
 * @author lupo
 */
public class Array {
    /**
     * 定义整型数据data保存数据
     */
    private int[] data;
    /**
     * 定义数组长度
     */
    private int length;
    /**
     * 定义中实际个数
     */
    private int count;

    /**
     * 构造方法，数组大小
     * @param capacity
     */
    public Array(int capacity) {
        this.data = new int[capacity];
        this.length = capacity;
        this.count = 0;
    }
    /**
     * 根据索引为查询元素
     * @param index
     */
    public int find(int index) {
        if (index < 0 || index >= count) {
            return -1;
        }
        return data[index];
    }
    /**
     * 根据索引删除元素
     * @param index
     */
    public int delete(int index) {
        if (index < 0 || index >= count) {
            return -1;
        }
        int remove = data[index];
        // 索引位置后面的元素向前搬移一位
        for (int i = index + 1; i < count; i++) {
            data[i - 1] = data[i];
        }
        count--;
        return remove;
    }
    /**
     * 将value添加到index位置上去
     * @param index
     * @param value
     */
    public boolean add(int index, int value) {
        // 数组已经满了
        if (Objects.equals(this.length, this.count)) {
            System.out.println("数组已经满了");
            return false;
        }
        // 位置不合法
        if (index < 0 || index >= count) {
            System.out.println("index 异常");
            return false;
        }
        // 位置合法
        for (int i = count; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        count++;
        return true;
    }
}
```

## GenericArray<T>.java

```java
public class GenericArray<T> {
    private T[] data;
    private int count;

    /**
     * 带参数初始化
     *
     * @param capacity
     */
    public GenericArray(int capacity) {
        data = (T[]) new Object[capacity];
        count = 0;
    }
    /**
     * 无参构造函数
     */
    public GenericArray() {
        this(1 << 4);
    }

    /**
     * 数组的容量
     */
    public int capacity() {
        return data.length;
    }

    /**
     * 当前元素的数量
     */
    public int count() {
        return count;
    }
    /**
     * 数组是否为空
     */
    public boolean isEmpty() {
        return count == 0;
    }

    /**
     * 修改index位置的元素
     */
    public void set(int index, T e) {
        checkIndex(index);
        data[index] = e;
    }
    /**
     * 根据index查询
     */
    public T get(int index) {
        checkIndex(index);
        return data[index];
    }
    /**
     * 是否包含指定元素
     */
    public boolean contains(T e) {
        for (T t : data) {
            if (Objects.equals(e, t)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取指定元素的索引
     */
    public int find(T e) {
        for (int i = 0; i < data.length; i++) {
            if (Objects.equals(e, data[i])) {
                return i;
            }
        }
        return -1;
    }
    /**
     * 在index位置插入元素e
     */
    public void add(int index, T e) {
        checkIndex(index);
        // 如果当前数组已经满了，即 count = data.length, 则将数组扩容为原来的2倍，并且搬移数据
        if (count == data.length) {
            resize(data.length << 1);
        }
        for (int i = count; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = e;
        count++;
    }
    /**
     * 在第一位插入元素e
     */
    public void addFirst(T e) {
        add(0, e);
    }
    /**
     * 在最后一次添加元素e
     */
    public void addLast(T e) {
        add(count, e);
    }
    /**
     * 删除指定index的元素，并返回
     */
    public T remove(int index) {
        checkIndexForRemove(index);
        T result = data[index];

        for (int i = index + 1; i < count; i++) {
            data[i - 1] = data[i];
        }
        count--;
        // 缩容
        if (count == data.length >> 2 && data.length / 2  != 0) {
            resize(data.length >> 1);
        }
        return result;
    }
    private void checkIndexForRemove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("remove failed! Require index >=0 and index < count.");
        }
    }
    /**
     * 数组扩容，时间复杂度 为O(n)
     *
     * @param capacity
     */
    private void resize(int capacity) {
        T[] tmp = (T[]) new Object[capacity];
        for (int i = 0; i < data.length; i++) {
            tmp[i] = data[i];
        }
        data = tmp;
    }
    /**
     * 校验index是否合法
     *
     * @param index
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Add failed! Require index >=0 and index <= count.");
        }
    }
}

```

