# 二分查找

## 概述

二分查找针对的是一个有序的数据集合，查找思想有点类似分治思想。每次都通过跟区间的中间元素对比，将待查找的区间缩小为之前的一半，直到找到要查找的元素，或者区间被缩小为 0

时间复杂度为：O(logn)

# CODE

## BinarySearch

```java
/**
 * 二分查找
 */
public class BinarySearch {
    public static void main(String[] args) {
        BinarySearch search = new BinarySearch();
        int[] array = new int[]{1, 3, 4, 5, 7, 8, 8, 8, 8, 9, 11, 18, 19};
        int first = search.bsearchFirstE(array, array.length, 8);
        System.out.println(first);
        int last = search.bsearchLastE(array, array.length, 8);
        System.out.println(last);
        int[] data = new int[]{3, 4, 6, 7, 10};
        int firstGE = search.bsearchFirstGE(data, data.length, 6);
        System.out.println(firstGE);
        int lastLE = search.bsearchLastLE(data, data.length, 6);
        System.out.println(lastLE);
    }
    /**
     * 二分查找
     * 查找第一个等于给定值的下标
     *
     * @param array
     * @param length
     * @param value
     * @return
     */
    public int bsearchFirstE(int[] array, int length, int value) {
        return bsearchFirstE(array, 0, length - 1, value);
    }
    private int bsearchFirstE(int[] array, int low, int high, int value) {
        if (low > high) {
            return -1;
        }
        int mid = low + ((high - low) >> 1);
        if (value > array[mid]) {
            return bsearchFirstE(array, mid + 1, high, value);
        } else if (value < array[mid]) {
            return bsearchFirstE(array, low, mid - 1, value);
        } else {
            if (0 == mid || value != array[mid - 1]) {
                return mid;
            } else {
                return bsearchFirstE(array, low, mid - 1, value);
            }
        }
    }
    /**
     * 二分查找
     * 查找最后一个等于给定值的下标
     *
     * @param array
     * @param length
     * @param value
     * @return
     */
    public int bsearchLastE(int[] array, int length, int value) {
        return bsearchLastE(array, 0, length - 1, value);
    }
    private int bsearchLastE(int[] array, int low, int high, int value) {
        if (low > high) {
            return -1;
        }
        int mid = low + ((high - low) >> 1);
        if (value > array[mid]) {
            return bsearchLastE(array, mid + 1, high, value);
        } else if (value < array[mid]) {
            return bsearchLastE(array, low, mid - 1, value);
        } else {
            if ((high == mid) || (value != array[mid + 1])) {
                return mid;
            } else {
                return bsearchLastE(array, mid + 1, high, value);
            }
        }
    }
    /**
     * 二分查找
     * 查找第一个大于等于给定值的元素的下标
     *
     * @param array
     * @param length
     * @param value
     * @return
     */
    public int bsearchFirstGE(int[] array, int length, int value) {
        return bsearchFirstGE(array, 0, length - 1, value);

    }
    private int bsearchFirstGE(int[] array, int low, int high, int value) {
        if (low > high) {
            return -1;
        }
        int mid = low + ((high - low) >> 1);
        if (value < array[mid]) {
            return bsearchFirstGE(array, low, mid - 1, value);
        } else {
            if (0 == mid || array[mid - 1] < value) {
                return mid;
            } else {
                return bsearchFirstGE(array, low, mid - 1, value);
            }
        }
    }
    /**
     * 二分查找
     * 查找最后一个小于等于给定值的元素的下标
     *
     * @param array
     * @param length
     * @param value
     * @return
     */
    public int bsearchLastLE(int[] array, int length, int value) {
        return bsearchLastLE(array, 0, length - 1, value);
    }
    private int bsearchLastLE(int[] array, int low, int high, int value) {
        if (low > high) {
            return -1;
        }
        int mid = low + ((high - low) >> 1);
        if (value > array[mid]) {
            return bsearchLastLE(array, mid + 1, high, value);
        } else {
            if (high == mid || array[mid + 1] > value) {
                return mid;
            } else {
                return bsearchLastLE(array, mid + 1, high, value);
            }
        }
    }
}

```