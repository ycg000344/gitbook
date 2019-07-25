# 排序

常用的排序方法：冒泡排序、插入排序、选择排序、归并排序、快速排序、计数排序、基数排序、桶排序。

## 如何分析一个排序算法

### 排序算法的执行效率

1. 最好、最坏、平均情况时间复杂度
2. 时间复杂度的系数、常数、低阶
3. 比较次数、移动次数

### 排序算法的内存消耗

原地排序 Sorted in place）。

原地排序算法，就是特指空间复杂度是 O(1) 的排序算法。

### 排序算法的稳定性

稳定性：如果待排序的序列中存在值相等的元素，经过排序之后，相等元素之间原有的先后顺序不变。

**稳定排序算法可以保持金额相同的两个对象，在排序之后的前后顺序不变。**

## 时间复杂度区分

### O(n2)、基于比较

#### 冒泡排序

冒泡排序只会操作相邻的两个数据。每次冒泡操作都会对相邻的两个元素进行比较，看是否满足大小关系要求。如果不满足就让它俩互换。一次冒泡会让至少一个元素移动到它应该在的位置，重复 n 次，就完成了 n 个数据的排序工作。


#### 插入排序
<p>首先，我们将数组中的数据分为两个区间，<strong>已排序区间</strong>和<strong>未排序区间</strong>。初始已排序区间只有一个元素，就是数组的第一个元素。插入算法的核心思想是取未排序区间中的元素，在已排序区间中找到合适的插入位置将其插入，并保证已排序区间数据一直有序。重复这个过程，直到未排序区间中元素为空，算法结束。</p>


#### 选择排序

选择排序算法的实现思路有点类似插入排序，也分已排序区间和未排序区间。但是选择排序每次会从未排序区间中找到最小的元素，将其放到已排序区间的末尾。

#### 思考

冒泡排序和插入排序的时间复杂度都是 O(n2)，都是原地排序，插入排序优于冒泡的原因：

冒泡排序不管怎么优化，元素交换的次数是一个固定值，是原始数据的逆序度。插入排序是同样的，不管怎么优化，元素移动的次数也等于原始数据的逆序度。

但是，从代码实现上来看，冒泡排序的数据交换要比插入排序的数据移动要复杂，冒泡排序需要 3 个赋值操作，而插入排序只需要 1 个。

### O(nLogn)、基于比较

#### 快排

快排的思想是这样的：如果要排序数组中下标从 p 到 r 之间的一组数据，我们选择 p 到 r 之间的任意一个数据作为 pivot（分区点）。

我们遍历 p 到 r 之间的数据，将小于 pivot 的放到左边，将大于 pivot 的放到右边，将 pivot 放到中间。经过这一步骤之后，数组 p 到 r 之间的数据就被分成了三个部分，前面 p 到 q-1 之间都是小于 pivot 的，中间是 pivot，后面的 q+1 到 r 之间是大于 pivot 的。

#### 归并

如果要排序一个数组，我们先把数组从中间分成前后两部分，然后对前后两部分分别排序，再将排好序的两部分合并在一起，这样整个数组就都有序了。


### O(n)、不基于比较

线性排序，非基于比较的排序算法，不涉及元素之间的比较操作。

#### 桶排序

核心思想是将要排序的数据分到几个有序的桶里，每个桶里的数据再单独进行排序。桶内排完序之后，再把每个桶里的数据按照顺序依次取出，组成的序列就是有序的了。

<p>如果要排序的数据有 n 个，我们把它们均匀地划分到 m 个桶内，每个桶里就有 k=n/m 个元素。每个桶内部使用快速排序，时间复杂度为 O(k * logk)。m 个桶排序的时间复杂度就是 O(m * k * logk)，因为 k=n/m，所以整个桶排序的时间复杂度就是 O(n*log(n/m))。当桶的个数 m 接近数据个数 n 时，log(n/m) 就是一个非常小的常量，这个时候桶排序的时间复杂度接近 O(n)。</p>

#### 计数排序

<p><strong>计数排序其实是桶排序的一种特殊情况</strong>。当要排序的 n 个数据，所处的范围并不大的时候，比如最大值是 k，我们就可以把数据划分成 k 个桶。每个桶内的数据值都是相同的，省掉了桶内排序的时间。</p>

#### 基数排序

# CODE

## Sorts

