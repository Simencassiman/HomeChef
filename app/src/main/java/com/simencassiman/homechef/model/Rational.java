package com.simencassiman.homechef.model;

import com.simencassiman.homechef.db.NumberFactory;

public class Rational extends Number {

    public Rational(int numerator, int denominator) throws IllegalArgumentException{
        this(0, numerator, denominator);
    }

    public Rational(int whole, int numerator, int denominator) throws IllegalArgumentException{
        if(!legalArguments(whole, numerator, denominator)){
            throw new IllegalArgumentException("All integers must be positive and the denominator different from zero.");
        }else {
            this.wholeFraction = whole;
            this.numerator = numerator;
            this.denominator = denominator;
        }
    }

    public boolean legalArguments(int whole, int numerator, int denominator) {
        return whole >=  0 && numerator >= 0 && denominator > 0;
    }

    public int getWholeFraction() {
        return wholeFraction;
    }

    private final int wholeFraction;

    public int getNumerator() {
        return numerator;
    }

    private final int numerator;

    public int getDenominator() {
        return denominator;
    }

    private final int denominator;

    @Override
    public int intValue() {
        return getWholeFraction();
    }

    @Override
    public long longValue() {
        return (long) getWholeFraction() + ((long) getNumerator()) / ((long) getDenominator());
    }

    @Override
    public float floatValue() {
        return (float) getWholeFraction() + ((float) getNumerator()) / ((float) getDenominator());
    }

    @Override
    public double doubleValue() {
        return (double) getWholeFraction() + ((double) getNumerator()) / ((double) getDenominator());
    }

    @Override
    public String toString() {
        return getWholeFraction() + " " + getNumerator() + "/" + getDenominator();
    }

    public Rational addRational(Rational other){
        int newDenominator = lcm(getDenominator(), other.getDenominator());
        int newNumerator = getNumerator() * newDenominator/getDenominator() +
                            other.getNumerator() * newDenominator/other.getDenominator();
        return new Rational(getWholeFraction() + other.getWholeFraction(),
                        newNumerator, newDenominator);
    }

    // MATH

    public Rational normalize(){
        int newWhole = 0;
        int newNumerator = numerator;
        int newDenominator = denominator;
        if(numerator >= denominator){
            newWhole = numerator/denominator;
            newNumerator -= newWhole*denominator;
        }
        newWhole += wholeFraction;
        int gcd = Rational.gcd(newNumerator, newDenominator);
        newNumerator /= gcd;
        newDenominator /= gcd;

        return new Rational(newWhole, newNumerator, newDenominator);
    }

    public static int lcm(int a, int b){
        int gcd = gcd(a, b);
        return Math.abs(a * b / gcd);
    }

    public static int gcd(int a, int b) {
        if (b == 0) return a;
        else if (a == 0) return b;
        return gcd(b,a%b);
    }

    public Rational add(Number num){
        if(num instanceof Rational)
            return add(num);
        else if(num instanceof Integer)
            return add((int)num);
        else
            return add((double)num);
    }

    public Rational add(int num){
        return new Rational(getWholeFraction() + num, getNumerator(), getDenominator());
    }

    public Rational add(double num){
        return add(NumberFactory.getRational(num));
    }

    public Rational add(Rational other){
        int newWhole = this.wholeFraction+other.wholeFraction;
        int newNumerator = this.numerator*other.denominator + other.numerator*this.denominator;
        int newDenominator = this.denominator*other.denominator;

        return (new Rational(newWhole, newNumerator, newDenominator)).normalize();
    }

    public Rational multiply(int factor){
        return new Rational(getWholeFraction(), getNumerator()*factor, getDenominator());
    }

    public Rational multiply(double factor){
        return multiply(NumberFactory.getRational(factor));
    }

    public Rational multiply(Rational other){
        int newWhole = this.wholeFraction*other.wholeFraction;
        int newNumerator =  this.wholeFraction*other.numerator*this.denominator +
                            other.wholeFraction*this.numerator*other.denominator+
                            this.numerator*other.numerator;
        int newDenominator = this.denominator*other.denominator;

        return (new Rational(newWhole, newNumerator, newDenominator)).normalize();
    }

}
