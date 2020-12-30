package com.simencassiman.homechef.model;

import java.util.ArrayList;
import java.util.List;

public enum Unit {

    KG {
        public String getSymbol(){ return "kg";}

        public Unit normalize(){ return G;}

        public double getNormalizationFactor(){ return 1000;}

        public boolean isIS(){ return true;}
    },
    G {
        public String getSymbol(){ return "g";}

        public Unit normalize(){ return G;}

        public double getNormalizationFactor(){ return 1;}

        public boolean isIS(){ return true;}
    },
    L {
        public String getSymbol(){ return "l";}

        public Unit normalize(){ return ML;}

        public double getNormalizationFactor(){ return 1000;}

        public boolean isIS(){ return true;}
    },
    DL {
        public String getSymbol(){ return "dl";}

        public Unit normalize(){ return ML;}

        public double getNormalizationFactor(){ return 100;}

        public boolean isIS(){ return true;}
    },
    CL {
        public String getSymbol(){ return "cl";}

        public Unit normalize(){ return ML;}

        public double getNormalizationFactor(){ return 10;}

        public boolean isIS() { return true;}
    },
    ML {
        public String getSymbol(){ return "ml";}

        public Unit normalize(){ return ML;}

        public double getNormalizationFactor(){ return 1;}

        @Override
        public boolean isIS() { return true;}
    },
    CUP {
        public String getSymbol(){ return "cup(s)";}

        public Unit normalize(){ return ML;}

        public double getNormalizationFactor(){ return 236.59;}

        public boolean isIS() { return false;}
    },
    POUND {
        @Override
        public String getSymbol() { return "lb";}

        @Override
        public Unit normalize() { return G;}

        @Override
        public double getNormalizationFactor() { return 453.592;}

        @Override
        public boolean isIS() { return false;}
    },
    OUNCE {
        @Override
        public String getSymbol(){ return "oz";}

        @Override
        public Unit normalize(){ return G;}

        @Override
        public double getNormalizationFactor(){ return 28.35;}

        @Override
        public boolean isIS() { return false; }
    },
    UNITS {
        @Override
        public String getSymbol() { return "unit(s)";}

        @Override
        public Unit normalize() { return UNITS;}

        @Override
        public double getNormalizationFactor() { return 1;}

        @Override
        public boolean isIS() { return false;}
    },
    UNKNOWN {
        @Override
        public String getSymbol() { return "";}

        @Override
        public Unit normalize() { return UNKNOWN;}

        @Override
        public double getNormalizationFactor() { return 1;}

        @Override
        public boolean isIS() { return false;}
    };

    public abstract String getSymbol();

    public abstract Unit normalize();

    public abstract double getNormalizationFactor();

    public boolean equals(Unit unit){
        return this.getSymbol().equals(unit.getSymbol());
    }

    public boolean compatible(Unit unit){
        return this.normalize().equals(unit.normalize());
    }

    public abstract boolean isIS();

    public List<Unit> getCompatibleUnits(){
        List<Unit> compatibleUnits = new ArrayList<>();
        for(Unit other: Unit.values()){
            if(this.compatible(other))
                compatibleUnits.add(other);
        }
        return compatibleUnits;
    }

    public List<Unit> getMultiples(){
        List<Unit> multiples = new ArrayList<>();
        for(Unit other: Unit.values()){
            if((other.isIS() == this.isIS()) && this.compatible(other) && this.getNormalizationFactor() < other.getNormalizationFactor())
                multiples.add(other);
        }
        return multiples;
    }

    public List<Unit> getSubmultiples(){
        List<Unit> submultiples = new ArrayList<>();
        for(Unit other: Unit.values()){
            if((other.isIS() == this.isIS()) && this.compatible(other) && this.getNormalizationFactor() > other.getNormalizationFactor())
                submultiples.add(other);
        }
        return submultiples;
    }

    /**
     * Return the conversion factor from this (unit) to "to" (provided unit).
     *
     * @param to unit to convert to
     * @return double representing conversion factor
     */
    public double getConversionFactor(Unit to) throws IllegalArgumentException{
        if(!this.compatible(to))
            throw new IllegalArgumentException("These units are not compatible. " +
                    "Cannot convert between them.");
        return this.getNormalizationFactor()/to.getNormalizationFactor();
    }
}

enum System{
    International,
    Imperial
}