```java
/**
 * 排序
 */
public class Sorts {
    /**
     * 冒泡排序
     * <p>
     * 冒泡的过程只涉及相邻数据的交换操作，只需要常量级的临时空间，所以它的空间复杂度为 O(1)，是一个原地排序算法。
     * 在冒泡排序中，只有交换才可以改变两个元素的前后顺序。为了保证冒泡排序算法的稳定性，当有相邻的两个元素大小相等的时候，我们不做交换，相同大小的数据在排序前后不会改变顺序，所以冒泡排序是稳定的排序算法。
     * 最好情况下，要排序的数据已经是有序的了，我们只需要进行一次冒泡操作，就可以结束了，所以最好情况时间复杂度是 O(n)。而最坏的情况是，要排序的数据刚好是倒序排列的，我们需要进行 n 次冒泡操作，所以最坏情况时间复杂度为 O(n2）
     *
     * @param array
     * @param length
     */
    public void bubbleSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            // 数据交换的标识
            boolean flag = false;
            for (int j = 0; j < length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                    // 有数据交换
                    flag = true;
                }
            }
            // 没有数据交换，结束循环
            if (!flag) {
                break;
            }
        }
    }
    /**
     * 插入排序
     *
     * <p>首先，我们将数组中的数据分为两个区间，
     * <strong>已排序区间</strong>和<strong>未排序区间</strong>。
     * 初始已排序区间只有一个元素，就是数组的第一个元素。
     * 插入算法的核心思想是取未排序区间中的元素，在已排序区间中找到合适的插入位置将其插入，并保证已排序区间数据一直有序。
     * 重复这个过程，直到未排序区间中元素为空，算法结束。</p>
     * <p>
     * 从实现过程可以很明显地看出，插入排序算法的运行并不需要额外的存储空间，所以空间复杂度是 O(1)，也就是说，这是一个原地排序算法。
     * <p>
     * 在插入排序中，对于值相同的元素，我们可以选择将后面出现的元素，插入到前面出现元素的后面，这样就可以保持原有的前后顺序不变，所以插入排序是稳定的排序算法。
     *
     * <p>如果要排序的数据已经是有序的，我们并不需要搬移任何数据。如果我们从尾到头在有序数据组里面查找插入位置，每次只需要比较一个数据就能确定插入的位置。所以这种情况下，最好是时间复杂度为 O(n)。注意，这里是<strong>从尾到头遍历已经有序的数据</strong>。</p>
     *
     * <p>如果数组是倒序的，每次插入都相当于在数组的第一个位置插入新的数据，所以需要移动大量的数据，所以最坏情况时间复杂度为 O(n<sup>2</sup>)。</p>
     * <p>
     * 对于插入排序来说，每次插入操作都相当于在数组中插入一个数据，循环执行 n 次插入操作，所以平均时间复杂度为 O(n<sup>2</sup>)。
     *
     * @param array
     * @param length
     */
    public void insertionSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        // 未排序区间
        for (int i = 1; i < length; i++) {
            // 取未排序区间中的元素
            int value = array[i];
            // 初始已排序区间只有一个元素，就是数组的第一个元素。
            int j = i - 1;
            // 在已排序区间中找到合适的插入位置
            for (; j >= 0; j--) {
                if (array[j] > value) {
                    // 已经排序的区间中的元素 > 该未排序的元素，将大的元素向后搬移一位
                    array[j + 1] = array[j];
                } else {
                    // 已排序区间的最大元素 < 该未排序的元素，结束循环
                    break;
                }
            }
            array[j + 1] = value;
        }
        for (int i : array) {
            System.out.println(i);
        }
    }
    /**
     * 选择排序
     * 选择排序算法的实现思路有点类似插入排序，也分已排序区间和未排序区间。但是选择排序每次会从未排序区间中找到最小的元素，将其放到已排序区间的末尾。
     *
     * @param array
     * @param length
     */
    public void selectionSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        for (int i = 0; i < length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < length; j++) {
                if (array[minIndex] > array[j]) {
                    minIndex = j;
                }
            }
            if (!Objects.equals(minIndex, i)) {
                int tmp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = tmp;
            }
        }

        for (int i : array) {
            System.out.println(i);
        }
    }
    /**
     * 归并排序
     *
     * @param array
     * @param length
     */
    public void mergeSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        mergeSort(array, 0, length - 1);
        for (int i : array) {
            System.out.println(i);
        }
    }
    /**
     * @param array
     * @param start
     * @param end
     */
    private void mergeSort(int[] array, int start, int end) {
        if (start >= end) {
            return;
        }
        int middle = start + (end - start) / 2;
        mergeSort(array, 0, middle);
        mergeSort(array, middle + 1, end);
        merge(array, start, middle, end);
    }
    /**
     * 申请一个临时数组 tmp，大小与 A[start…end] 相同。
     * 我们用两个游标 i 和 j，分别指向 A[start…middle] 和 A[middle+1…end] 的第一个元素。
     * 比较这两个元素 A[i] 和 A[j]，
     * 如果 A[i]<=A[j]，我们就把 A[i] 放入到临时数组 tmp，并且 i 后移一位，
     * 否则将 A[j] 放入到数组 tmp，j 后移一位。
     *
     * @param array
     * @param start
     * @param middle
     * @param end
     */
    private void merge(int[] array, int start, int middle, int end) {
        int k = 0;
        int i = start;
        int j = middle + 1;
        int[] tmp = new int[end - start + 1];
        while (i <= middle && j <= end) {
            if (array[i] < array[j]) {
                tmp[k++] = array[i++];
            } else {
                tmp[k++] = array[j++];
            }
        }
        // 判断哪个子数组中有剩余的数据
        int _start = i;
        int _end = middle;
        if (j <= end) {
            _start = j;
            _end = end;
        }
        while (_start <= _end) {
            tmp[k++] = array[_start++];
        }
        // 将tmp中的数组拷贝回a[p...r]
        for (int l = 0; l < (end - start + 1); l++) {
            array[start + l] = tmp[l];
        }
    }
    /**
     * 快速排序，快排
     *
     * @param array
     * @param length
     */
    public void quickSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        quickSort(array, 0, length - 1);
        for (int i : array) {
            System.out.println(i);
        }
    }
    private void quickSort(int[] array, int start, int end) {
        if (start >= end) {
            return;
        }
        int partition = partition(array, start, end);
        quickSort(array, start, partition - 1);
        quickSort(array, partition + 1, end);
    }
    /**
     * partition() 分区函数
     * 随机选择一个元素作为 pivot（一般情况下，可以选择 start 到 end 区间的最后一个元素），然后对 A[start…end] 分区，函数返回 pivot 的下标。
     *
     * @param array
     * @param start
     * @param end
     * @return
     */
    private int partition(int[] array, int start, int end) {
        int pivot = array[end];
        int i = start;
        for (int j = start; j < end; j++) {
            if (array[j] < pivot) {
                int tmp = array[j];
                array[j] = array[i];
                array[i++] = tmp;
            }
        }
        int tmp = array[i];
        array[i] = array[end];
        array[end] = tmp;
        return i;
    }
    /**
     * 计数排序
     *
     * @param array
     * @param length
     */
    public void countSort(int[] array, int length) {
        if (1 >= length) {
            return;
        }
        // 查找数组的范围
        int max = array[0];
        for (int i : array) {
            if (max < i) {
                max = i;
            }
        }
        // 桶
        int[] c = new int[max + 1];
        // 计算每个元素的个数，放入c中
        for (int i : array) {
            c[i]++;
        }
        // 依次累加
        for (int i = 1; i < c.length; i++) {
            c[i] += c[i - 1];
        }
        // 临时数组r,存储排序后的数据
        int[] r = new int[length];
        for (int i = array.length - 1; i >= 0; i--) {
            int value = array[i];
            int index = c[value] - 1;
            r[index] = value;
            c[value] = index;
        }
        // 数据转移
        for (int i = 0; i < r.length; i++) {
            array[i] = r[i];
        }
        for (int i = 0; i < array.length; i++) {
            System.out.println(i + ":" + array[i]);
        }
    }
    public static void main(String[] args) {
        Sorts sorts = new Sorts();
        /*int[] array = new int[]{4, 5, 6, 1, 3, 2};*/
        /*sorts.bubbleSort(array, array.length);*/

        /*sorts.insertionSort(array, array.length);*/

        /*sorts.selectionSort(array, array.length);*/

        /*sorts.mergeSort(array, array.length);*/

        /*int[] array = new int[]{6, 11, 3, 9, 8};*/
        /*sorts.quickSort(array, array.length);*/

        int[] array = new int[]{2, 5, 3, 0, 2, 3, 0, 3};
        sorts.countSort(array, array.length);
    }
}

```

