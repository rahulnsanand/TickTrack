package com.theflopguyproductions.ticktrack.utils;

import java.util.Random;

public class UniqueIdGenerator {


    private static String currentTime;
    private static String moduleName;
    private static String randomValue;
    private static Random random = new Random();

    public static String getUniqueCounterID(){
        currentTime = String.valueOf(System.currentTimeMillis());
        moduleName = "TickTrackCounterID";
        randomValue = String.valueOf(random.nextInt(9999));

        return moduleName+"_"+currentTime+"_"+randomValue;
    }

    public static String getUniqueTimerID(){
        currentTime = String.valueOf(System.currentTimeMillis());
        moduleName = "TickTrackTimerID";
        randomValue = String.valueOf(random.nextInt(9999));

        return moduleName+"_"+currentTime+"_"+randomValue;
    }

    public static Integer getUniqueIntegerTimerID(){

        return random.nextInt(9999);
    }

}
