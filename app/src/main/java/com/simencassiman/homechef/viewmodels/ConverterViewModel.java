package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.model.Rational;
import com.simencassiman.homechef.model.Unit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static java.lang.Math.round;

public class ConverterViewModel extends ViewModel {

    private static final String TAG = "ConverterViewModel";
    private static final int PRECISION = 100;

    @Inject
    public ConverterViewModel(){
        allUnits = new ArrayList<>();
        for(Unit u: Unit.values()) allUnits.add(u.getSymbol());
    }

    public Unit getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(Unit fromUnit) {
        this.fromUnit = fromUnit;
    }
    private Unit fromUnit;

    public Unit getToUnit() {
        return toUnit;
    }

    public void setToUnit(Unit toUnit) {
        this.toUnit = toUnit;
    }
    private Unit toUnit;

    public Number getFromValue() {
        return fromValue;
    }

    public void setFromValue(Number fromValue) {
        this.fromValue = fromValue;
    }
    private Number fromValue;

    public Number getToValue() {
        return toValue;
    }

    public void setToValue(Number toValue) {
        this.toValue = toValue;
    }
    private Number toValue;

    public List<String> getAllUnits(){
        return new ArrayList<>(allUnits);
    }
    private List<String> allUnits;

    public void convert(){
        if(getFromValue() == null)
            return;
        if(getFromValue() instanceof Rational){
            setToValue(((Rational) getFromValue()).multiply(getConversionFactor()));
        }else{
            setToValue(round(getFromValue().doubleValue()*getConversionFactor()*PRECISION)/PRECISION);
            if(getToValue().doubleValue()%1 == 0)
                setToValue(getToValue().intValue());
        }
    }

    public double getConversionFactor(){
        return getFromUnit().getConversionFactor(getToUnit());
    }

    public boolean isSwapping(){
        return isSwapping;
    }

    public void setSwapping(boolean state){
        this.isSwapping = state;
    }
    private boolean isSwapping;
}
