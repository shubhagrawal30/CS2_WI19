package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private Node<E> start, end;
    private int size;

    public LinkedDeque(){
        this.start = null;
        this.end = null;
        this.size = 0;
    }

    public String toString(){
        if (this.size == 0)
            return "[]";
        String list = "";
        Node current = this.start;
        while(current != null){
            list += ", " + current.data;
            current = current.next;
        }

        return "[" + list.substring(2) + "]";
    }

    @Override
    public void addBack(E e) {
        if (this.size == 0) {
            this.start = new Node(e, null, null);
            this.end = this.start;
        }
        else {
            this.end.next = new Node(e, this.end, null);
            this.end = this.end.next;
        }
        this.size++;
    }

    @Override
    public void addFront(E e) {
        if (this.size == 0) {
            this.start = new Node(e, null, null);
            this.end = this.start;
        }
        else {
            this.start.last = new Node(e, null, this.start);
            this.start = this.start.last;
        }
        this.size++;
    }

    @Override
    public E removeBack() {
        if (this.size == 0)
            return null;
        if (this.size == 1) {
            this.size--;
            Node temp = this.start;
            this.start = null;
            this.end = null;
            return (E) temp.data;
        }
        Node temp = this.end;
        this.end = this.end.last;
        this.end.next = null;
        this.size--;
        return (E) temp.data;
    }

    @Override
    public E removeFront() {
        if (this.start == null)
            return null;
        this.size--;
        Node temp = this.start;
        this.start = this.start.next;
        return (E)temp.data;
    }

    @Override
    public boolean enqueue(E e) {
        addBack(e);
        return true;
    }

    @Override
    public E dequeue() {
        return removeFront();
    }

    @Override
    public boolean push(E e) {
        addFront(e);
        return true;
    }

    @Override
    public E pop() {
        return removeFront();
    }

    @Override
    public E peek() {
        return peekFront();
    }

    @Override
    public E peekBack() {
        if (this.start == null)
            return null;
        return (E) this.end.data;
    }

    @Override
    public E peekFront() {
        if (this.start == null)
            return null;
        return this.start.data;
    }

    @Override
    public Iterator<E> iterator() {
        return new LDIterator();
    }

    private class LDIterator implements Iterator<E> {

        private Node current = LinkedDeque.this.start;

        @Override
        public boolean hasNext() {
            return (null != current);
        }

        @Override
        public E next() {
            E result = (E) current.data;
            current = current.next;
            return result;
        }
    }
    @Override
    public int size() {
        return this.size;
    }

    private static class Node<E> {
        public final E data;
        public Node<E> next;
        public Node<E> last;

        public Node(E data) {
            this(data, null, null);
        }

        public Node(E data, Node<E> last, Node<E> next) {
            this.data = data;
            this.next = next;
            this.last = last;
        }
    }
}