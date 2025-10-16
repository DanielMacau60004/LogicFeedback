package com.logic.feedback.nd.algorithm.tests;

import java.util.Arrays;

public class BinaryGoalGeneric {

    public static void main(String[] args) {
        BinaryMap map = new BinaryMap();
        byte[] b = create((short) 111, map);
        byte[] b1 = transit(b, (short) 222, (short) 2, map);
        byte[] b2 = transit(b1, (short) 333, (short) 18, map);

        byte[] b1a = transit(b, (short) 222, (short) 18, map);
        byte[] b2a = transit(b1a, (short) 333, (short) 2, map);

        byte[] b1b = transit(b, (short) 222, (short) 333, map);
        byte[] b2b = transit(b1b, (short) 18, (short) 2, map);

        System.out.println(toString(b, map) + " " + getIdentifier(b));
        System.out.println(toString(b1, map) + " " + getIdentifier(b1));
        System.out.println(toString(b1a, map) + " " + getIdentifier(b1a));
        System.out.println(toString(b1b, map) + " " + getIdentifier(b1b));
        System.out.println("\n");

        System.out.println(toString(b2, map) + " " + getIdentifier(b2));
        System.out.println(toString(b2a, map) + " " + getIdentifier(b2a));
        System.out.println(toString(b2b, map) + " " + getIdentifier(b2b));

        System.out.println(map.assumptions.size());
    }


    public static byte[] create(short exp, BinaryMap map) {
        byte[] data = new byte[3];
        setExp(data, exp, map);
        return data;
    }

    // Only supports numbers between 0-16777215
    private static void setAssumption(byte[] data, int index) {
        data[3] = (byte) ((index >> 24) & 0xFF);
        data[4] = (byte) ((index >> 16) & 0xFF);
        data[5] = (byte) ((index >> 8) & 0xFF);
        data[6] = (byte) (index & 0xFF);
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
    public static void setExp(byte[] data, short exp, BinaryMap map) {
        data[1] = (byte) ((exp >> 8) & 0xFF);
        data[2] = (byte) (exp & 0xFF);

        if (containsAssumption(data, exp, map))
            setClosed(data);
    }

    public static int getExp(byte[] data) {
        return (((data[1] & 0xFF) << 8) | (data[2] & 0xFF));
    }

    public static int numberOfAssumptions(byte[] data, BinaryMap map) {
        return getAssumptions(data, map).length();
    }

    public static boolean containsAssumption(byte[] data, short assumption, BinaryMap map) {
        return getAssumptions(data, map).contains(assumption);
    }

    private static Integer getAssumption(byte[] data) {
        if (data.length <= 3) return null;

        int value = 0;
        value |= (data[3] & 0xFF) << 24;
        value |= (data[4] & 0xFF) << 16;
        value |= (data[5] & 0xFF) << 8;
        value |= (data[6] & 0xFF);
        return value;
    }

    public static BinarySet getAssumptions(byte[] data, BinaryMap map) {
        Integer assumption = getAssumption(data);
        if (assumption == null) return new BinarySet();

        BinarySet assumptions = map.getAssumptions(assumption);
        if (assumptions != null) return assumptions;
        return new BinarySet();
    }

    public static byte[] transit(byte[] data, short exp, Short assumption, BinaryMap map) {
        byte[] goal;

        if (assumption == null || containsAssumption(data, assumption, map)) goal = data.clone();
        else {
            BinarySet assumptions;
            if (data.length > 3) { //Add assumption to an existing set
                goal = data.clone();
                assumptions = new BinarySet(getAssumptions(data, map));
            } else { //Add first assumption
                goal = Arrays.copyOf(data, data.length + 4);
                assumptions = new BinarySet();
            }

            assumptions.add(assumption);
            setAssumption(goal, map.storeAssumption(assumptions));
        }

        setHeight(goal, getHeight(goal) + 1);
        setExp(goal, exp, map);

        return goal;
    }

    //TODO tenho que usar a combinaçao do hash com o equals, nao ha outra hipotese :(
    public static Integer getIdentifier(byte[] data) {
        return (getExp(data) + "" + getAssumption(data)).hashCode();//Arrays.hashCode(Arrays.copyOfRange(data, 1, data.length));
    }

    public static String toString(byte[] data, BinaryMap map) {
        return "[isClosed: " + isClosed(data) +
                ", height: " + getHeight(data) +
                ", exp: " + getExp(data) +
                ", numberOfAssumptions: " + numberOfAssumptions(data, map) +
                ", assumptions: " + getAssumptions(data, map) +
                "] bytes: " + data.length;
    }

}
