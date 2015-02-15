package com.upenn.utils;

/**
 * Created by chengjia on 2/14/15.
 */
public class MiscCalc {
    public MiscCalc(){
    }

    public static boolean all_true(boolean[] array)
    {
        for(boolean b : array) if(!b) return false;
        return true;
    }

    public static boolean all_false(boolean[] array)
    {
        for(boolean b : array) if(b) return false;
        return true;
    }
}
