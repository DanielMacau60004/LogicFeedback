package com.logic.feedback.nd.algorithm.tests;

import java.util.Arrays;

public class BinaryGoalBackup {

    public static void main(String[] args) {
        byte[] b = create(111);
        byte[] b1 = transit(b, 222, 2);
        byte[] b2 = transit(b1, 333, 18);

        byte[] b1a = transit(b, 222, 18);
        byte[] b2a = transit(b1a, 333, 2);

        byte[] b1b = transit(b, 222, 333);
        byte[] b2b = transit(b1b, 18, 2);

        System.out.println(toString(b) + " " + getIdentifier(b));
        System.out.println(toString(b1) + " " + getIdentifier(b1));
        System.out.println(toString(b1a) + " " + getIdentifier(b1a));
        System.out.println(toString(b1b) + " " + getIdentifier(b1b));
        System.out.println("\n");

        System.out.println(toString(b2) + " " + getIdentifier(b2));
        System.out.println(toString(b2a) + " " + getIdentifier(b2a));
        System.out.println(toString(b2b) + " " + getIdentifier(b2b));

        /*
        System.out.println(toString(b2));
        byte[] b3 = transit(b2,6, 12);
        System.out.println(toString(b3));
        byte[] b4 = transit(b3,6, 124);
        System.out.println(toString(b4));

        List<byte[]> list = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        try {
            while (true) {
                list.add(transit(b1, 45,3));
                if (list.size() % 1_000_000 == 0) {
                    System.out.println("Created " + list.size() + " objects so far...");
                }
            }
        } catch (Exception e) {
            System.out.println("Out of memory after " + list.size() + " objects");
            System.out.println("Time: " + (System.currentTimeMillis() - currentTime));
        }
*/
    }


    public static byte[] create(int exp) {
        return create(exp, new int[]{});
    }

    public static byte[] create(int exp, int[] assumptions) {
        byte[] data = new byte[4 + assumptions.length * 2];
        for (int i = 0; i < assumptions.length; i++)
            appendAssumption(data, i, assumptions[i]);

        setExp(data, exp);
        return data;
    }

    // Only supports numbers between 0-65535
    private static void appendAssumption(byte[] data, int i, int assumption) {
        data[4 + i * 2] = (byte) ((assumption >> 8) & 0xFF);
        data[4 + i * 2 + 1] = (byte) (assumption & 0xFF);
        data[3] = (byte) (i + 1);
    }

    public static boolean isClosed(byte[] data) {
        return (data[0] & 0b10000000) != 0;
    }

    public static void setClosed(byte[] data) {
        data[0] = (byte) (data[0] | 0b10000000);
    }

    public static int getHeight(byte[] data) {
        return data[0] & 0b01111111;
    }

    // Only supports numbers between 0-127
    public static void setHeight(byte[] data, int height) {
        data[0] = (byte) ((data[0] & 0b10000000) | (height & 0b01111111));
    }

    // Only supports numbers between 0-65535
    public static void setExp(byte[] data, int exp) {
        data[1] = (byte) ((exp >> 8) & 0xFF);
        data[2] = (byte) (exp & 0xFF);

        if (containsAssumption(data, exp))
            setClosed(data);
    }

    public static int getExp(byte[] data) {
        return (((data[1] & 0xFF) << 8) | (data[2] & 0xFF));
    }

    public static int numberOfAssumptions(byte[] data) {
        return data[3];
    }

    public static boolean containsAssumption(byte[] data, int assumption) {
        for (int i = 0; i < numberOfAssumptions(data); i++)
            if (assumption == (byte) ((data[4 + i * 2] & 0b10000000) | (data[4 + i * 2 + 1] & 0b01111111)))
                return true;
        return false;
    }

    public static int[] getAssumptions(byte[] data) {
        int[] assumptions = new int[numberOfAssumptions(data)];

        for (int i = 0; i < assumptions.length; i++)
            assumptions[i] = (byte) ((data[4 + i * 2] & 0b10000000) | (data[4 + i * 2 + 1] & 0b01111111));

        return assumptions;
    }

    public static byte[] transit(byte[] data, int exp, Integer assumption) {
        byte[] goal;
        if (assumption == null || containsAssumption(data, assumption)) goal = data.clone();
        else {
            goal = Arrays.copyOf(data, data.length + 2);
            appendAssumption(goal, numberOfAssumptions(goal), assumption);
        }

        setHeight(goal, getHeight(goal) + 1);
        setExp(goal, exp);

        return goal;
    }

    //TODO The order of the elements in the set is relevant....
    public static Integer getIdentifier(byte[] data) {
        int e = (byte) ((data[1] & 0b10000000) | (data[2] & 0b01111111));

        int size = numberOfAssumptions(data);
        int count = 0;
        for (int i = 0; i < size; i++)
            count += (byte) ((data[4 + i * 2] & 0b10000000) | (data[4 + i * 2 + 1] & 0b01111111));

        return (e + "" + count).hashCode();
    }


    public static String toString(byte[] data) {
        return "[isClosed: " + isClosed(data) +
                //", height: " + getHeight(data) +
                ", exp: " + getExp(data) +
                ", numberOfAssumptions: " + numberOfAssumptions(data) +
                ", assumptions: " + Arrays.toString(getAssumptions(data)) +
                "] bytes: " + data.length;
    }

}
