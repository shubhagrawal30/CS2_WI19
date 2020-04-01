package edu.caltech.cs2.lab07;

public class LongestCommonSubsequence {
    public static int findLCS(String a, String b) {
        Integer[][] memo = new Integer[a.length()][b.length()];
        return findLCS(a, b, a.length() - 1, b.length() - 1, memo);
    }

    private static int findLCS(String a, String b, int ia, int ib, Integer[][] memo) {
        if (ia < 0 || ib < 0)
            return 0;
        if (memo[ia][ib] != null)
            return memo[ia][ib];
        if (a.charAt(ia) == b.charAt(ib)) {
            return findLCS(a, b, ia - 1, ib - 1, memo) + 1;
        }
        int t = 0;
        if (ia > 0) {
            t = findLCS(a, b, ia - 1, ib, memo);
            memo[ia - 1][ib] = t;
        }
        if (ib > 0) {
            t = Math.max(t, findLCS(a, b, ia, ib - 1, memo));
            memo[ia][ib - 1] = t;
        }
        return t;
    }
}