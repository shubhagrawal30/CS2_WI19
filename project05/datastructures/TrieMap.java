package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.function.Function;
import java.util.Iterator;

public class TrieMap<A, K extends Iterable<A>, V> extends ITrieMap<A, K, V> {

    public TrieMap(Function<IDeque<A>, K> collector) {
        super(collector);
    }

    @Override
    public boolean isPrefix(K key) {
        TrieNode<A, V> n = root;
        for (A k : key) {
            if (!n.pointers.containsKey(k))
                return false;
            n = n.pointers.get(k);
        }
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        TrieNode<A, V> n = root;
        IDeque<V> d = new ArrayDeque<>();
        for (A k : prefix) {
            if (!n.pointers.containsKey(k))
                return d;
            n = n.pointers.get(k);
        }
        values(d, n);
        return d;
    }

    @Override
    public V get(K key) {
        TrieNode<A, V> n = root;
        for (A k : key) {
            if (!n.pointers.containsKey(k))
                return null;
            n = n.pointers.get(k);
        }
        return n.value;
    }

    @Override
    public V remove(K key) {
        TrieNode<A, V> n = root;
        TrieNode<A, V> p2 = null;
        A p1 = null;
        if (!containsKey(key))
            return null;
        for (A k : key) {
            if (n.pointers.size() > 1 || n.value != null || n == root) {
                p2 = n;
                p1 = k;
            }
            n = n.pointers.get(k);
        }
        V v = n.value;
        size--;
        if (!n.pointers.isEmpty())
            n.value = null;
        else
            p2.pointers.remove(p1);
        return v;
    }

    @Override
    public V put(K key, V value) {
        TrieNode<A, V> n = root;
        for (A k : key) {
            if (!n.pointers.containsKey(k))
                n.pointers.put(k, new TrieNode<>());
            n = n.pointers.get(k);
        }
        V v = n.value;
        n.value = value;
        if (v == null)
            size++;
        return v;
    }

    @Override
    public boolean containsKey(K key) {
        TrieNode<A, V> n = root;
        for (A k : key) {
            if (!n.pointers.containsKey(k))
                return false;
            n = n.pointers.get(k);
        }
        return n.value != null;
    }

    @Override
    public boolean containsValue(V value) {
        return containsValue(value, root);
    }

    private boolean containsValue(V value, TrieNode<A, V> n) {
        if (n.value == value)
            return true;
        for (TrieNode<A, V> k : n.pointers.values()) {
            boolean b = containsValue(value, k);
            if (b)
                return true;
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> v = new LinkedDeque<>();
        keySet(v, root, new ArrayDeque<A>());
        return v;
    }

    private void keySet(ICollection<K> c, TrieNode<A, V> n, ArrayDeque<A> l) {
        if (n.value != null)
            c.add(collector.apply(l));
        for (A k : n.pointers.keySet()) {
            l.add(k);
            keySet(c, n.pointers.get(k), l);
            l.removeFront();
        }
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> v = new ArrayDeque<>();
        values(v, root);
        return v;
    }

    private void values(ICollection<V> c, TrieNode<A, V> n) {
        if (n.value != null)
            c.add(n.value);
        for (TrieNode<A, V> k : n.pointers.values())
            values(c, k);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}