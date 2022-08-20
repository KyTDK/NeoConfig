package com.neomechanical.neoconfig.java;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    public static <newType, oldType> List<newType> cast(List<oldType> list) {
        ArrayList<newType> newlyCastedArrayList = new ArrayList<>();
        for (oldType listObject : list) {
            //Check cast
            newlyCastedArrayList.add((newType) listObject);

        }
        return newlyCastedArrayList;
    }
}
