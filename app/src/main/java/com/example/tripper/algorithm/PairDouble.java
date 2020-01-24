package com.example.tripper.algorithm;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class PairDouble {
    double first;
    int second;

    PairDouble(double first, int second) {
        this.first = first;
        this.second = second;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int hashCode() {
        return 31 * Double.hashCode(first) + second;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof PairDouble) {
            PairDouble otherPair = (PairDouble) other;
            return this.first == otherPair.first && this.second == otherPair.second;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
