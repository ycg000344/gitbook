# 链表

## 引言

缓存清理策略：
1. 先进先出策略 FIFO（First In，First Out)
2. 最少使用策略 LFU（Least Frequently Used)
3. 最近最少使用策略 LRU（Least Recently Used)

## 与数组比较

数组需要**连续**的内存空间，对内存对要求比较高，而链表则是使用指针将**零散**的内存空间串联起来使用。

## 链表结构

### 单链表

头节点：记录链表的基地址

尾节点：指针不指向下一个节点，而是指向NULL


插入、删除时间复杂度：O(1)，因为只要操作相邻的节点改变指针

查询时间复杂度：O(n)，只能利用指针一个一个查询

### 循环链表

尾节点：指针指向头节点

### 双向链表

两个指针：后继指针NEXT指向后面的节点、前驱指针PREV指向前面的节点

实例：linkedHashMap

### 双向循环链表

# CODE

## Node

```java
@ToString
public class Node {
    public int data;
    public Node next;

    public Node(int data, Node next) {
        this.data = data;
        this.next = next;
    }
}

```

## LinkedListAlgo

```java
/**
 * 1) 单链表反转
 * 2) 链表中环的检测
 * 3) 两个有序的链表合并
 * 4) 删除链表倒数第n个结点
 * 5) 求链表的中间结点
 */
@Getter
@Setter
@ToString
public class LinkedListAlgo {
    /**
     * 单链表翻转
     *
     * @param node
     * @return
     */
    public Node reverse(Node node) {
        Node head = null;
        if (Objects.nonNull(node)) {
            Node prev = null;
            Node current = node;
            Node next;
            while (Objects.nonNull(current)) {
                next = current.next;
                // 尾节点为head
                if (Objects.isNull(next)) {
                    head = current;
                }
                // 翻转节点指针
                // 将后继指针next指向前面的节点
                current.next = prev;
                // 相邻两个节点依次前进一步
                prev = current;
                current = next;
            }
        }
        return head;
    }
    /**
     * 检测环
     *
     * @param node
     * @return
     */
    public boolean checkCircle(Node node) {
        if (Objects.isNull(node)) {
            return false;
        }
        Node fast = node.next;
        Node slow = node;
        while (Objects.nonNull(fast) && Objects.nonNull(fast.next)) {
            fast = fast.next.next;
            slow = slow.next;
        }
        if (Objects.equals(fast, slow)) {
            return true;
        }
        return false;
    }
    /**
     * 有序链表的合并
     *
     * @param a
     * @param b
     * @return
     */
    public Node mergeSoredLinks(Node a, Node b) {
        if (Objects.isNull(a)) {
            return b;
        }
        if (Objects.isNull(b)) {
            return a;
        }
        Node head;
        Node p = a;
        Node q = b;
        if (p.data < b.data) {
            head = p;
            p = p.next;
        } else {
            head = q;
            q = q.next;
        }
        Node current = head;
        while (Objects.nonNull(p) && Objects.nonNull(q)) {
            if (p.data < q.data) {
                current.next = p;
                p = p.next;
            } else {
                current.next = q;
                q = q.next;
            }
            current = current.next;
        }
        if (Objects.nonNull(p)) {
            current.next = p;
        } else {
            current.next = q;
        }
        return head;
    }
    /**
     * 删除倒数第K个结点
     *
     * @param node
     * @param k
     * @return
     */
    public Node deleteLastKNode(Node node, int k) {
        if (0 >= k) {
            return node;
        }
        if (Objects.isNull(node)) {
            return null;
        }

        Node fast = node;
        int i = 1;
        while (Objects.nonNull(fast) && i < k) {
            fast = fast.next;
            i++;
        }
        // k > node的节点数量
        if (Objects.isNull(fast)) {
            return node;
        }
        Node slow = node;
        Node prev = null;
        // 当fast走到最后一个节点的时候，slow正好是要被删除的节点元素
        while (Objects.nonNull(fast.next)) {
            fast = fast.next;
            prev = slow;
            slow = slow.next;
        }
        if (Objects.isNull(prev)) {
            node = node.next;
        } else {
            prev.next = prev.next.next;
        }
        return node;
    }
    /**
     * 中间节点
     * @param node
     * @return
     */
    public Node findMiddleNode(Node node) {
        if (Objects.isNull(node)) {
            return node;
        }
        Node fast = node;
        Node slow = node;
        while (Objects.nonNull(fast.next) && Objects.nonNull(fast.next.next)) {
            fast = fast.next.next;
            slow = slow.next;
        }
        return slow;
    }
}
```

## SinglyLinkedList

