# 栈

## 概述

栈是一种操作受限的数据结构，只支持入栈和出栈，其最大的特点是后进者先出。栈既可以采用数组实现，也可以用链表，两者在入栈、出栈的时间复杂度都为O(1)。


## 思考

1. 我们在讲栈的应用时，讲到用函数调用栈来保存临时变量，为什么函数调用要用“栈”来保存临时变量呢？用其他数据结构不行吗？

2. 我们都知道，JVM 内存管理中有个“堆栈”的概念。栈内存用来存储局部变量和方法调用，堆内存用来存储 Java 中的对象。那 JVM 里面的“栈”跟我们这里说的“栈”是不是一回事呢？如果不是，那它为什么又叫作“栈”呢？

内存中的堆栈和数据结构堆栈不是一个概念，可以说内存中的堆栈是真实存在的物理区，数据结构中的堆栈是抽象的数据存储结构。
          
内存空间在逻辑上分为三部分：代码区、静态数据区和动态数据区，动态数据区又分为栈区和堆区。
                  
代码区：存储方法体的二进制代码。高级调度（作业调度）、中级调度（内存调度）、低级调度（进程调度）控制代码区执行代码的切换。
                  
静态数据区：存储全局变量、静态变量、常量，常量包括final修饰的常量和String常量。系统自动分配和回收。
                  
栈区：存储运行方法的形参、局部变量、返回值。由系统自动分配和回收。
                  
堆区：new一个对象的引用或地址存储在栈区，指向该对象存储在堆区中的真实数据。


# CODE

## Node

```java
@Getter
@Setter
@ToString
public class Node {
    public String data;
    public Node next;
    public Node(String data) {
        this.data = data;
        this.next = null;
    }
}

```

## ArrayStack

```java
/**
 * 顺序栈
 * 入栈、出栈
 */
@Getter
@Setter
@ToString
public class ArrayStack {
    /**
     * 容器
     */
    private String[] data;
    /**
     * 容器内元素数量
     */
    private int count;
    /**
     * 容器大小
     */
    private int capacity;

    public ArrayStack(int capacity) {
        this.data = new String[capacity];
        this.count = 0;
        this.capacity = capacity;
    }
    public ArrayStack() {
        this(1 << 4);
    }
    /**
     * 入栈
     *
     * @param value
     */
    public void push(String value) {
        // 判断栈内空间
        if (Objects.equals(capacity, count)) {
            return;
        }
        if (StrUtil.isEmpty(value)) {
            return;
        }
        data[count++] = value;
    }
    /**
     * 出栈
     *
     * @return
     */
    public String pop() {
        if (Objects.equals(count, 0)) {
            return null;
        }
        String s = data[count - 1];
        data[count - 1] = null;
        count--;
        return s;
    }
    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack();
        for (int i = 0; i < 18; i++) {
            stack.push(i + "");
        }
        System.out.println(stack.toString());
        String tmp;
        while (StrUtil.isNotEmpty((tmp = stack.pop()))) {
            System.out.println(tmp);
        }
    }
}

```

## LinkedListStack

```java
/**
 * 链式栈
 * 入栈、出栈
 */
@Getter
@Setter
@ToString
public class LinkedListStack {
    private Node head = new Node(StrUtil.EMPTY);
    private int count = 0;
    /**
     * 入栈
     *
     * @param value
     */
    public void push(String value) {
        Node node = new Node(value);
        node.next = head;
        head = node;
        count++;
    }
    /**
     * 出栈
     *
     * @return
     */
    public String pop() {
        if (Objects.equals(count, 0)) {
            return StrUtil.EMPTY;
        }
        Node p = head;
        head = p.next;
        count--;
        return p.data;
    }
    public static void main(String[] args) {
        LinkedListStack stack = new LinkedListStack();
        for (int i = 0; i < 5; i++) {
            stack.push(i + "");
        }
        System.out.println(stack.toString());
        String tmp;
        while (StrUtil.isNotEmpty((tmp = stack.pop()))) {
            System.out.println(tmp);
        }
    }
}

```

