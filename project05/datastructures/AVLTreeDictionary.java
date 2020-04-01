package edu.caltech.cs2.datastructures;

public class AVLTreeDictionary<K extends Comparable<K>, V> extends BSTDictionary<K, V> {
    public V put(K key, V value) {
        if (root == null) {
            size = 1;
            root = new AVLNode(key, value);
            return null;
        }
        if (root.key.equals(key)) {
            V oldValue = root.value;
            root = new AVLNode((AVLNode) root, key, value);
            return oldValue;
        }
        ArrayDeque<AVLNode<K, V>> nodes = new ArrayDeque();
        ArrayDeque<Integer> paths = new ArrayDeque();
        AVLNode<K, V> cur = (AVLNode) root;
        while (cur != null && !cur.key.equals(key)) {
            nodes.push(cur);
            if (key.compareTo(cur.key) < 0) {
                paths.push(0);
                cur = (AVLNode) cur.children[0];
            } else {
                paths.push(1);
                cur = (AVLNode) cur.children[1];
            }
        }
        nodes.peek().children[paths.peek()] = new AVLNode(cur, key, value);


        if (cur == null) {
            size++;
            while (!nodes.isEmpty()) {
                AVLNode<K, V> node = nodes.pop();
                paths.pop();
                int balance = balance(node);
                if (balance > 1) {
                    if (node.children[1] != null && balance((AVLNode<K, V>) node.children[1]) < 0) {
                        if (node == root)
                            root = rotateRightLeft(node);
                        else
                            nodes.peek().children[paths.peek()] = rotateRightLeft(node);
                    } else {
                        if (node == root)
                            root = rotateLeft(node);
                        else
                            nodes.peek().children[paths.peek()] = rotateLeft(node);
                    }

                } else if (balance < -1) {
                    if (node.children[0] != null && balance((AVLNode<K, V>) node.children[0]) > 0) {
                        if (node == root)
                            root = rotateLeftRight(node);
                        else
                            nodes.peek().children[paths.peek()] = rotateLeftRight(node);
                    } else {
                        if (node == root)
                            root = rotateRight(node);
                        else
                            nodes.peek().children[paths.peek()] = rotateRight(node);
                    }
                }
                updateHeight(node);
            }
            return null;
        }
        return cur.value;
    }

    private int balance(AVLNode<K, V> node) {
        int balance = 0;
        if (node.children[1] != null) {
            balance = ((AVLNode) node.children[1]).height;
        }
        if (node.children[0] != null) {
            balance -= ((AVLNode) node.children[0]).height;
        }
        return balance;
    }

    private void updateHeight(AVLNode<K, V> node) {
        int height = 0;
        if (node.children[1] != null) {
            height = ((AVLNode) node.children[1]).height;
        }
        if (node.children[0] != null) {
            height = Math.max(height, ((AVLNode) node.children[0]).height);
        }
        node.height = height + 1;
    }

    private AVLNode<K, V> rotateRightLeft(AVLNode<K, V> node) {
        node.children[1] = rotateRight((AVLNode<K, V>) node.children[1]);
        return rotateLeft(node);
    }

    private AVLNode<K, V> rotateLeftRight(AVLNode<K, V> node) {
        node.children[0] = rotateLeft((AVLNode<K, V>) node.children[0]);
        return rotateRight(node);
    }

    private AVLNode<K, V> rotateRight(AVLNode<K, V> node) {
        AVLNode<K, V> t1 = (AVLNode) node.children[0];
        AVLNode<K, V> t2 = (AVLNode) t1.children[1];
        t1.children[1] = node;
        node.children[0] = t2;
        updateHeight(node);
        updateHeight(t1);
        return t1;
    }

    private AVLNode<K, V> rotateLeft(AVLNode<K, V> node) {
        AVLNode<K, V> t1 = (AVLNode) node.children[1];
        AVLNode<K, V> t2 = (AVLNode) t1.children[0];
        t1.children[0] = node;
        node.children[1] = t2;
        updateHeight(node);
        updateHeight(t1);
        return t1;
    }

    private static class AVLNode<K, V> extends BSTNode<K, V> {
        public int height;

        public AVLNode(K key, V value) {
            this(null, key, value);
        }

        public AVLNode(AVLNode<K, V> node, K key, V value) {
            super(node, key, value);
            this.height = 1;
        }
    }

    /**
     * Overrides the remove method in BST
     *
     * @param key
     * @return The value of the removed BSTNode if it exists, null otherwise
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
}



