package com.simencassiman.homechef.db;

import androidx.room.TypeConverter;

import com.simencassiman.homechef.model.Rational;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.Unit;

import java.util.Date;

public class Converter {

    private static final String TAG = "Converter";
    //Recipe
    @TypeConverter
    public static Recipe.EditState toState(String state) {
        for(Recipe.EditState s: Recipe.EditState.values()){
            if(s.toString().equals(state)){
                return s;
            }
        }
        return null;
    }

    @TypeConverter
    public static String fromState(Recipe.EditState state){
        return state == null? null: state.toString();
    }

    //Amount
    @TypeConverter
    public static String fromValue(Number num){
        if(!(num instanceof Rational)){
            Float newNum = num.floatValue();
            return newNum.toString();
        }else
            return num.toString();
    }

    @TypeConverter
    public static Number toValue(String num){
        String[] strings = num.split(",");
        Number[] numbers = new Number[strings.length];
        int i = 0;
        for(String s: strings)
            numbers[i++] = NumberFactory.getNumber(s);

        return collapse(numbers);
    }

    public static Number collapse(Number[] numbers){
        Number num = null;
        for(Number n: numbers){
            if (num == null)
                num = n;
            else{
                if(n instanceof Rational)
                    num = ((Rational) n).add(num);
                else if(num instanceof Rational)
                    num = ((Rational) num).add(n);
                else
                    num = num.floatValue() +  n.floatValue();
            }
        }
        return num;
    }

    @TypeConverter
    public static String fromUnit(Unit unit){
        return unit.getSymbol();
    }

    @TypeConverter
    public static Unit toUnit(String string){
        for(Unit unit: Unit.values()){
            if(unit.getSymbol().equals(string)) return unit;
        }
        return Unit.UNITS;
    }

    // Shopping list
    @TypeConverter
    public static long fromDate(Date date){
        if (date == null)
            return 0;
        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(long millisec){
        return new Date(millisec);
    }

//    @TypeConverter
//    public static LiveData<Map<Integer,Boolean>> mapFromCursor(Cursor cursor){
//        Map<Integer,Boolean> map = new HashMap<>();
//        do {
//            boolean collected = true;
//            if(cursor.getInt(1) == 0)
//                collected = false;
//            map.put(cursor.getInt(0),collected);
//        } while (!cursor.isLast());
//        cursor.close();
//
//        return new MutableLiveData<>(map);
//    }
}
