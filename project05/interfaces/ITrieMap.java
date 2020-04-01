package edu.caltech.cs2.interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class ITrieMap<A, K extends Iterable<A>, V> implements IDictionary<K, V> {
    protected TrieNode<A, V> root;
    protected Function<IDeque<A>, K> collector;
    protected int size;

    public ITrieMap(Function<IDeque<A>, K> collector) {
        this.collector = collector;
        this.size = 0;
        this.root = new TrieNode<>();
    }

    public abstract boolean isPrefix(K key);
    public abstract IDeque<V> getCompletions(K prefix);

    public void clear() {
        this.size = 0;
        this.root = new TrieNode<>();
    }

    protected static class TrieNode<A, V> {
        public final Map<A, TrieNode<A, V>> pointers;
        public V value;

        public TrieNode() {
            this(null);
        }

        public TrieNode(V value) {
            this.pointers = new HashMap<>();
            this.value = value;
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            if (this.value != null) {
                b.append("[" + this.value + "]-> {\n");
                this.toString(b, 1);
                b.append("}");
            }
            else {
                this.toString(b, 0);
            }
            return b.toString();
        }

        private String spaces(int i) {
            StringBuilder sp = new StringBuilder();
            for (int x = 0; x < i; x++) {
                sp.append(" ");
            }
            return sp.toString();
        }

        protected boolean toString(StringBuilder s, int indent) {
            boolean isSmall = this.pointers.entrySet().size() == 0;

            for (Map.Entry<A, TrieNode<A, V>> entry : this.pointers.entrySet()) {
                A idx = entry.getKey();
                TrieNode<A, V> node = entry.getValue();

                if (node == null) {
                    continue;
                }

                V value = node.value;
                s.append(spaces(indent) + idx + (value != null ? "[" + value + "]" : ""));
                s.append("-> {\n");
                boolean bc = node.toString(s, indent + 2);
                if (!bc) {
                    s.append(spaces(indent) + "},\n");
                }
                else if (s.charAt(s.length() - 5) == '-') {
                    s.delete(s.length() - 5, s.length());
                    s.append(",\n");
                }
            }
            if (!isSmall) {
                s.deleteCharAt(s.length() - 2);
            }
            return isSmall;
        }
    }

    @Override
    public String toString() {
        return this.root.toString();
    }
}
