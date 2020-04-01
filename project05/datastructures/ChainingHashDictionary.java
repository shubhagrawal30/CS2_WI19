package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDictionary;

import java.util.Iterator;
import java.util.function.Supplier;

public class ChainingHashDictionary<K, V> implements IDictionary<K, V> {
    private Supplier<IDictionary<K, V>> chain;
    private int size;
    private final float LOAD_FACTOR = 1.0f;

    private IDictionary<K, V>[] buckets;

    public ChainingHashDictionary(Supplier<IDictionary<K, V>> chain) {
        this.chain = chain;
        size = 0;
        buckets = (IDictionary<K, V>[]) new IDictionary[11];
    }

    private void rehash() {
        IDictionary<K, V>[] buckets = this.buckets;
        this.buckets = (IDictionary<K, V>[]) new IDictionary[buckets.length * 2 + 1];
        size = 0;
        for (IDictionary<K, V> bucket : buckets) {
            if (bucket != null) {
                Iterator<K> keys = bucket.keySet().iterator();
                Iterator<V> values = bucket.values().iterator();
                while (keys.hasNext())
                    put(keys.next(), values.next());
            }
        }
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % buckets.length);
    }

    @Override
    public V get(K key) {
        if (buckets[hash(key)] == null)
            return null;
        return buckets[hash(key)].get(key);
    }

    @Override
    public V remove(K key) {
        if (buckets[hash(key)] == null)
            return null;
        V value = buckets[hash(key)].remove(key);
        if (value != null)
            size--;
        return value;
    }

    @Override
    public V put(K key, V value) {
        if (buckets[hash(key)] == null)
            buckets[hash(key)] = chain.get();
        V oldValue = buckets[hash(key)].put(key, value);
        if (oldValue == null)
            size++;
        if (((double) size) / buckets.length > LOAD_FACTOR)
            rehash();
        return oldValue;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (IDictionary<K, V> bucket : buckets)
            if (bucket != null && bucket.containsValue(value))
                return true;
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keySet = new ArrayDeque();
        for (IDictionary<K, V> bucket : buckets)
            if (bucket != null)
                for (K key : bucket.keySet())
                    keySet.add(key);
        return keySet;
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new ArrayDeque();
        for (IDictionary<K, V> bucket : buckets)
            if (bucket != null)
                for (V value : bucket.values())
                    values.add(value);
        return values;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
