package com.logic.feedback.nd.algorithm.tests;

import java.util.Arrays;

public class BinarySet {

    private short[] data;

    public BinarySet() {
        data = new short[0];
    }

    public BinarySet(BinarySet bitArray) {
        data = new short[bitArray.length()];
        for (int i = 0; i < bitArray.length(); i++)
            data[i] = bitArray.data[i];
    }

    public boolean contains(short element) {
        for (short elem : data) {
            if (elem == element) return true;
            else if (elem > element) return false;
        }

        return false;
    }

    public int length() {
        return data.length;
    }

    public void add(short newElement) {
        if (contains(newElement))
            return;

        short[] newData = new short[data.length + 1];

        int i = 0;
        while (i < data.length && data[i] < newElement) {
            newData[i] = data[i];
            i++;
        }

        newData[i] = newElement;
        for (int j = i; j < data.length; j++)
            newData[j + 1] = data[j];

        this.data = newData;
    }

    public void remove(short element) {
        if (!contains(element))
            return;

        short[] newData = new short[data.length - 1];
        int i = 0, j = 0;

        while (i < data.length) {
            if (data[i] != element)
                newData[j++] = data[i];
            i++;
        }

        this.data = newData;
    }


    public short[] getData() {
        return data;
    }

    protected BinarySet clone() {
        return new BinarySet(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinarySet bitArray = (BinarySet) o;

        if (bitArray.length() != data.length) return false;
        for (int i = 0; i < bitArray.length(); i++)
            if (data[i] != bitArray.data[i]) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    public String toString(BinaryMap map) {
        StringBuilder str = new StringBuilder("[");
        for (int i = 0; i < data.length; i++) {
            str.append(map.getFormulas(data[i]));
            if (i < data.length - 1) {
                str.append(", ");
            }
        }
        return str.append("]").toString();
    }

}
