package com.simencassiman.homechef.model;

import androidx.annotation.Nullable;
import androidx.room.Embedded;

import com.simencassiman.homechef.db.Converter;
import com.simencassiman.homechef.db.NumberFactory;

import org.jetbrains.annotations.NotNull;

public class Ingredient {

    public Ingredient(@NotNull String name, Amount amount) {
        this.name = name;
        this.amount = amount;
    }

    public static boolean isValidIngredient(String name, Amount amount){
        return !name.isEmpty();
    }

    public static boolean isValidIngredient(String name, String amount, Unit unit){
        return !name.isEmpty() && Amount.isValidAmount(NumberFactory.getNumber(amount), unit);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    private String name;

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    @Embedded
    @NotNull
    private Amount amount;

    public void add(Ingredient other){
        if(other != null)
            amount.add(other.getAmount());
    }

    @Override
    public String toString() {
        return getName() + " " + getAmount().toString();
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if(other instanceof Ingredient){
            boolean sameName = (getName() != null && ((Ingredient) other).getName() != null &&
                                getName().equals(((Ingredient) other).getName())) ||
                                (getName() == null && ((Ingredient) other).getName() == null);
            boolean sameAmount = (getAmount() != null && ((Ingredient) other).getAmount() != null &&
                                getAmount().equals(((Ingredient) other).getAmount())) ||
                                (getAmount() == null && ((Ingredient) other).getAmount() == null);

            return sameName && sameAmount;
        }else {
            return false;
        }
    }

    public static Ingredient.Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private String name;
        private Number value;
        private Unit unit;

        public Builder setName(String name){
            this.name = name;
            return this;
        }

        public Builder setValue(String value){
            return setValue(NumberFactory.getNumber(value));
        }

        public Builder setValue(Number num){
            this.value = num;
            return this;
        }

        public Builder setUnit(String string){
            return setUnit(Converter.toUnit(string));
        }

        public Builder setUnit(Unit unit){
            this.unit = unit;
            return this;
        }

        public Ingredient build(){
            Unit u = unit != null? unit:Unit.UNKNOWN;
            Amount amount = new Amount(value, u);
            return new Ingredient(name, amount);
        }
    }
}
