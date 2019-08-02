# BF 算法

> 在字符串 A 中查找字符串 B，那字符串 A 就是 *主串* ，字符串 B 就是 *模式串* 。我们把主串的长度记作 n，模式串的长度记作 m。因为我们是在主串中查找模式串，所以 n>m。

BF算法的核心思想就是**我们在主串中，检查起始位置分别是 0、1、2…n-m 且长度为 m 的 n-m+1 个子串，看有没有跟模式串匹配的**

# RK 算法

RK 算法的思路是这样的：我们通过哈希算法对主串中的 n-m+1 个子串分别求哈希值，然后逐个与模式串的哈希值比较大小。如果某个子串的哈希值与模式串相等，那就说明对应的子串和模式串匹配了（这里先不考虑哈希冲突的问题，后面我们会讲到）。因为哈希值是一个数字，数字之间比较是否相等是非常快速的，所以模式串和子串比较的效率就提高了。


# BM 算法

## 坏字符原则（bad character rule ）

我们从模式串的末尾往前倒着匹配，当我们发现某个字符没法匹配的时候。我们把这个没有匹配的字符叫作**坏字符（主串中的字符）**。

当发生不匹配的时候，我们把坏字符对应的模式串中的字符下标记作 si。如果坏字符在模式串中存在，我们把这个坏字符在模式串中的下标记作 xi。如果不存在，我们把 xi 记作 -1。那模式串往后移动的位数就等于 si-xi。（注意，我这里说的下标，都是字符在模式串的下标）。

### 可能出现的问题

单纯使用坏字符规则还是不够的。因为根据 si-xi 计算出来的移动位数，有可能是负数，比如主串是 aaaaaaaaaaaaaaaa，模式串是 baaa。不但不会向后滑动模式串，还有可能倒退。所以，BM 算法还需要用到“好后缀规则”。

## 好后缀原则（good suffix shift）

+ 在模式串中，查找跟好后缀字串匹配的另外一个字串
+ 在好后缀的后缀字串中，查找最长的、能跟模式串前缀字串匹配的字串


# code

## StringMatch

```java
/**
 * @ClassName: StringMatch
 * @Description: 字符串匹配算法
 * @Date: 2019/1/3 14:13
 * @Author lupo.f@outlook.com
 * @Version 1.0.0
 * @Since JDK 1.8
 */
public class StringMatch {
    private static final int SIZE = 256;
    /**
     * 模式串中个个字符的散列表，用于快速查找坏字符在模式串中的下标XI
     *
     * @param b:  模式串
     * @param m:  模式串长度
     * @param bc: 散列表
     * @return: void
     * @author: lupo.f@outlook.com
     * @date: 2019/1/3 14:17
     */
    private void generateBC(char[] b, int m, int[] bc) {
        for (int i = 0; i < SIZE; i++) {
            bc[i] = -1;
        }
        for (int i = 0; i < m; i++) {
            bc[(int) b[i]] = i;
        }
    }
    /**
     * BM算法
     *
     * @param a: 主串
     * @param n: 主串长度
     * @param b: 模式串
     * @param m: 模式串长度
     * @return: 模式串在主串中的下标位置
     * @author: lupo.f@outlook.com
     * @date: 2019/1/3 14:15
     */
    public int bm(char[] a, int n, char[] b, int m) {
        // 散列表
        int[] bc = new int[SIZE];
        // 构建散列表
        generateBC(b, m, bc);
        // 好后缀串的预处理
        int[] suffix = new int[m];
        boolean[] prefix = new boolean[m];
        generateGS(b, m, suffix, prefix);
        // 主串与模式串对齐的第一个字符下标位置
        int i = 0;
        while (i <= (n - m)) {
            // 从后往前进行对比字符
            int j;
            for (j = m - 1; j >= 0; j--) {
                if (a[i + j] != b[j]) {
                    // 坏字符所在位置 si = j
                    break;
                }
            }
            if (j < 0) {
                // 匹配成功
                return i;
            }
            // 模式串滑动长度 si - xi
            // i += (j - bc[(int) a[i + j]]);
            int x = j - bc[(int) a[i + j]];
            int y = 0;
            // 有好后缀的情况
            if (j < m - 1) {
                y = moveByGS(j, m, suffix, prefix);
            }
            i += Math.max(x, y);
        }
        return -1;
    }
    /**
     * TODO
     *
     * @param j:      坏字符对应的模式串中的字段的下标
     * @param m:      模式串的长度
     * @param suffix:
     * @param prefix:
     * @return: int
     * @author: lupo.f@outlook.com
     * @date: 2019/1/5 12:55
     */
    private int moveByGS(int j, int m, int[] suffix, boolean[] prefix) {
        // 好后缀的长度
        int k = m - 1 - j;
        if (suffix[k] != -1) {
            return j - suffix[k];
        }
        for (int r = j + 2; r <= m - 1; r++) {
            if (prefix[m - r] = true) {
                return r;
            }
        }
        return m;
    }
    /**
     * 好后缀原则处理前对模式串进行预处理
     * <p>
     * 拿下标从 0 到 m-2 的字串与模式串，求出公共后缀字串。
     * 如果公共后缀字串的长度为K，就记录 suffix[k] = j（j表示公共后缀字串的起始下标）
     * 如果j=0，那么说明公共后缀字串也是模式串的前缀字串，将 prefix[k] = true
     *
     * @param b:      模式串
     * @param m:      模式串的长度
     * @param suffix: 数组的下标K记录的是后缀字串的长度，下标对应的数组中的内容表示的是在模式串中与好后缀相匹配的字串的起始下标位置
     * @param prefix: 模式串的后缀字串能否匹配模式串的前缀字串
     * @return: void
     * @author: lupo.f@outlook.com
     * @date: 2019/1/3 16:03
     */
    private void generateGS(char[] b, int m, int[] suffix, boolean[] prefix) {
        for (int i = 0; i < m; i++) {
            suffix[i] = -1;
            prefix[i] = false;
        }
        // b[0,i] --> b[0, m-2]
        for (int i = 0; i < m - 1; i++) {
            int j = i;
            // 公共后缀字串的长度
            int k = 0;
            // 求公共后缀字串
            while (j >= 0 && b[j] == b[m - 1 - k]) {
                ++k;
                suffix[k] = j;
                --j;
            }
            // 如果公共后缀字串也是前缀字串
            if (-1 == j) {
                prefix[k] = true;
            }
        }
    }
}

```

