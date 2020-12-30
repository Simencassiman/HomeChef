package com.simencassiman.homechef.util;

public class StringUtil {

    public static String capitalize(String s) {
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static String capitalizeWords(String s){
        String[] s_array = s.split(" ");
        String result = "";
        boolean initial = true;
        for(String st: s_array){
            if(!initial){
                result += " ";
            }else {
                initial = false;
            }
            result += StringUtil.capitalize(st);
        }

        return result;
    }
}
