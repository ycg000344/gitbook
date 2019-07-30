# 概述

堆是一种特殊的树，满足两点即可视为堆：

1. 堆是一个完全二叉树
2. 堆中的每个节点的值都大于等于（或者小于等于）其子树中每个节点的值，大顶堆（小顶堆）


# code

## Heap

```java
/**
 * 堆
 * 1. 堆是一个完全二叉树
 * 2. 堆中的每个节点的值都大于等于（或者小于等于）其子树中每个节点的值，大顶堆（小顶堆）
 */
@ToString
public class Heap {
    private int[] data;
    private int capacity;
    private int count;
    public static void main(String[] args) {
        Heap heap = new Heap();
        for (int i = 0; i < heap.capacity + 3; i++) {
            heap.insert(RandomUtil.randomInt(1, 32));
        }
        System.out.println(heap.toString());
        heap.deleteMax();
        System.out.println(heap.toString());
    }
    public Heap() {
        this(1 << 4);
    }
    public Heap(int capacity) {
        this.data = new int[capacity + 1];
        this.capacity = capacity;
        this.count = 0;
    }
    /**
     * 向堆中插入一个元素，并从下往上堆化，heapify
     *
     * @param value
     */
    public boolean insert(int value) {
        // 满了
        if (count >= capacity) {
            return false;
        }
        count++;
        data[count] = value;
        int i = count;
        // 从下往上堆化
        while ((i >> 1) > 0 && data[i] > data[i >> 1]) {
            ArrayUtil.swap(data, i, i >> 1);
            i = i >> 1;
        }
        return true;
    }
    /**
     * 移除堆顶元素
     * 将最后一个元素替换堆顶元素，然后从上往下堆化
     */
    public void deleteMax() {
        // 空
        if (0 == count) {
            return;
        }
        data[1] = data[count];
        count--;
        heapifyUp2Down(data, count, 1);
    }
    /**
     * 从上往下进行堆化
     *
     * @param data
     * @param count
     * @param i
     */
    private void heapifyUp2Down(int[] data, int count, int i) {
        while (true) {
            int max = i;
            if ((i << 1) <= count && data[i] < data[i << 1]) {
                max = i << 1;
            }
            if (((i << 1) + 1) <= count && data[i] < data[(i << 1) + 1]) {
                max = (i << 1) + 1;
            }
            if (max == i) {
                break;
            }
            ArrayUtil.swap(data, i, max);
            i = max;
        }
    }
    /**
     * 建堆，时间复杂度为O(length)
     *
     * @param data
     * @param length
     */
    private void buildHead(int[] data, int length) {
        for (int i = length >> 1; i > 0; i--) {
            heapifyUp2Down(data, length, i);
        }
    }
    /**
     * 堆排序
     *
     * @param data
     * @param length
     */
    public void sort(int[] data, int length) {
        buildHead(data, length);
        int k = length;
        while (k > 1) {
            ArrayUtil.swap(data, 1, k);
            k--;
            heapifyUp2Down(data, k, 1);
        }
    }
}

```

