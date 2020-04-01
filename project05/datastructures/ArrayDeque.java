package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    private E[] arr;
    private static final int iniCap = 10;
    private static final int growFac = 2;
    private int size;

    public ArrayDeque(){
        this.arr = (E[]) new Object[iniCap];
        this.size = 0;
    }

    public ArrayDeque(int initialCapacity){
        arr = (E[]) new Object[initialCapacity];
        this.size = 0;
    }

    public String toString(){
        if (this.size == 0)
            return "[]";
        String list = "";
        for(int i = this.size; i > 0; i--)
            list += ", " + this.arr[i-1];

        return "[" + list.substring(2) + "]";
    }

    private void ensureSize() {
        if (this.size >= this.arr.length) {
            E[] newData = (E[]) new Object[this.arr.length * growFac];
            for (int i = 0; i < this.size; i++) {
                newData[i] = this.arr[i];
            }
            this.arr = newData;
        }
    }

    @Override
    public void addFront(E e) {
        push(e);
    }


    @Override
    public void addBack(E e) {
        enqueue(e);
    }

    @Override
    public E removeFront() {
        return pop();
    }

    @Override
    public E removeBack() {
        if (this.size == 0)
            return null;
        E value = this.arr[0];
        for (int i = 1; i < this.size; i++)
            this.arr[i-1] = this.arr[i];
        this.size--;
        return value;
    }

    @Override
    public boolean enqueue(E e) {
        ensureSize();
        for(int i = this.size; i > 0; i--)
            this.arr[i] = this.arr[i-1];
        this.arr[0] = e;
        this.size++;
        return true;
    }

    @Override
    public E dequeue() {
        return pop();
    }

    @Override
    public boolean push(E e) {
        ensureSize();
        this.arr[this.size] = e;
        this.size++;
        return true;
    }

    @Override
    public E pop() {
        if (this.size == 0)
            return null;
        E value = peek();
        this.size--;
        return value;
    }

    @Override
    public E peek() {
        if (this.size == 0)
            return null;
        return this.arr[this.size-1];
    }

    @Override
    public E peekFront() {
        return peek();
    }

    @Override
    public E peekBack() {
        if (this.size == 0)
            return null;
        return this.arr[0];
    }

    @Override
    public Iterator<E> iterator() {
        return new ADIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    private class ADIterator implements Iterator<E> {

        private int currentIndex = 0;

        @Override
        public boolean hasNext() {
            return currentIndex < ArrayDeque.this.size();
        }

        @Override
        public E next() {
            E result = ArrayDeque.this.arr[this.currentIndex];
            this.currentIndex++;
            return result;
        }
    }
}
