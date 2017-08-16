package com.cloutteam.jarcraftinator;

import com.cloutteam.jarcraftinator.world.BlockState;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        BlockState dummyState = new BlockState();

        List<Long> longList = new ArrayList<>();
        StringBuilder currentLong = new StringBuilder();
        for (int y = 0; y < 16; y++)
            for (int z = 0; z < 16; z++)
                for (int x = 0; x < 16; x++) {
                    for (int i = 0; i < 4; i++) {
                        currentLong.append(getBit(dummyState.getMetadata(), i));
                        if (currentLong.length() == 64) {
                            System.out.println(currentLong.toString());
                            longList.add(parseLong(currentLong.toString()));
                            currentLong = new StringBuilder();
                        }
                    }
                    for (int i = 0; i < 9; i++) {
                        currentLong.append(getBit(dummyState.getId(), i));
                        if (currentLong.length() == 64) {
                            System.out.println(currentLong.toString());
                            longList.add(parseLong(currentLong.toString()));
                            currentLong = new StringBuilder();
                        }
                    }
                }
        System.out.println(longList);
        System.out.println(longList.size());
    }

    private static long parseLong(String s) {
        return new BigInteger(s, 2).longValue();
    }

    private static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

}
