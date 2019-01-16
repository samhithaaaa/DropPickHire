package com.avinash.droppickhire.helper;

import java.util.Date;

public class Helper {

    public static String uniqueIdGenerator() {
        String uniqueID = "";
        Date timeStamp = new Date();

        //6 digit unique id generated using 4 digits random number, 2 digits of second
        //ensures uniqueness in every request
        uniqueID = uniqueID + (int) Math.floor(Math.random()*9000) + 1000;
        uniqueID = uniqueID+timeStamp.getSeconds();

        return uniqueID;
    }
}
