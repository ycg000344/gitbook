# 图

## 概述

1. 顶点
2. 边
3. 度，就是与顶点相连的边数
    1. 出度
    2. 入度
4. 带权图

## 存储方法

1. 邻接矩阵，是一个二维数组。
2. 邻接表，链表

## 搜索算法

### 广度优先搜索算法

地毯式搜索，借助队列实现，结果为顶点之间的最短路径

### 深度优先搜索算法

回溯思想，适合采用递归实现，借助栈实现。

# CODE

## Graph

```java
/**
 * 图
 * 无向图
 */
public class Graph {
    // 顶点个数
    private int capacity;
    // 邻接表
    private LinkedList<Integer>[] adj;
    public Graph() {
        this(1 << 4);
    }
    public Graph(int capacity) {
        this.capacity = capacity;
        adj = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            adj[i] = new LinkedList<>();
        }
    }
    /**
     * 无向图一条边存两次
     *
     * @param s 顶点
     * @param t 顶点
     */
    public void addEdge(int s, int t) {
        adj[s].add(t);
        adj[t].add(s);
    }
    /**
     * 广度优先搜索，Breadth-First-Search
     * 时间复杂度：
     * 终点t离起点s很远，需要遍历整图，那么每个顶点都需要出入队列，每条边都会访问一次，时间复杂度为O(V+E),V:顶点个数，E：边数，对于一个连同图来讲，边数要>= (V-1)，
     * 所以时间复杂度是O(E)
     * <p>
     * 空间复杂度：
     * 需要3个临时数组变量，visited、queue、prev，他们存储空间大小均不会大于V，所以空间复杂度为O(V)
     *
     * @param s 开始顶点
     * @param t 结束顶点
     */
    public void bfs(int s, int t) {
        if (s == t) {
            return;
        }
        /*
         用来记录已经被访问的顶点，避免顶点的重复访问，如果顶点q已经被访问，则将visited[q] = true
          */
        boolean[] visited = new boolean[capacity];
        visited[s] = true;
        /*
         用来存储已经被访问，但是相连的顶点还没被访问的顶点。
         因为广度优先搜索是逐层访问的，只有把第K层顶点访问完成之后，才能访问第K+1层顶点。
         当访问到第K层顶点的时候需要把第K层顶点记录下来，稍后才能通过第K层访问第K+1层顶点
          */
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        /*
        用来记录搜索路径，不过该路径是反向存储的。
        prev[w] 储存的是顶点w是从哪个前驱顶点来的。
        比如，通过顶点2的链接表访问到顶点3，那么prev[3] = 2。
         */
        int[] prev = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            prev[i] = -1;
        }
        while (queue.size() != 0) {
            int w = queue.poll();
            for (int i = 0; i < adj[w].size(); i++) {
                int q = adj[w].get(i);
                if (!visited[q]) {
                    prev[q] = w;
                    if (q == t) {
                        print(prev, s, t);
                        return;
                    }
                    visited[q] = true;
                    queue.add(q);
                }
            }
        }
    }
    /**
     * 递归打印s -> t
     *
     * @param prev
     * @param s
     * @param t
     */
    private void print(int[] prev, int s, int t) {
        if (prev[t] != -1 && t != s) {
            print(prev, s, prev[t]);
        }
        System.out.println(t + " ");
    }
    // 递归结束标示
    private boolean found = false;
    /**
     * 深度优先搜索
     * 回溯思想
     *
     * @param s 开始顶点
     * @param t 结束顶点
     */
    public void dfs(int s, int t) {
        found = false;
        boolean[] visited = new boolean[capacity];
        int[] prev = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            prev[i] = -1;
        }
        recurDfs(s, t, visited, prev);
        print(prev, s, t);
    }
    /**
     * 递归
     * @param w
     * @param t
     * @param visited
     * @param prev
     */
    private void recurDfs(int w, int t, boolean[] visited, int[] prev) {
        if (found) {
            return;
        }
        visited[w] = true;
        if (w == t) {
            found = true;
            return;
        }
        for (int i = 0; i < adj[w].size(); i++) {
            Integer q = adj[w].get(i);
            if (!visited[q]) {
                prev[q] = w;
                recurDfs(q, t, visited, prev);
            }
        }
    }
}

```

