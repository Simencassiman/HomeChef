package com.simencassiman.homechef.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.db.RecipeCollector;
import com.simencassiman.homechef.db.RecipeIngredient;
import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.db.ShoppingListRecipe;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

public class ShoppingListViewModel extends ViewModel {

    private static final String TAG = "ShoppingListViewModel";

    private Repository repository;

    private ShoppingList list;
    private boolean hasList = false;

    private Stack stack = new Stack();

    @Inject
    public ShoppingListViewModel(Repository repo){
        this.repository = repo;
    }

    public void newList(String name){
        Random rand = new Random();
        int id = rand.nextInt(1000);
        while (!repository.isUniqueListId(id))
                id = rand.nextInt(1000);

        repository.insertShoppingList(new ShoppingList(id, name));

        while ((list = repository.getShoppingList(id)) == null){}
        hasList = true;
    }

    public int getListId(){
        return list.getId();
    }

    public LiveData<List<RecipeCollector>> getRecipes(){
        if(list == null)
            return null;
        return list.getRecipes();
    }

    public LiveData<List<Ingredient>> getIngredients(){
        return list.getIngredients();
    }

    public void loadList(int id){
        list = repository.getShoppingList(id);
        if(list != null)
            hasList = true;
    }

    public boolean hasList(){
        return hasList;
    }

    public void changeTitle(String title){

    }

    public void addRecipes(Integer... ids){

    }

    public void addIngredient(Ingredient ingredient){

    }

    public void collectIngredient(int posisiton){
        if(getIngredients().getValue() != null) {
            stack.add(new StackObject<>(RecipeIngredient.class,
                    getIngredients().getValue().get(posisiton).getName(),StackObject.COLLECT));
            repository.collectIngredient(getListId(), getIngredients().getValue().get(posisiton).getName());
        }
    }

    public void removeRecipe(int position) throws IllegalStateException{
        if(getRecipes() == null || getRecipes().getValue() == null)
            throw new IllegalStateException("Cannot remove recipe when there are non.");

        repository.removeRecipeFromShoppingList(new ShoppingListRecipe(getListId(),
                getRecipes().getValue().get(position).getId(),getRecipes().getValue().get(position).collected,
                getRecipes().getValue().get(position).getPeopleCount()));
    }

    public void incrementPeopleCount(int position){
        repository.updateShoppingListRecipeCount(getListId(),
                    getRecipes().getValue().get(position).getId(),1);
    }

    public void decrementPeopleCount(int position){
        repository.updateShoppingListRecipeCount(getListId(),
                    getRecipes().getValue().get(position).getId(),-1);
    }

    public void countEverything(){
        List<Ingredient> i = getIngredients().getValue();
        if (i == null)
            Log.d(TAG, "countEverything: i is null...");
        else
            Log.d(TAG, "countEverything: just counted "+i.size());

        List<String> ing = repository.countEverythingOnList(getListId());
        if (ing == null)
            Log.d(TAG, "countEverything: ing is null...");
        else if(ing.size() == 0)
            Log.d(TAG, "countEverything: ing has no elements");
        else{
            for(String s: ing)
                Log.d(TAG, "countEverything: " + s);
        }

    }

    public void undo(){
        if(stack.size() == 0)
            return;
        StackObject event = stack.pop();
        if(event.getElement() == RecipeCollector.class){

        }else if (event.getElement() == RecipeIngredient.class){
            if(event.getAction() == StackObject.COLLECT)
                repository.uncollectIngredient(getListId(), (String) event.getIdentifier());
        }
    }

    static class Stack {

        Stack(){
            this.stack = new ArrayList<>();
        }
        private final List<StackObject> stack;

        public StackObject pop(){
            StackObject obj = stack.get(0);
            stack.remove(0);
            return obj;
        }

        public void add(StackObject obj) throws IllegalArgumentException{
            if(obj == null)
                throw new IllegalArgumentException("EventStack object may not be null");
            stack.add(0, obj);
        }

        public int size(){
            return stack.size();
        }

        public boolean hasEvents(){
            return stack.size() != 0;
        }
    }

    static class StackObject<T> {
        static final int DELETE = 0;
        static final int COLLECT = 1;

        StackObject(Class element, T identifier, int action) throws IllegalArgumentException {
            if (!isValidIdentifier(element, identifier))
                throw new IllegalArgumentException("Identifier type does not match element class.");

            this.element = element;
            this.identifier = identifier;
            this.action = action;
        }

        public boolean isValidIdentifier(Class element, T identifier) {
            if ((element == RecipeCollector.class && !(identifier instanceof Integer)) ||
                    (element == RecipeIngredient.class && !(identifier instanceof String)))
                return false;
            else
                return true;
        }

        public Class getElement() {
            return element;
        }

        private final Class element;

        public T getIdentifier() {
            return identifier;
        }

        private final T identifier;

        public int getAction() {
            return action;
        }

        private final int action;
    }
}
