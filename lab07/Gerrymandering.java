package edu.caltech.cs2.lab07;

public class Gerrymandering {

    private static int lower;
    private static int upper;

    public static boolean canGerrymander(int[] a, int[] b) {
        int[] diff = new int[a.length];
        int sum = 0;
        for (int i = 0; i < a.length; i++) {
            diff[i] = a[i] - b[i];
            sum += Math.abs(diff[i]);
        }
        upper = Math.abs(sum);
        lower = 0;
        Integer[][][][] memo = new Integer[a.length + 1][2*upper + 2][2*upper + 2][1 + a.length/2];

        return canGerrymander(diff, a.length, 0, 0, a.length/2, memo);

    }

    public static boolean canGerrymander(int[] arr, int i, int p, int q, int k, Integer[][][][] memo) {
        if (i == 0 && k == 0 && p > 0 && q > 0) {
            return true;
        }

        if (i <= 0 || k <= 0 || k > i) {
            return false;
        }

        if (memo[i][upper+p][upper+q][k] != null) {
            return memo[i][upper+p][upper+q][k] == 1;
        }

        int val = arr[i-1];

        boolean giveOne = true;
        boolean giveTwo = true;
        if (giveOne && giveTwo) {
            memo[i][upper+p][upper+q][k] = (canGerrymander(arr, i - 1, p + val, q, k - 1, memo) ||
                    canGerrymander(arr, i - 1, p, q + val, k, memo)) ? 1 : 0;

            return memo[i][upper+p][upper+q][k] == 1;
        }
        if (giveOne){
            memo[i][upper+p][upper+q][k] = canGerrymander(arr, i - 1, p + val, q, k - 1, memo) ? 1 : 0;
            return memo[i][upper+p][upper+q][k] == 1;
        }
        if (giveTwo) {
            memo[i][upper+p][upper+q][k] = canGerrymander(arr, i - 1, p, q + val, k, memo) ? 1 : 0;
            return memo[i][upper+p][upper+q][k] == 1;
        }
        return false;
    }
}