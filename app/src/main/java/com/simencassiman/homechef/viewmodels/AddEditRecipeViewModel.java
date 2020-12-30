package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class AddEditRecipeViewModel extends ViewModel {

    public static final int EDIT = 1;
    public static final int ADD = 2;

    private Repository repository;
    private Map<Integer, Unit> selectedUnits = new HashMap<>();

    @Inject
    public AddEditRecipeViewModel(Repository repo){
        this.repository = repo;
        this.peopleCounter = 1;
        this.spinnerCounter = 0;
        this.request = -1;
        this.tags = new ArrayList<>();
        this.instructions = new ArrayList<>();
    }


    public Recipe getOldRecipe() {
        return oldRecipe;
    }

    public void setOldRecipe(Recipe oldRecipe) {
        this.oldRecipe = oldRecipe;
    }

    private Recipe oldRecipe;

    public Recipe getNewRecipe() {
        return newRecipe;
    }

    public void setNewRecipe(Recipe recipe){
        this.newRecipe = recipe;
    }

    private Recipe newRecipe;

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    private int request;

    public void loadRecipe(int id){
        oldRecipe = repository.getRecipeById(id);
        peopleCounter = oldRecipe.getPeopleCount();
        setVegan(oldRecipe.isVegan());
        setVegetarian(oldRecipe.isVegetarian());
        setInstructions(oldRecipe.getInstructions() != null?
                new ArrayList<>(oldRecipe.getInstructions()) : new ArrayList<>());
    }

    public int saveNewRecipe(){
        if (oldRecipe == null && newRecipe != null && request == ADD){
            repository.insertRecipe(newRecipe);
            return 0;
        }else if (oldRecipe != null && newRecipe != null && request == EDIT &&
                !oldRecipe.equals(newRecipe)){
            newRecipe.setId(oldRecipe.getId());
            repository.updateRecipe(oldRecipe, newRecipe);
            return 0;
        }
        return -1;
    }

    public void deleteRecipe(){
        if(oldRecipe != null) repository.deleteRecipe(oldRecipe);
    }

    public int getLastIngredientPosition() {
        return lastIngredientPosition;
    }

    public void setLastIngredientPosition(int lastIngredientPosition) {
        this.lastIngredientPosition = lastIngredientPosition;
    }

    public void incrementLastIngredientPosition(){
        this.lastIngredientPosition ++;
    }

    private int lastIngredientPosition;

    public int getSpinnerCounter() {
        return spinnerCounter;
    }

    public void setSpinnerCounter(int spinnerCounter) {
        this.spinnerCounter = spinnerCounter;
    }

    public void incrementSpinnerCounter(){
        this.spinnerCounter ++;
    }

    public int getValidIdTag(int id){
        int tag = id + getSpinnerCounter();
        incrementSpinnerCounter();
        return  tag;
    }

    private int spinnerCounter;

    public Unit getSpinnerItem(int id){
        return selectedUnits.get(id);
    }

    public void addSpinnerItem(Integer id, Unit unit){
        selectedUnits.put(id, unit);
    }

    public int getPeopleCounter() { return peopleCounter;}

    public void setPeopleCounter(int peopleCounter) {
        if (peopleCounter >= 1 && peopleCounter <= 20)
            this.peopleCounter = peopleCounter;
    }

    public void incrementPeopleCounter() throws IllegalStateException{
        if (peopleCounter == Recipe.MAX_PEOPLE_COUNT)
            throw new IllegalStateException(Recipe.MAX_PEOPLE_COUNT +
                                            " is the maximum amount of people");
        else peopleCounter++;
    }

    public void decreasePeopleCounter() throws IllegalStateException{
        if (peopleCounter == Recipe.MIN_PEOPLE_COUNT)
            throw new IllegalStateException(Recipe.MIN_PEOPLE_COUNT +
                                            " is the minimum amount of people");
        else peopleCounter--;

    }

    private int peopleCounter;

    public String[] getTags(){
        String[] t = new String[tags.size()];
        for(int i = 0; i<tags.size(); i++) t[i] = tags.get(i);
        return t;
    }

    public void addTag(String tag){
        if(!tag.isEmpty())
            tags.add(tag);
    }

    public void removeTag(String tag){
        tags.remove(tag);
    }

    public boolean containsTag(String tag){
        return tags.contains(tag);
    }

    private List<String> tags;

    public boolean isVegetarian(){ return vegetarian;}

    public void setVegetarian(boolean state){
        if(!state)
            setVegan(false);
        vegetarian = state;
    }

    private boolean vegetarian;

    public boolean isVegan(){
        return vegan;
    }

    public void setVegan(boolean state){
        if(state)
            setVegetarian(true);
        vegan = state;
    }

    private boolean vegan;

    public List<String> getInstructions(){
        return this.instructions;
    }

    public void setInstructions(List<String> instructions){
        this.instructions = instructions;
    }

    private List<String> instructions;
}
