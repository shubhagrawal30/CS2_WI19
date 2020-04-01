package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Iterator;

public class BSTDictionary<K extends Comparable<K>, V> implements IDictionary<K, V> {

    private static int LEFT = 0;
    private static int RIGHT = 1;

    protected BSTNode<K, V> root;
    protected int size;

    public BSTDictionary() {
        this.size = 0;
    }

    @Override
    public V get(K key) {
        BSTNode<K, V> cur = root;
        while (cur != null && cur.key != key) {
            if (key.compareTo(cur.key) < 0) {
                cur = cur.children[0];
            } else {
                cur = cur.children[1];
            }
        }
        return (cur != null ? cur.value : null);
    }

    @Override
    public V remove(K key) {
        if (root == null)
            return null;

        BSTNode<K, V> cur = root;
        BSTNode<K, V> prev = null;
        int lr = 0;
        while (cur != null && !cur.key.equals(key)) {
            prev = cur;
            if (key.compareTo(cur.key) < 0) {
                lr = LEFT;
                cur = cur.children[LEFT];
            } else {
                lr = RIGHT;
                cur = cur.children[RIGHT];
            }
        }
        if (cur == null) {
            return null;
        } else {
            size--;
            V value = cur.value;
            if (cur.isLeaf()) {
                if (cur == root)
                    root = null;
                else
                    prev.children[lr] = null;
            } else if (cur.children[RIGHT] == null) {
                prev.children[lr] = cur.children[LEFT];
            } else if (cur.children[LEFT] == null) {
                    prev.children[lr] = cur.children[RIGHT];
            } else {
                BSTNode<K, V> prevReplacement = cur;
                BSTNode<K, V> replacement = cur.children[RIGHT];
                while (replacement.children[LEFT] != null) {
                    prevReplacement = replacement;
                    replacement = replacement.children[LEFT];
                }
                cur.value = replacement.value;
                cur.key = replacement.key;
                if (prevReplacement.children[LEFT] == replacement)
                    prevReplacement.children[LEFT] = replacement.children[RIGHT];
                else
                    prevReplacement.children[RIGHT] = replacement.children[RIGHT];
            }
            return value;
        }
    }

    @Override
    public V put(K key, V value) {
        if (root == null) {
            size++;
            root = new BSTNode(key, value);
            return null;
        }
        if (root.key.equals(key)) {
            V oldValue = root.value;
            root = new BSTNode(root, key, value);
            return oldValue;
        }
        BSTNode<K, V> cur = root;
        BSTNode<K, V> prev = null;
        while (cur != null && !cur.key.equals(key)) {
            prev = cur;
            if (key.compareTo(cur.key) < 0) {
                cur = cur.children[0];
            } else {
                cur = cur.children[1];
            }
        }
        if (key.compareTo(prev.key) < 0) {
            prev.children[0] = new BSTNode(cur, key, value);
        } else {
            prev.children[1] = new BSTNode(cur, key, value);
        }
        if (cur == null) {
            size++;
            return null;
        }
        return cur.value;
    }

    @Override
    public boolean containsKey(K key) {
        BSTNode<K, V> cur = root;
        while (cur != null) {
            if (cur.key.equals(key))
                return true;
            if (key.compareTo(cur.key) < 0) {
                cur = cur.children[0];
            } else {
                cur = cur.children[1];
            }
        }
        return cur != null;
    }

    @Override
    public boolean containsValue(V value) {
        return containsValue(value, root);
    }

    private boolean containsValue(V value, BSTNode<K, V> node) {
        if (node == null)
            return false;
        if (node.value == value)
            return true;
        if (!node.isLeaf())
            return containsValue(value, node.children[0]) || containsValue(value, node.children[1]);
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keys = new LinkedDeque();
        keySet(keys, root);
        return keys;
    }

    private void keySet(ICollection<K> keys, BSTNode<K, V> node) {
        if (node != null) {
            keys.add(node.key);
            keySet(keys, node.children[0]);
            keySet(keys, node.children[1]);
        }
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> value = new LinkedDeque();
        values(value, root);
        return value;
    }

    private void values(ICollection<V> value, BSTNode<K, V> node) {
        if (node != null) {
            value.add(node.value);
            values(value, node.children[0]);
            values(value, node.children[1]);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
    
    @Override
    public String toString() {
        if (this.root == null) {
            return "{}";
        }

        StringBuilder contents = new StringBuilder();

        IQueue<BSTNode<K, V>> nodes = new ArrayDeque<>();
        BSTNode<K, V> current = this.root;
        while (current != null) {
            contents.append(current.key + ": " + current.value + ", ");

            if (current.children[0] != null) {
                nodes.enqueue(current.children[0]);
            }
            if (current.children[1] != null) {
                nodes.enqueue(current.children[1]);
            }

            current = nodes.dequeue();
        }

        return "{" + contents.toString().substring(0, contents.length() - 2) + "}";
    }

    protected static class BSTNode<K, V> {
        public K key;
        public V value;
        public BSTNode<K, V>[] children;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.children = (BSTNode<K, V>[]) new BSTNode[2];
        }

        public BSTNode(BSTNode<K, V> o) {
            this.key = o.key;
            this.value = o.value;
            this.children = (BSTNode<K, V>[]) new BSTNode[2];
            this.children[LEFT] = o.children[LEFT];
            this.children[RIGHT] = o.children[RIGHT];
        }

        public BSTNode(BSTNode<K, V> o, K key, V value) {
            this.key = key;
            this.value = value;
            if (o != null) {
                this.children = o.children;
            } else {
                this.children = (BSTNode<K, V>[]) new BSTNode[2];
            }
        }

        public boolean isLeaf() {
            return this.children[LEFT] == null && this.children[RIGHT] == null;
        }

        public boolean hasBothChildren() {
            return this.children[LEFT] != null && this.children[RIGHT] != null;
        }
    }
}
