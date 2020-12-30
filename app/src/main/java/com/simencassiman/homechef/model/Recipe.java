package com.simencassiman.homechef.model;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.simencassiman.homechef.db.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity(tableName = "recipe")
public class Recipe {

    private static final String TAG = "Recipe";

    public enum EditState{
        PUBLISHED,
        DRAFT,
        EDITED
    }

    @Ignore
    public Recipe(int id, String title){
        this(id, title, null, null, null,
                null, 1);
    }

    public Recipe(int id, String title, String imageURI, String author, String description,
                  EditState state, int peopleCount){
        this(id, title, imageURI, author, description,
                state, peopleCount, null);
    }

    @Ignore
    public Recipe(int id, String title, String imageURI, String author, String description,
                  EditState state, int peopleCount, ArrayList<Ingredient> ingredients){
        this(id, title, imageURI, 0, false, false, author, description,
                state, peopleCount, null, null, ingredients, null);
    }

    @Ignore
    public Recipe(int id, String title, String imageURI, /*Bitmap image,*/ int time, boolean vegetarian,
                  boolean vegan, String author, String description, EditState state, int peopleCount,
                  String utilities, String[] tags, ArrayList<Ingredient> ingredients, ArrayList<String> instructions) {

        setId(id);
        setTitle(title);
        setImageURI(imageURI);
//        this.image = image;
        setAuthor(author);
        setDescription(description);
        setIngredients(ingredients);
        setState(state);
        setPeopleCount(peopleCount);
        setTags(tags);
        setVegetarian(vegetarian);
        setVegan(vegan);
        setInstructions(instructions);
    }

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    @PrimaryKey
    private int id;

    public String getTitle() {
        return title == null? "No title":title;
    }

    public void setTitle(String title) { this.title = title;}

    private String title;

    public String getImageURI() { return imageURI;}

    public void setImageURI(String imageURI) { this.imageURI = imageURI;}

    private String imageURI;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    private int time;

