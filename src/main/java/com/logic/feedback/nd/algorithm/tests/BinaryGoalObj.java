package com.logic.feedback.nd.algorithm.tests;

import java.util.Arrays;

public class BinaryGoalObj {

    private final byte[] data;

    BinaryGoalObj(byte[] data) {
        this.data = data;
    }

    public BinaryGoalObj(short exp, BinaryMap map) {
        this(new byte[3]);
        setExp(exp, map);
    }

    // Only supports numbers between 0-4095
    private void setAssumption(int index) {
        data[3] = (byte) ((index >> 24) & 0xFF);
        data[4] = (byte) ((index >> 16) & 0xFF);
        data[5] = (byte) ((index >> 8) & 0xFF);
        data[6] = (byte) (index & 0xFF);
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

    // Only supports numbers between 0-4095
    public void setExp(short exp, BinaryMap map) {
        data[1] = (byte) ((exp >> 8) & 0xFF);
        data[2] = (byte) (exp & 0xFF);

        if (containsAssumption(exp, map))
            setClosed();
    }

    public short getExp() {
        return (short) (((data[1] & 0xFF) << 8) | (data[2] & 0xFF));
    }

    public int numberOfAssumptions(BinaryMap map) {
        return getAssumptions(map).length();
    }

    public boolean containsAssumption(short assumption, BinaryMap map) {
        return getAssumptions(map).contains(assumption);
    }

    public int getAssumption() {
        if (data.length <= 3) return 0;

        int value = 0;
        value |= (data[3] & 0xFF) << 24;
        value |= (data[4] & 0xFF) << 16;
        value |= (data[5] & 0xFF) << 8;
        value |= (data[6] & 0xFF);
        return value;
    }

    public BinarySet getAssumptions(BinaryMap map) {
        return map.getAssumptions(getAssumption());
    }

    public BinaryGoalObj transit(short exp, Short assumption, BinaryMap map) {
        BinaryGoalObj obj;
        BinarySet assumptions;

        if (assumption == null || containsAssumption(assumption, map)) obj = new BinaryGoalObj(data.clone());
        else {
            if (data.length > 3) { //Add assumption to an existing set
                obj = new BinaryGoalObj(data.clone());
                assumptions = new BinarySet(getAssumptions(map));
            } else { //Add first assumption
                obj = new BinaryGoalObj(Arrays.copyOf(data, data.length + 4));
                assumptions = new BinarySet();
            }

            assumptions.add(assumption);
            obj.setAssumption(map.storeAssumption(assumptions));
        }

        obj.setHeight(obj.getHeight() + 1);
        obj.setExp(exp, map);

        return obj;
    }

    public BinaryGoalObj invert(short exp, Short assumption, BinaryMap map) {
        BinaryGoalObj obj = new BinaryGoalObj(data.clone());

        if(assumption != null) {
            BinarySet assumptions = getAssumptions(map).clone();
            assumptions.remove(assumption);
            obj.setAssumption(map.storeAssumption(assumptions));
        }

        obj.setExp(exp, map);
        return obj;
    }

    public int getIdentifier() {
        return ((getExp() & 0xFFF) << 20) | (getAssumption() & 0xFFFFF);
    }

    @Override
    public int hashCode() {
        return getIdentifier();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BinaryGoalObj other)
            return getExp() == other.getExp() && getAssumption() == other.getAssumption(); //getIdentifier() == other.getIdentifier();
        return false;
    }

    public String toString(BinaryMap map) {
        return "[id: " + getIdentifier() +
                ", isClosed: " + isClosed() +
                ", height: " + getHeight() +
                ", exp: " + map.getFormulas(getExp()) +
                ", numberOfAssumptions: " + numberOfAssumptions(map) +
                ", assumptions: " + getAssumptions(map).toString(map) +
                "]";
    }

}
