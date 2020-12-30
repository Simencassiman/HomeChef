package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.model.Recipe;

import javax.inject.Inject;

public class RecipeViewModel extends ViewModel {

    private Repository repository;

    @Inject
    public RecipeViewModel(Repository repo) {
        this.repository = repo;
        this.recipeId = -1;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    private int recipeId;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    private Recipe recipe;

    public void loadRecipe(){
        if(recipeId != -1){
            recipe = repository.getRecipeById(recipeId);
        }
    }

    public void addToFavorites(){
        recipe.setIsFavorite(true);
        repository.addToFavorites(recipe);
    }

    public void removeFromFavorites(){
        recipe.setIsFavorite(false);
        repository.removeFromFavorites(recipe);
    }

    public void updatePeopleCount(boolean increment) throws IllegalStateException{

        if (getRecipe().getPeopleCount() == Recipe.MAX_PEOPLE_COUNT && increment)
            throw new IllegalStateException(Recipe.MAX_PEOPLE_COUNT +
                                            " is the maximum amount of people");
        else if (getRecipe().getPeopleCount() == Recipe.MIN_PEOPLE_COUNT && !increment)
            throw new IllegalStateException(Recipe.MIN_PEOPLE_COUNT +
                                            " is the minimum amount of people");

        try {
            if(increment) getRecipe().updatePeopleCount(getRecipe().getPeopleCount()+1);
            else getRecipe().updatePeopleCount(getRecipe().getPeopleCount()-1);
        }catch (IllegalArgumentException e){
            throw new IllegalStateException(e.getMessage());
        }
    }
}
