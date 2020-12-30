package com.simencassiman.homechef.model;

import org.jetbrains.annotations.NotNull;

public class Amount {

    private static final String TAG = "Amount";

    public Amount(Number value, @NotNull Unit unit) throws IllegalArgumentException{
        if (!isValidAmount(value, unit))
            throw new IllegalArgumentException("Cannot make a valid Amount with input");

        this.unit = unit;
        this.value = value;

        refactor();
    }

    public static boolean isValidAmount(Number value, Unit unit){
        return (value instanceof Integer || value instanceof Float || value instanceof Long ||
                value instanceof Double || value instanceof Rational)
                && unit != null;
    }

    public Number getValue() {
        return value;
    }
    
    private Number value;

    @NotNull
    public Unit getUnit() {
        return unit;
    }

    @NotNull
    private Unit unit;

    public void add(Amount other){
        if (other == null || !unit.compatible(other.getUnit()))
            return;

        // Reduce to common unit, compatibility is already checked
        this.normalize();
        other.normalize();

        if(value instanceof Rational)
            value = ((Rational) value).add(other.getValue());
        else if (other.getValue() instanceof Rational)
            value = ((Rational) other.getValue()).add(this.value);
        else {
            value = value.doubleValue() + other.getValue().doubleValue();
        }

        // Revert to nicer format for display
        refactor();
    }

    public void normalize(){
        if(value instanceof Rational)
            value = value.doubleValue();

        value = value.floatValue()*unit.getNormalizationFactor();
        unit = unit.normalize();
    }

    public void refactor(){
        if(value == null)
            return;

        if(value instanceof Rational){
            ((Rational) value).normalize();
        }else {
            Unit newUnit = this.unit;
            double newValue = value.doubleValue();
            if(value.doubleValue() < 1){
                for(Unit unit: this.unit.getSubmultiples()){
                    newValue = value.doubleValue() / this.unit.getConversionFactor(unit);
                    if(newValue > 1) {
                        newUnit = unit;
                        break;
                    }
                }
            }else{
                for (Unit unit : this.unit.getMultiples()) {
                    if (value.doubleValue() >= unit.getNormalizationFactor() && unit.getNormalizationFactor() > newUnit.getNormalizationFactor())
                        newUnit = unit;
                }
            }
            if(unit != newUnit) {
                value = value.doubleValue() / this.unit.getConversionFactor(newUnit);
                unit = newUnit;
            }

            if (value.doubleValue() % 1 == 0)
                value =  value.intValue();
        }
    }

    @Override
    @NotNull
    public String toString() {
        return value.toString() + " " + getUnit().getSymbol();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Amount &&
                getValue().doubleValue() * getUnit().getNormalizationFactor() ==
                        ((Amount) obj).getValue().doubleValue() * ((Amount) obj).getUnit().getNormalizationFactor() &&
                getUnit().compatible(((Amount) obj).getUnit());
    }
}
