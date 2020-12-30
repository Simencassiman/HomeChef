package com.simencassiman.homechef.db;

import com.simencassiman.homechef.model.Rational;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberFactory {
    public static final int RATIONAL_PRECISION = 100;

    public static Number getNumber(String num){
        if (num == null){
            return null;
        }else if(num.contains("/")){
            int whole = 0;
            if(num.contains(" ")){
                String[] splitFraction = num.split(" ");
                whole = (int) Integer.parseInt(splitFraction[0]);
                num = splitFraction[1];
            }
            String[] values = num.split("/");
            int numerator = (int) Integer.parseInt(values[0]);
            int denominator = (int) Integer.parseInt(values[1]);
            return new Rational(whole, numerator, denominator);
        }else{
            try {
                return NumberFormat.getInstance().parse(num);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static Rational getRational(double num){
        if(num%1 == 0){
            return new Rational((int) num, 0, 1);
        }

        int numerator = (int) Math.round(num*RATIONAL_PRECISION);
        int denominator = RATIONAL_PRECISION;
        int wholePart = 0;
        if (numerator == denominator){
            return new Rational(1, 0,1);
        }else if(numerator > denominator){
            wholePart = numerator/denominator;
            numerator -= denominator*wholePart;
        }
        int gcd = Rational.gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;

        return new Rational(wholePart,numerator,denominator);
    }
}
