package com.logic.feedback.nd.algorithm.tests;

import com.logic.api.IFormula;
import com.logic.api.LogicAPI;
import com.logic.feedback.nd.algorithm.proofs.BitArray;
import com.logic.feedback.nd.algorithm.proofs.BitGraphHandler;
import com.logic.feedback.nd.algorithm.proofs.GoalNode;

import java.util.*;

public class BinaryGoal {

    public static void main(String[] args) {
        BinaryGoal b = new BinaryGoal(111);
        System.out.println(b);
        BinaryGoal b1 = b.transit(222, 2);
        System.out.println(b1);
        BinaryGoal b2 = b1.transit(333, 18);
        System.out.println(b2);
        BinaryGoal b3 = b2.transit(6, 12);
        System.out.println(b3);

        List<BinaryGoal> list = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        try {
            while (true) {
                list.add(b1.transit(2, null));

                if (list.size() % 1_000_000 == 0) {
                    System.out.println("Created " + list.size() + " objects so far...");
                }
            }
        } catch (OutOfMemoryError e) {
            System.out.println("Out of memory after " + list.size() + " objects");
            System.out.println("Time: " + (System.currentTimeMillis() - currentTime));
        }

    }

    private final byte[] data;

    BinaryGoal(byte[] data) {
        this.data = data;
    }

    public BinaryGoal(int exp) {
        this(exp, new int[]{});
    }

    public BinaryGoal(int exp, int[] assumptions) {
        this.data = new byte[4 + assumptions.length * 2];
        for (int i = 0; i < assumptions.length; i++)
            appendAssumption(i, assumptions[i]);

        setExp(exp);
    }

    // Only supports numbers between 0-65535
    private void appendAssumption(int i, int assumption) {
        data[4 + i * 2] = (byte) ((assumption >> 8) & 0xFF);
        data[4 + i * 2 + 1] = (byte) (assumption & 0xFF);
        this.data[3] = (byte) (i + 1);
    }

    public boolean isClosed() {
        return (data[0] & 0b10000000) != 0;
    }

    public void setClosed() {
        data[0] = (byte) (data[0] | 0b10000000);
    }

    public int getHeight() {
        return data[0] & 0b01111111;
    }

    // Only supports numbers between 0-127
    public void setHeight(int height) {
        data[0] = (byte) ((data[0] & 0b10000000) | (height & 0b01111111));
    }

    // Only supports numbers between 0-65535
    public void setExp(int exp) {
        this.data[1] = (byte) ((exp >> 8) & 0xFF);
        this.data[2] = (byte) (exp & 0xFF);

        if(containsAssumption(exp))
            setClosed();
    }

    public int getExp() {
        return (((data[1] & 0xFF) << 8) | (data[2] & 0xFF));
    }

    public int numberOfAssumptions() {
        return data[3];
    }

    public boolean containsAssumption(int assumption) {
        for (int i = 0; i < numberOfAssumptions(); i++)
            if (assumption == (byte) ((data[4 + i * 2] & 0b10000000) | (data[4 + i * 2 + 1] & 0b01111111)))
                return true;
        return false;
    }

    public int[] getAssumptions() {
        int[] assumptions = new int[numberOfAssumptions()];

        for (int i = 0; i < assumptions.length; i++)
            assumptions[i] = (byte) ((data[4 + i * 2] & 0b10000000) | (data[4 + i * 2 + 1] & 0b01111111));

        return assumptions;
    }

    public BinaryGoal transit(int exp, Integer assumption) {
        BinaryGoal goal;
        if (assumption == null || containsAssumption(assumption)) goal = new BinaryGoal(data.clone());
        else {
            goal = new BinaryGoal(Arrays.copyOf(data, data.length + 2));
            goal.appendAssumption(numberOfAssumptions(), assumption);
        }

        goal.setHeight(goal.getHeight() + 1);
        goal.setExp(exp);

        return goal;
    }

    @Override
    public String toString() {
        return "[isClosed: " + isClosed() +
                ", height: " + getHeight() +
                ", exp: " + getExp() +
                ", numberOfAssumptions: " + numberOfAssumptions() +
                ", assumptions: " + Arrays.toString(getAssumptions()) +
                "] bytes: " + data.length;
    }

}
