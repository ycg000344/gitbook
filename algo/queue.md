# 队列

## 概述

先进者先出，这就是典型的**队列**

### 基本操作

1. 入队，放一个数据到队列尾部
2. 出队，从队列头部取一个元素

## 类型

### 顺序队列

采用数组实现的队列

*会出现数据搬移的清空*

### 链式队列

采用链表实现的队列

### 循环队列

队列满了的条件:
```java
(tail+1)%n=head
```

> 当队列满时，图中的 tail 指向的位置实际上是没有存储数据的。所以，循环队列会浪费一个数组的存储空间。

# CODE

## Node

```java
@Getter
@ToString
@Setter
public class Node {
    public String data;
    public Node next;
    public Node(String data) {
        this.data = data;
        this.next = null;
    }
}
```

## ArrayQueue

```java
/**
 * 使用数组实现的队列，顺序队列
 * 1. 入队，在尾部添加元素
 * 2. 出队，在头部取出元素
 */
@Getter
@Setter
@ToString
public class ArrayQueue {
    private String[] data;
    private int capacity;
    private int head;
    private int tail;
    public ArrayQueue() {
        this(1 << 4);
    }
    public ArrayQueue(int capacity) {
        this.data = new String[capacity];
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
    }
    /**
     * 入队
     * @param value
     * @return
     */
    public boolean enQueue(String value) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        if (Objects.equals(capacity, tail)) {
            // 容器满了 tail == capacity && head == 0
            if (Objects.equals(0, head)) {
                return false;
            }
            // 数据搬移
            for (int i = head; i < tail; i++) {
                data[i - head] = data[i];
            }
            tail -= head;
            head = 0;
        }
        data[tail++] = value;
        return true;
    }
    /**
     *  出队
     * @return
     */
    public String deQueue() {
        if (Objects.equals(head, tail)) {
            return null;
        }
        String tmp = data[head];
        data[head++] = null;
        return tmp;
    }
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        for (int i = 0; i < 5; i++) {
            queue.enQueue(i + "");
        }
        System.out.println(queue.toString());
        String tmp;
        while (StrUtil.isNotEmpty((tmp = queue.deQueue()))) {
            System.out.println(tmp);
        }
        System.out.println(queue.toString());
        for (int i = 4; i > -1; i++) {
            queue.enQueue(i + "");
        }
        System.out.println(queue.toString());
    }
}
```

## LinkedQueue

```java
/**
 * 使用链表实现的队列，链式队列
 * 1。 入队
 * 2。 出队
 */
@Getter
@Setter
@ToString
public class LinkedQueue {
    private Node head;
    private Node tail;
    public LinkedQueue() {
        this.head = new Node(StrUtil.EMPTY);
        this.tail = head;
    }
    /**
     * 入队
     *
     * @param value
     * @return
     */
    public boolean enQueue(String value) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        tail.next = new Node(value);
        tail = tail.next;
        return true;
    }
    /**
     * 出队
     *
     * @return
     */
    public String deQueue() {
        if (Objects.isNull(head.next)) {
            return null;
        }
        Node p = head.next;
        head.next = p.next;
        if (Objects.isNull(head.next)) {
            tail = head;
        }
        return p.data;
    }
    public static void main(String[] args) {
        LinkedQueue queue = new LinkedQueue();
        for (int i = 0; i < 4; i++) {
            queue.enQueue(i + "");
        }
        System.out.println(queue.toString());
        String tmp;
        while (StrUtil.isNotEmpty((tmp = queue.deQueue()))) {
            System.out.println(tmp);
        }
        System.out.println(queue.toString());
    }
}
```

## CircularQueue

```java
/**
 * 循环队列
 */
@Getter
@Setter
@ToString
public class CircularQueue {
    private String[] data;
    private int capacity;
    private int head;
    private int tail;
    public CircularQueue() {
        this(1 << 2);
    }
    public CircularQueue(int capacity) {
        this.data = new String[capacity];
        this.capacity = capacity;
        this.head = 0;
        this.tail = 0;
    }
    /**
     * 入队
     *
     * @param value
     * @return
     */
    public boolean enQueue(String value) {
        if (StrUtil.isEmpty(value)) {
            return false;
        }
        // 队列已经满了
        if (Objects.equals(head, (1 + tail) % capacity)) {
            return false;
        }
        data[tail] = value;
        tail = (tail + 1) % capacity;
        return true;
    }
    /**
     * 出队
     *
     * @return
     */
    public String deQueue() {
        if (Objects.equals(head, tail)) {
            return null;
        }
        String s = data[head];
        head =(head + 1) % capacity;
        return s;
    }
    public static void main(String[] args) {
        CircularQueue queue = new CircularQueue();
        for (int i = 0; i < 5; i++) {
            queue.enQueue(i + "");
        }
        System.out.println(queue.toString());
    }
}

```

