package edu.caltech.cs2.project08.datastructures;

import edu.caltech.cs2.project08.interfaces.IDeque;
import edu.caltech.cs2.project08.interfaces.IQueue;
import edu.caltech.cs2.project08.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {

    @Override
    public void addFront(E e) {

    }

    @Override
    public void addBack(E e) {

    }

    @Override
    public E removeFront() {
        return null;
    }

    @Override
    public E removeBack() {
        return null;
    }

    @Override
    public boolean enqueue(E e) {
        return false;
    }

    @Override
    public E dequeue() {
        return null;
    }

    @Override
    public boolean push(E e) {
        return false;
    }

    @Override
    public E pop() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    @Override
    public E peekFront() {
        return null;
    }

    @Override
    public E peekBack() {
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }
}

