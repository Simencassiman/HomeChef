package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class RecipeListViewModel extends ViewModel {

    public static final int ALL_RECIPIES_MODE = 0;
    public static final int FAVORITE_RECIPIES_MODE = 1;
    public static final int SHOPPING_LIST_MODE = 2;
    public static final int MY_RECIPES = 3;

    private int mode;
    private int shoppingListId = -1;

    private LiveData<PagedList<Recipe>> recipeList;

    private Repository repository;

    @Inject
    public RecipeListViewModel(Repository repo) {
        this.repository = repo;
        mode = -1;
    }

    public int getMode(){ return this.mode;}

    public void setMode(int mode) throws IllegalArgumentException{
        if(!isValidMode(mode))
            throw new IllegalArgumentException("This is not a valid mode for a recipe list");

        this.mode = mode;
        loadRecipes();
    }

    public boolean isValidMode(int mode){
        List<Integer> modes = new ArrayList<>();
        modes.add(ALL_RECIPIES_MODE);
        modes.add(FAVORITE_RECIPIES_MODE);
        modes.add(SHOPPING_LIST_MODE);
        return modes.contains(mode);
    }

    private LiveData<PagedList<Recipe>> createRecipes(){
        LiveData<PagedList<Recipe>> recipes = new MutableLiveData<>();
        /*recipes.add(new Recipe("Meatballs"));
        recipes.add(new Recipe("Sushi"));
        recipes.add(new Recipe("Spaghetti"));*/
        return recipes;
    }

    private void loadRecipes(){
        switch (getMode()){
            case ALL_RECIPIES_MODE:
                getAllRecipes();
                break;
            case FAVORITE_RECIPIES_MODE:
                getFavorites();
                break;
            case SHOPPING_LIST_MODE:
                getAllRecipes();
                break;
        }
    }


    public void getAllRecipes(){
        recipeList = repository.getAllRecipes();
    }

    public void getRecipesFromSearch(String... tags){
        recipeList = repository.getRecipesFromSearch(tags);
    }

    public void getFavorites(){
        recipeList = repository.getFavorites();
    }

    public LiveData<PagedList<Recipe>> getRecipeList() {
        return recipeList;
    }

    public void getMyRecipes(User user){
        recipeList = repository.getMyRecipes(user);
    }

    private Recipe[] getSelectedItems(@NotNull List<Integer> indices){
        Recipe[] items = new Recipe[indices.size()];
        int i = 0;
        for(Integer ind: indices){
            items[i++] = getRecipeList().getValue().get(ind);
        }
        return items;
    }

    public void deleteRecipes(List<Integer> indices){
        repository.deleteRecipe(getSelectedItems(indices));
    }

    public void addToFavorites(List<Integer> indices){
        repository.addToFavorites(getSelectedItems(indices));
    }

    public void removeFromFavorites(List<Integer> indices){
        repository.removeFromFavorites(getSelectedItems(indices));
    }

    public void addToShoppingList(List<Integer> indices){
        repository.addRecipesToShoppingList(getSelectedItems(indices), getShoppingListId());
    }

    public int getShoppingListId() throws IllegalStateException{
        if (getMode() != SHOPPING_LIST_MODE)
            throw new IllegalStateException("Tried to access shoppingListId, but not in shopping list mode");
        return shoppingListId;
    }

    public void setShoppingListId(int id) throws IllegalStateException, IllegalArgumentException{
        if (getMode() != SHOPPING_LIST_MODE)
            throw new IllegalStateException("Tried to set shoppingListId, but not in shopping list mode");
        if (id < 0)
            throw new IllegalArgumentException("Invalid id: " + id);
        this.shoppingListId = id;
    }
}