    public boolean isVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
    }

    private boolean vegetarian;

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    private boolean vegan;

    /*public Bitmap getImage() { return image;}

    public void setImage(Bitmap image) { this.image = image;}

    private Bitmap image;*/

    public String getAuthor() { return author;}

    public void setAuthor(String author) { this.author = author;}

    private String author;

    public String getDescription() {
       return description == null? "No description":description;
    }

    public void setDescription(String description) { this.description = description;}

    private String description;

    public EditState getState() { return state;}

    public void setState(EditState state) { this.state = state;}

    @TypeConverters({Converter.class})
    private EditState state;

    public int getPeopleCount() { return peopleCount;}

    public void setPeopleCount(int peopleCount) {
        if(isValidPeopleCount(peopleCount)) this.peopleCount = peopleCount;
    }

    public void updatePeopleCount(int newPeopleCount) throws IllegalArgumentException {
        if(!isValidPeopleCount(newPeopleCount))
            throw new IllegalArgumentException("This people count is out of range");

        for(Ingredient ingredient: ingredients){
            Number oldValue = ingredient.getAmount().getValue();
            Number newValue;
            if(oldValue instanceof Rational){
                newValue = new Rational(((Rational) oldValue).getNumerator() * newPeopleCount,
                        ((Rational) oldValue).getDenominator() * peopleCount);
            }else{
                newValue = (double) Math.round(
                        (oldValue.doubleValue() * newPeopleCount / peopleCount)*100) /100;
            }

            ingredient.setAmount(new Amount(newValue, ingredient.getAmount().getUnit()));
        }

        setPeopleCount(newPeopleCount);
    }

    public static boolean isValidPeopleCount(int peopleCount){
        if(peopleCount > MAX_PEOPLE_COUNT || peopleCount < MIN_PEOPLE_COUNT) return false;
        else return true;
    }

    private int peopleCount;

    public static final int MAX_PEOPLE_COUNT = 20;

    public static final int MIN_PEOPLE_COUNT = 1;

    public String getUtilities() {
        return utilities;
    }

    public void setUtilities(String utilities) {
        this.utilities = utilities;
    }

    private String utilities;

    public ArrayList<Ingredient> getIngredients() { return new ArrayList<>(ingredients);}

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        if (ingredients != null) this.ingredients = new ArrayList<>(ingredients);
        else this.ingredients = new ArrayList<>();
    }

    @Ignore
    private ArrayList<Ingredient> ingredients = new ArrayList<>();

    public String[] getTags() {
        return tags == null? null:this.tags.clone();
    }

    public void setTags(String[] tags){
        this.tags = tags;
    }

    public void setTags(ArrayList<String> tags){
        if (tags != null){
            String[] newTags = new String[tags.size()];
            int i = 0;
            for(String tag: tags) newTags[i++] = tag;

            setTags(newTags);
        }else{
            setTags((String[]) null);
        }
    }

    public String getTagsInString() {
        if (tags == null) return "";
        else{
            String out = "";
            boolean initial = true;
            for(String s: tags){
                if (initial) initial = false;
                else out += " ";
                out += s;
            }
            return out;
        }
    }

    @Ignore
    private String[] tags;

    public List<String> getInstructions() {
        if(instructions == null)
            return new ArrayList<>();
        else
            return new ArrayList<>(instructions);
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public int getNumberOfInstructions(){
        return this.instructions.size();
    }
    @Ignore
    private List<String> instructions;

    public boolean isFavorite(){ return this.isFavorite;}

    public void setIsFavorite(boolean fav){ this.isFavorite = fav;}

    @Ignore
    private boolean isFavorite;

    private boolean sameId(Recipe other){
        return getId() == other.getId();
    }

    private boolean sameTitle(Recipe other){
        return (getTitle() != null && other.getTitle() != null &&
                getTitle().equals(other.getTitle())) ||
                (getTitle() == null && other.getTitle() == null);
    }

    private boolean sameDescription(Recipe other){
        return (getDescription() != null &&
                other.getDescription() != null &&
                getDescription().equals(other.getDescription())) ||
                (getDescription() == null && other.getDescription() == null);
    }

    private boolean sameImageURI(Recipe other){
        return (getImageURI() != null && other.getImageURI() != null &&
                getImageURI().equals(other.getImageURI())) ||
                (getImageURI() == null && other.getImageURI() == null);
    }

    private boolean samePublisher(Recipe other){
        return (getAuthor() != null && other.getAuthor() != null &&
                getAuthor().equals(other.getAuthor())) ||
                (getAuthor() == null && other.getAuthor() == null);
    }

    private boolean sameState(Recipe other){
        return (getState() != null && other.getState() != null &&
                getState().equals(other.getState())) ||
                (getState() == null && other.getState() == null);
    }

    private boolean samePeopleCount(Recipe other){
        return getPeopleCount() == other.getPeopleCount();
    }

    private boolean sameVeggieState(Recipe other){
        return isVegetarian()^other.isVegetarian();
    }

    private boolean sameVeganState(Recipe other){
        return isVegan()^other.isVegan();
    }

    private boolean sameTags(Recipe other){
        return (getTags() != null && other.getTags() != null &&
                Arrays.equals(getTags(), other.getTags())) ||
                (getTags() == null && other.getTags() == null);
    }

    private boolean sameIngredients(Recipe other){
        return (getIngredients() != null && other.getIngredients() != null) &&
                getIngredients().equals(other.getIngredients()) ||
                (getIngredients() == null && other.getIngredients() == null);
    }

    private boolean sameInstructions(Recipe other){
        Log.d(TAG, "sameInstructions: this instructions length is " + getInstructions().size());
        Log.d(TAG, "sameInstructions: other instructions length is " + other.getInstructions().size());
        Log.d(TAG, "sameInstructions: " + getInstructions().equals(other.getInstructions()));
        return getInstructions().equals(other.getInstructions());
    }

    @Override
    public boolean equals(@Nullable Object other) {

        if (other instanceof Recipe){
            return  sameId((Recipe) other) &&
                    sameTitle((Recipe) other) &&
                    sameDescription((Recipe) other) &&
                    sameImageURI((Recipe) other) &&
                    samePublisher((Recipe) other) &&
                    sameState((Recipe) other) &&
                    samePeopleCount((Recipe) other) &&
                    sameVeggieState((Recipe) other) &&
                    sameVeganState((Recipe) other) &&
                    sameTags((Recipe) other) &&
                    sameIngredients((Recipe) other) &&
                    sameInstructions((Recipe) other);
        }else
            return false;
    }

    public static Builder builder() { return new Builder();}

    public static class Builder{

        int id, time = 0;
        String title, imageURI, author, description, utilities;
        //Bitmap image;
        EditState state;
        ArrayList<Ingredient> ingredients;
        String[] tags;
        ArrayList<String> steps;
        int peopleCounter = 1;
        boolean vegetarian = false, vegan = false, isFavorite = false;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setTime(int time){
            this.time = time;
            return this;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setDescription(String desc){
            this.description = desc;
            return this;
        }

        public Builder setImageUri(String uri){
            this.imageURI = uri;
            return this;
        }

        public Builder setPeopleCounter(int count){
            this.peopleCounter = count;
            return this;
        }

        public Builder setUtilities(String u){
            this.utilities = u;
            return this;
        }

        public Builder setAuthor(String author){
            this.author = author;
            return this;
        }

        public Builder setState(EditState state){
            this.state = state;
            return this;
        }

        public Builder setIngredients(ArrayList<Ingredient> ingredients){
            this.ingredients = new ArrayList<>(ingredients);
            return this;
        }

        public Builder setTags(String[] tags){
            String[] newTags = new String[tags.length];
            int i = 0;
            for(String tag: tags) newTags[i++] = tag.toLowerCase();
            this.tags = newTags;
            return this;
        }

        public Builder setSteps(List<String> steps){
            this.steps = new ArrayList<>(steps);
            return this;
        }

        public Builder setVegetarian(boolean veggie){
            this.vegetarian = veggie;
            return this;
        }

        public Builder setVegan(boolean vegan){
            this.vegan = vegan;
            return this;
        }

        public Builder setIsFavorite(boolean fav){
            this.isFavorite = fav;
            return this;
        }

        public Recipe build(){
            Recipe r =  new Recipe(id, title, imageURI, time, vegetarian, vegan, author, description, state, peopleCounter,
                    utilities, tags, ingredients, steps);
            r.setIsFavorite(isFavorite);
            return r;
        }

    }
}
