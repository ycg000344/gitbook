父节点、子节点、兄弟节点、根节点、叶子节点/叶节点

**高度**： 节点到叶子节点到最长路径

在我们的生活中，“高度”这个概念，其实就是从下往上度量，比如我们要度量第 10 层楼的高度、第 13 层楼的高度，起点都是地面。所以，树这种数据结构的高度也是一样，从最底层开始计数，并且计数的起点是 0。

**深度**：根节点到该节点所经历到边的个数

“深度”这个概念在生活中是从上往下度量的，比如水中鱼的深度，是从水平面开始度量的。所以，树这种数据结构的深度也是类似的，从根结点开始度量，并且计数起点也是 0。

**层数**：节点的深度 + 1

“层数”跟深度的计算类似，不过，计数起点是 1，也就是说根节点的位于第 1 层。

# 二叉树

<p>二叉树，顾名思义，每个节点最多有两个“叉”，也就是两个子节点，分别是<strong>左子节点</strong>和<strong>右子<strong><strong>节</strong></strong>点</strong>。不过，二叉树并不要求每个节点都有两个子节点，有的节点只有左子节点，有的节点只有右子节点。</p>

## 满二叉树

<p>叶子节点全都在最底层，除了叶子节点之外，每个节点都有左右两个子节点，这种二叉树就叫作<strong>满二叉树</strong>。</p>

## 完全二叉树

<p>叶子节点都在最底下两层，最后一层的叶子节点都靠左排列，并且除了最后一层，其他层的节点个数都要达到最大，这种二叉树叫作<strong>完全二叉树</strong>。</p>


## 二叉树的遍历

每个节点最多会被访问两次，所以遍历操作的时间复杂度，跟节点的个数 n 成正比，也就是说二叉树遍历的时间复杂度是 O(n)。

### 前序遍历

前序遍历是指，对于树中的任意节点来说，先打印这个节点，然后再打印它的左子树，最后打印它的右子树。

前序遍历的递推公式：
```java
preOrder(r) = print r->preOrder(r->left)->preOrder(r->right)
```

### 中序遍历

中序遍历是指，对于树中的任意节点来说，先打印它的左子树，然后再打印它本身，最后打印它的右子树。

中序遍历的递推公式：
```java
inOrder(r) = inOrder(r->left)->print r->inOrder(r->right)
```

### 后序遍历

后序遍历是指，对于树中的任意节点来说，先打印它的左子树，然后再打印它的右子树，最后打印这个节点本身。

后序遍历的递推公式：
```java
postOrder(r) = postOrder(r->left)->postOrder(r->right)->print r
```

# 二叉查找树

二叉查找树要求，在树中的任意一个节点，其左子树中的每个节点的值，都要小于这个节点的值，而右子树节点的值都大于这个节点的值。

# code

## Node

```java
/**
 * 树节点
 */
@ToString
public class Node {
    public int data;
    public Node left;
    public Node right;
    public Node(int value) {
        this.data = value;
    }
}
```

## Tree

```java
/**
 * 树
 */
public class Tree {
    /**
     * 前序遍历
     * 先打印这个节点，然后再打印它的左子树，最后打印它的右子树。
     *
     * @param root
     */
    public void preOrder(Node root) {
        if (Objects.isNull(root)) {
            return;
        }
        System.out.println(root.data);
        preOrder(root.left);
        preOrder(root.right);
    }
    /**
     * 中序遍历
     * 先打印它的左子树，然后再打印它本身，最后打印它的右子树。
     *
     * @param root
     */
    public void inOrder(Node root) {
        if (Objects.isNull(root)) {
            return;
        }
        inOrder(root.left);
        System.out.println(root.data);
        inOrder(root.right);
    }
    /**
     * 后序遍历
     * 先打印它的左子树，然后再打印它的右子树，最后打印这个节点本身。
     *
     * @param root
     */
    public void postOrder(Node root) {
        if (Objects.isNull(root)) {
            return;
        }
        postOrder(root.left);
        postOrder(root.right);
        System.out.println(root.data);
    }
}

```

## BinarySearchTree

```java
/**
 * 二叉查找树
 * 二叉查找树要求，在树中的任意一个节点，其左子树中的每个节点的值，都要小于这个节点的值，而右子树节点的值都大于这个节点的值。
 */
public class BinarySearchTree {
    private Node root;
    /**
     * 二叉查找树
     *
     * @param value
     * @return
     */
    public Node find(int value) {
        if (Objects.isNull(root)) {
            return null;
        }
        Node p = root;
        while (Objects.nonNull(p)) {
            if (value == p.data) {
                return p;
            } else if (value > p.data) {
                p = p.right;
            } else {
                p = p.left;
            }
        }
        return null;
    }
    /**
     * @param value
     */
    public void insert(int value) {
        if (Objects.isNull(root)) {
            root = new Node(value);
        } else {
            Node p = root;
            while (Objects.nonNull(p)) {
                if (value < p.data) {
                    if (Objects.isNull(p.left)) {
                        p.left = new Node(value);
                        return;
                    } else {
                        p = p.left;
                    }
                } else {
                    if (Objects.isNull(p.right)) {
                        p.right = new Node(value);
                        return;
                    } else {
                        p = p.right;
                    }
                }
            }
        }
    }
    /**
     * @param value
     */
    public void delete(int value) {
        if (Objects.isNull(root)) {
            return;
        }
        Node p = root;
        // pp 是 p 的父节点
        Node pp = null;
        while (Objects.nonNull(p) && value != p.data) {
            pp = p;
            if (value > p.data) {
                p = p.right;
            } else {
                p = p.left;
            }
        }
        // 没有找到value的节点
        if (Objects.isNull(p)) {
            return;
        }
        // 被删除的节点有两个子节点，那么查找右树的最小子节点，转移到要删除的节点
        if (Objects.nonNull(p.left) && Objects.nonNull(p.right)) {
            Node minPP = p;
            Node minP = p.right;
            while (Objects.nonNull(minP.left)) {
                minPP = minP;
                minP = minP.left;
            }
            // 右树中值最小的节点的替换要删除节点的值
            p.data = minP.data;
            // 删除节点
            p = minP;
            pp = minPP;
        }
        // 被删除的节点只有一个子节点
        Node child;
        if (Objects.nonNull(p.left)) {
            child = p.left;
        } else if (Objects.nonNull(p.right)) {
            child = p.right;
        } else {
            child = null;
        }

        if (Objects.isNull(pp)) {
            root = child;
        } else if (pp.left == p) {
            pp.left = child;
        } else {
            pp.right = child;
        }
    }
    public int findMax() {
        if (Objects.isNull(root)) {
            return -1;
        } else {
            Node p = root;
            while (Objects.nonNull(p.right)) {
                p = p.right;
            }
            return p.data;
        }
    }
    public int findMin() {
        if (Objects.isNull(root)) {
            return -1;
        } else {
            Node p = root;
            while (Objects.isNull(p.left)) {
                p = p.left;
            }
            return p.data;
        }
    }
}

```