```java
/**
 * 单链表的插入、删除、查询
 * 数据类型为int
 */
@Getter
@Setter
@ToString
public class SinglyLinkedList {
    private Node head = null;
    public Node findByValue(int value) {
        Node p = head;
        while (Objects.nonNull(p) && !Objects.equals(p.data, value)) {
            p = p.next;
        }
        return p;
    }
    public Node findByIndex(int index) {
        Node p = head;
        int pos = 0;
        while (Objects.nonNull(p) && !Objects.equals(index, pos)) {
            p = p.next;
            pos++;
        }
        return p;
    }
    /**
     * 无头节点
     * 表头插入
     * 这种操作将于输入的顺序相反，逆序
     *
     * @param value
     */
    public void insertToHead(int value) {
        Node node = this.createNode(value);
        insertToHead(node);
    }
    public void insertToHead(Node node) {
        if (Objects.isNull(head)) {
            head = node;
        } else {
            // 每次使用node替换head
            node.next = head;
            head = node;
        }
    }
    /**
     * 顺序插入
     * 链尾插入
     *
     * @param value
     */
    public void insertTail(int value) {
        Node node = this.createNode(value);
        if (Objects.isNull(head)) {
            head = node;
        } else {
            Node p = head;
            while (Objects.nonNull(p.next)) {
                p = p.next;
            }
            p.next = node;
        }
    }
    /**
     * 将指定value插入到指定node后面
     *
     * @param prev
     * @param value
     */
    public void insertAfter(Node prev, int value) {
        Node next = this.createNode(value);
        this.insertAfter(prev, next);
    }
    public void insertAfter(Node prev, Node next) {
        if (Objects.nonNull(prev)) {
            next.next = prev.next;
            prev.next = next;
        }
    }
    /**
     * 将指定value插入到指定node的前面
     *
     * @param node
     * @param value
     */
    public void insertBefore(Node node, int value) {
        Node prev = this.createNode(value);
        insertBefore(node, prev);
    }
    public void insertBefore(Node node, Node prev) {
        if (Objects.nonNull(node)) {
            if (Objects.equals(head, node)) {
                insertToHead(prev);
            } else {
                Node p = head;
                while (Objects.nonNull(p) && !Objects.equals(p.next, node)) {
                    p = p.next;
                }
                if (Objects.nonNull(p)) {
                    prev.next = node;
                    p.next = prev;
                }
            }
        }
    }
    /**
     * 删除给定node
     *
     * @param node
     */
    public void deleteNode(Node node) {
        if (Objects.nonNull(head) && Objects.nonNull(node)) {
            if (Objects.equals(head, node)) {
                head = head.next;
            } else {
                Node p = head;
                while (Objects.nonNull(p) && !Objects.equals(p.next, node)) {
                    p = p.next;
                }
                if (Objects.nonNull(p)) {
                    p.next = p.next.next;
                }
            }
        }
    }
    /**
     * 删除给定的value
     *
     * @param value
     */
    public void deleteByValue(int value) {
        if (Objects.nonNull(head)) {
            Node p = head;
            Node q = null;
            while (Objects.nonNull(p) && !Objects.equals(p.data, value)) {
                q = p;
                p = p.next;
            }
            if (Objects.nonNull(p)) {
                if (Objects.isNull(q)) {
                    head = head.next;
                } else {
                    q.next = q.next.next;
                }
            }
        }
    }
    public boolean TFResult(Node left, Node right) {
        while (Objects.nonNull(left) && Objects.nonNull(right)) {
            if (Objects.equals(left.data, right.data)) {
                left = left.next;
                right = right.next;
                continue;
            } else {
                break;
            }
        }
        if (Objects.isNull(left) && Objects.isNull(right)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 回文判断
     *
     * @return
     */
    public boolean palindrome() {
        if (Objects.isNull(head)) {
            return false;
        } else {
            if (Objects.isNull(head.next)) {
                return false;
            } else {
                // 找到中间节点
                Node p = head;
                Node q = head;
                while (Objects.nonNull(q.next) && Objects.nonNull(q.next.next)) {
                    p = p.next;
                    q = q.next.next;
                }
                System.out.println("中间节点：" + p.toString());
                Node left;
                Node right;
                // q.next为空，那么链表节点一定为奇数，p为链表的中心节点
                if (Objects.isNull(q.next)) {
                    right = p.next;
                    left = inverseLinkList(p).next;
                } else {
                    // 链表节点数量为偶数
                    right = p.next;
                    left = inverseLinkList(p);
                }
                return TFResult(left, right);
            }
        }
    }
    /**
     * 无头翻转链表
     *
     * @param p
     * @return
     */
    public Node inverseLinkList(Node p) {
        if (Objects.isNull(p) || Objects.isNull(p.next)) {
            return p;
        } else {
            Node prev = null;
            Node next = null;
            Node r = head;

            while (!Objects.equals(r, p)) {
                next = r.next;
                r.next = prev;
                prev = r;
                r = next;
            }
            r.next = prev;
            return r;
        }
    }
    private Node createNode(int data) {
        return new Node(data, null);
    }
    public static void main(String[] args) {
        SinglyLinkedList link = new SinglyLinkedList();
        System.out.println("hi");
        int data[] = {1, 2, 5, 5, 2, 1};
        for (int i : data) {
            link.insertTail(i);
        }
        System.out.println(link.toString());
        System.out.println(link.palindrome());
    }
}
```

