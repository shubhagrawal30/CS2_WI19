package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;

public class MoveToFrontDictionary<K, V> implements IDictionary<K,V> {
    private Node<K, V> root;
    private int size;

    public MoveToFrontDictionary() {
        this.size = 0;
    }

    @Override
    public V get(K key) {
        Node<K, V> cur = root;
        Node<K, V> prev = null;
        while (cur != null) {
            if (cur.key.equals(key)) {
                if (prev != null) {
                    prev.next = prev.next.next;
                    cur.next = root;
                    root = cur;
                }
                return cur.value;
            }
            prev = cur;
            cur = cur.next;
        }
        return null;
    }

    @Override
    public V remove(K key) {
        Node<K, V> cur = root;
        Node<K, V> prev = null;
        while (cur != null) {
            if (cur.key.equals(key)) {
                V value = cur.value;
                if (prev == null) {
                    root = cur.next;
                } else {
                    prev.next = prev.next.next;
                }
                this.size--;
                return value;
            }
            prev = cur;
            cur = cur.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        Node<K, V> cur = root;
        while (cur != null) {
            if (cur.key.equals(key)) {
                V oldValue = cur.value;
                cur.value = value;
                return oldValue;
            }
            cur = cur.next;
        }
        size++;
        cur = new Node(key, value);
        cur.next = root;
        root = cur;
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        Node<K, V> cur = root;
        while (cur != null) {
            if (cur.key.equals(key))
                return true;
            cur = cur.next;

        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        Node<K, V> cur = root;
        while (cur != null) {
            if (cur.value.equals(value))
                return true;
            cur = cur.next;

        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keys = new LinkedDeque<K>();
        Node<K, V> cur = root;
        while (cur != null) {
            keys.add(cur.key);
            cur = cur.next;
        }
        return keys;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<V>();
        Node<K, V> cur = root;
        while (cur != null) {
            values.add(cur.value);
            cur = cur.next;
        }
        return values;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    private class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
