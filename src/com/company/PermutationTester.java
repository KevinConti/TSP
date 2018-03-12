package com.company;

public class PermutationTester {

    static int[] val;
    static int now = -1;
    static int V = 6; //14 for real run
    static int count = 0;

    public static void p(int k) {
        now++;
        val[k] = now;
        if (now == V) handleP();
        for (int i = 1; i <= V; i++)
            if (val[i] == 0) p(i);
        now--;
        val[k] = 0;
    }

    public static void handleP() {
        count++;

        Main.usePermutation(val);
    }
}
