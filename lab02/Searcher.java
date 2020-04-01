package edu.caltech.cs2.lab02;

public class Searcher {
    public static boolean isSorted(int[] array) {
        int prev = Integer.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            if (prev > array[i]) {
                return false;
            }
            prev = array[i];
        }
        return true;
    }

    public static int linearSearch(int needle, int[] haystack) {
        if (!isSorted(haystack))
            throw new IllegalArgumentException();
        int i;
        for (i = 0; i < haystack.length && haystack[i] <= needle; i++) {
            if (haystack[i] == needle)
                return i;
        }
        return -i - 1;
    }

    public static int binarySearch(int needle, int[] haystack) {
        int first = 0, last = haystack.length/2, mid;
        boolean found = false;
        while (first <= last) {
            mid = (first + last) / 2;
            if (haystack[mid] == needle)
                return mid;


        }
        return -1;
    }
}
