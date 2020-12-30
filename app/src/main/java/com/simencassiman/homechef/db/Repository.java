package com.simencassiman.homechef.db;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.simencassiman.homechef.model.Amount;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Rational;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.ShoppingList;
import com.simencassiman.homechef.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Repository {

    private static final String TAG = "Repository";

    public static int PAGE_SIZE = 30;
    private RecipeDao recipeDao;
    private PagedList.Config config;

    @Inject
    public Repository(RecipeDao dao){

        this.recipeDao = dao;
        config = (new PagedList.Config.Builder())
                .setPageSize(PAGE_SIZE)
                .build();
    }

    public Recipe getRecipeById(int id){
        Recipe recipe = null;
        try {
            recipe = new DbAccessAsyncTask<Recipe, Integer>(recipeDao, DbAccessAsyncTask.GET_RECIPE,
                                            id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return recipe;
    }

    public LiveData<PagedList<Recipe>> getAllRecipes(){
        //Get update from firebase

        return new LivePagedListBuilder<>(recipeDao.getAllTitles(), config).build();
    }

    public LiveData<PagedList<Recipe>> getRecipesFromSearch(String... tags){
        //Get update from firebase
        return null;
    }

    public LiveData<PagedList<Recipe>> getFavorites(){
        //Get update from firebase
        return new LivePagedListBuilder<>(recipeDao.getFavoriteTitles(), config).build();
    }

    public LiveData<PagedList<Recipe>> getMyRecipes(User user){
        //Get update from firebase
        return null;
    }

    public void insertRecipe(Recipe recipe){
        new DbAccessAsyncTask<Recipe, Recipe>(recipeDao, DbAccessAsyncTask.INSERT_RECIPE, recipe).execute();
    }

    public void updateRecipe(Recipe oldRecipe, Recipe newRecipe){
        new DbAccessAsyncTask<Recipe,Recipe>(recipeDao, DbAccessAsyncTask.UPDATE_RECIPE,
                                oldRecipe, newRecipe).execute();
    }

    public void deleteRecipe(Recipe... recipes){
        new DbAccessAsyncTask<Recipe,Recipe>(recipeDao, DbAccessAsyncTask.DELETE_RECIPE, recipes).execute();
    }

    public void deleteAll(){
        new DbAccessAsyncTask<Recipe,Void>(recipeDao, DbAccessAsyncTask.DELETE_RECIPE).execute();
    }

    public void addToFavorites(Recipe... recipes){
        new DbAccessAsyncTask<Recipe,Recipe>(recipeDao, DbAccessAsyncTask.ADD_TO_FAVORITES, recipes).execute();
    }

    public void removeFromFavorites(Recipe... recipes){
        new DbAccessAsyncTask<Recipe,Recipe>(recipeDao, DbAccessAsyncTask.REMOVE_FROM_FAVORITES, recipes).execute();
    }

    public void addRecipesToShoppingList(Recipe[] recipes, Integer... listIds){
        for(Integer listId: listIds) {
            Integer[] ids = new Integer[recipes.length+1];
            ids[0] = listId;
            for (int i = 1; i < ids.length;i++) ids[i] = recipes[i-1].getId();
            new DbAccessAsyncTask<Void, Integer>(recipeDao,
                    DbAccessAsyncTask.ADD_RECIPES_TO_SHOPPING_LIST, ids).execute();
        }
    }

    public void removeRecipeFromShoppingList(ShoppingListRecipe recipe){
        new DbAccessAsyncTask<Void,ShoppingListRecipe>(recipeDao,
                DbAccessAsyncTask.REMOVE_RECIPE_FROM_SHOPPING_LIST,recipe).execute();
    }

    public ShoppingList getShoppingList(int id){
        try {
            return new DbAccessAsyncTask<ShoppingList,Integer>(recipeDao,DbAccessAsyncTask.GET_SHOPPING_LIST,id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "getShoppingList: " + e.getMessage());
        }
        return null;
    }

    public LiveData<List<ShoppingList>> getShoppingLists(){
        return recipeDao.getShoppingLists();
    }

    public void insertShoppingList(ShoppingList... lists){
        new DbAccessAsyncTask<Void,ShoppingList>(recipeDao,DbAccessAsyncTask.INSERT_SHOPPING_LIST,lists).execute();
    }

    public void updateShoppingList(ShoppingList... lists){

    }

    public void deleteShoppingList(ShoppingList... lists){
        new DbAccessAsyncTask<Void,ShoppingList>(recipeDao,DbAccessAsyncTask.DELETE_SHOPPING_LIST,lists).execute();
    }

    public void emptyShoppingList(int id){
        new DbAccessAsyncTask<Void,Integer>(recipeDao,DbAccessAsyncTask.REMOVE_RECIPE_FROM_SHOPPING_LIST,id).execute();
    }

    public boolean isUniqueListId(int id){
        try {
            return (new DbAccessAsyncTask<ShoppingList,Integer>(recipeDao,DbAccessAsyncTask.GET_SHOPPING_LIST, id).execute().get())
                    == null;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "isUniqueListId: " + e.getMessage());
        }
        return false;
    }

    public void updateShoppingListRecipeCount(int listId, int recipeId, int count){
        Integer[] elements = {listId, recipeId, count};
        new DbAccessAsyncTask<Void,Integer>(recipeDao,
                DbAccessAsyncTask.UPDATE_SHOPPING_LIST_RECIPE_COUNT,elements).execute();
    }

    public void insertShoppingListIngredient(int listId, int recipeId, Ingredient ingredient)
            throws IllegalArgumentException{
        ShoppingListIngredient ing;
        if(recipeId >= 0){
            ing = ShoppingListRecipeIngredient.builder()
                                              .setListId(listId)
                                              .setRecipeId(recipeId)
                                              .setIngredient(ingredient)
                                              .build();
        }else{
            ing = new ShoppingListPureIngredient(listId, ingredient,false);
        }
        new DbAccessAsyncTask<Void, ShoppingListIngredient>(recipeDao,
                DbAccessAsyncTask.INSERT_SHOPPING_LIST_INGREDIENT,ing).execute();
    }

    public void collectIngredient(int listId, String ingredient) throws IllegalArgumentException{
        if(ingredient == null)
            throw new IllegalArgumentException("Ingredient cannot be null.");

        new DbAccessAsyncTask<Void,DbInputPackage<String>>(recipeDao,
                DbAccessAsyncTask.COLLECT_INGREDIENT,
                new DbInputPackage(listId,ingredient)).execute();
    }

    public void uncollectIngredient(int listId, String name){
        new DbAccessAsyncTask<Void,DbInputPackage<String>>(recipeDao,
                DbAccessAsyncTask.UNCOLLECT_INGREDIENT,new DbInputPackage(listId,name)).execute();
    }

    public List<String>  countEverythingOnList(int id){
        try {
            return new DbAccessAsyncTask<List<String>,Integer>(recipeDao,DbAccessAsyncTask.EVERYTHING,id).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class DbAccessAsyncTask<T,C> extends AsyncTask<Void, Void, T>{

        static final int GET_RECIPE            = 0;
        static final int INSERT_RECIPE         = 1;
        static final int UPDATE_RECIPE         = 2;
        static final int DELETE_RECIPE         = 3;
        static final int ADD_TO_FAVORITES      = 4;
        static final int REMOVE_FROM_FAVORITES = 5;
        static final int GET_SHOPPING_LIST     = 6;
        static final int INSERT_SHOPPING_LIST  = 7;
        static final int UPDATE_SHOPPING_LIST  = 8;
        static final int DELETE_SHOPPING_LIST  = 9;
        static final int ADD_RECIPES_TO_SHOPPING_LIST     = 10;
        static final int REMOVE_RECIPE_FROM_SHOPPING_LIST = 11;
        static final int UPDATE_SHOPPING_LIST_RECIPE_COUNT= 12;
        static final int INSERT_SHOPPING_LIST_INGREDIENT  = 13;
        static final int UPDATE_SHOPPING_LIST_INGREDIENT  = 14;
        static final int COLLECT_INGREDIENT               = 15;
        static final int UNCOLLECT_INGREDIENT             = 16;
        static final int EVERYTHING = 30;

        final int[] ID_TASKS = {GET_RECIPE, GET_SHOPPING_LIST, ADD_RECIPES_TO_SHOPPING_LIST,
                        REMOVE_RECIPE_FROM_SHOPPING_LIST,UPDATE_SHOPPING_LIST_RECIPE_COUNT,EVERYTHING};
        final int[] RECIPE_TASKS = {INSERT_RECIPE, UPDATE_RECIPE, DELETE_RECIPE,
                        ADD_TO_FAVORITES, REMOVE_FROM_FAVORITES};
        final int[] SHOPPING_LIST_TASKS = {INSERT_SHOPPING_LIST,
                        UPDATE_SHOPPING_LIST, DELETE_SHOPPING_LIST};
        final int[] SHOPPING_LIST_RECIPE_TASKS = {REMOVE_RECIPE_FROM_SHOPPING_LIST};
        final int[] SHOPPING_LIST_INGREDIENT_TASKS = {INSERT_SHOPPING_LIST_INGREDIENT,
                        UPDATE_SHOPPING_LIST_INGREDIENT};
        final int[] DB_PACKAGE_TASKS = {COLLECT_INGREDIENT, UNCOLLECT_INGREDIENT};

        final Dictionary<Class,int[]> VALID_TASKS;

        private int task;
        private C[] elements;
        private RecipeDao recipeDao;

        DbAccessAsyncTask(RecipeDao dao, int task){
            this.recipeDao = dao;
            this.task = task;
            VALID_TASKS = new Hashtable<>();
        }

        DbAccessAsyncTask(RecipeDao dao, int task, C... elements)
                throws IllegalArgumentException{
            this.recipeDao = dao;
            this.task = task;
            this.elements = elements;

            VALID_TASKS = new Hashtable<>();
            initializeValidTasks();

            if(elements.length > 0 && VALID_TASKS.get(elements[0].getClass()) != null &&
                    !contains(VALID_TASKS.get(elements[0].getClass()), task))
                throw new IllegalArgumentException("Task doesn't match given arguments");
            if(elements.length > 0 && VALID_TASKS.get(elements[0].getClass()) == null)
                throw new IllegalArgumentException("Unknown elements class");
            if(elements.length == 0)
                throw new IllegalArgumentException("Empty elements array provided");
        }

        private void initializeValidTasks(){
            VALID_TASKS.put(Integer.class, ID_TASKS);
            VALID_TASKS.put(Recipe.class, RECIPE_TASKS);
            VALID_TASKS.put(ShoppingList.class, SHOPPING_LIST_TASKS);
            VALID_TASKS.put(ShoppingListRecipe.class, SHOPPING_LIST_RECIPE_TASKS);
            VALID_TASKS.put(ShoppingListRecipeIngredient.class, SHOPPING_LIST_INGREDIENT_TASKS);
            VALID_TASKS.put(ShoppingListPureIngredient.class, SHOPPING_LIST_INGREDIENT_TASKS);
            VALID_TASKS.put(DbInputPackage.class, DB_PACKAGE_TASKS);
        }

        private boolean contains(int[] tasks, int task){
            for(int t: tasks){
                if(t == task) return true;
            }
            return false;
        }

        @Override
        protected T doInBackground(Void... voids) throws IllegalArgumentException, IllegalStateException{
            switch (task){
                case GET_RECIPE:
                    return (T) getRecipe((Integer) elements[0]);
                case INSERT_RECIPE:
                    insertRecipe(elements);
                    break;
                case UPDATE_RECIPE:
                    updateRecipe(elements);
                    break;
                case DELETE_RECIPE:
                    deleteRecipe(elements);
                    break;
                case ADD_TO_FAVORITES:
                    addToFavorites(elements);
                    break;
                case REMOVE_FROM_FAVORITES:
                    removeFromFavorites(elements);
                    break;
                case ADD_RECIPES_TO_SHOPPING_LIST:
                    addRecipesToShoppingList(elements);
                    break;
                case REMOVE_RECIPE_FROM_SHOPPING_LIST:
                    removeRecipeFromShoppingList(elements);
                    break;
                case GET_SHOPPING_LIST:
                    return (T) getShoppingList((Integer) elements[0]);
                case INSERT_SHOPPING_LIST:
                    insertShoppingLists(elements);
                    break;
                case UPDATE_SHOPPING_LIST:
                    updateShoppingList(elements);
                    break;
                case DELETE_SHOPPING_LIST:
                    deleteShoppingList(elements);
                    break;
                case UPDATE_SHOPPING_LIST_RECIPE_COUNT:
                    updateRecipeCount((Integer) elements[0], (Integer) elements[1], (Integer) elements[2]);
                    break;
                case INSERT_SHOPPING_LIST_INGREDIENT:
                    insertShoppingListIngredient((ShoppingListIngredient) elements[0]);
                    break;
                case UPDATE_SHOPPING_LIST_INGREDIENT:
                    updateShoppingListIngredient(elements);
                    break;
                case COLLECT_INGREDIENT:
                    collectIngredient(elements);
                    break;
                case UNCOLLECT_INGREDIENT:
                    uncollectIngredients(elements);
                    break;
                case EVERYTHING:
                    return (T) getEverything((Integer) elements[0]);
            }
            return null;
        }


        // Recipes

        private Recipe getRecipe(int id) throws IllegalArgumentException{
            Recipe recipe = recipeDao.getRecipeById(id);
            if(recipe == null)
                throw new IllegalArgumentException("There is no recipe with the provided ID: "+id);

            ArrayList<Ingredient> ingredients = new ArrayList<>();
            for(RecipeIngredient ingredient: recipeDao.getRecipeIngredients(id))
                ingredients.add(ingredient.getIngredient());

            ArrayList<String> tags = new ArrayList<>();
            for(RecipeTag tag: recipeDao.getRecipeTags(id))
                tags.add(tag.getTag());

            ArrayList<String> instructions = new ArrayList<>();
            for(RecipeInstruction ins: recipeDao.getRecipeInstructions(id)) {
                if (!ins.getText().trim().isEmpty())
                    instructions.add(ins.getText());
            }

            boolean isFavorite = recipeDao.inFavorites(id);

            recipe.setIngredients(ingredients);
            recipe.setTags(tags);
            recipe.setIsFavorite(isFavorite);
            recipe.setInstructions(instructions);
            return recipe;
        }

        private void insertRecipe(C... recipes){
            for(C recipe: recipes){
                if(recipe != null) {
                    RecipeIngredient[] adaptedIngredients = getRecipeIngredients((Recipe) recipe);
                    RecipeTag[] adaptedTags = getRecipeTags((Recipe) recipe);
                    RecipeInstruction[] instructions = getRecipeInstructions((Recipe) recipe);

                    recipeDao.insertRecipe((Recipe) recipe);
                    recipeDao.insertAllRecipeIngredients(adaptedIngredients);
                    recipeDao.insertAllRecipeTags(adaptedTags);
                    recipeDao.insertAllRecipeInstructions(instructions);
                }
            }
        }

        private void updateRecipe(C... recipes) throws IllegalArgumentException{
            Recipe recipe0 = (Recipe) recipes[0];
            Recipe recipe1 = (Recipe) recipes[1];
            if (recipe0 == null || recipe1 == null)
                throw new IllegalArgumentException("One of the provided recipes is null");
            if(recipe0.getId() != recipe1.getId())
                throw new IllegalArgumentException("Recipe ID's do not match");
            if(!recipeDao.recipeExists(recipe0.getId()))
                throw new IllegalArgumentException("The original recipe does not exist");

            recipeDao.updateRecipe(recipe1);

            if(recipe0.getIngredients() != null && !recipe0.getIngredients().equals(recipe1.getIngredients())){
                RecipeIngredient[] ingredients;
                if (recipe1.getIngredients() != null)
                    ingredients = getRecipeIngredients(recipe1);
                else
                    ingredients = new RecipeIngredient[0];
                recipeDao.deleteRecipeIngredients(recipe1.getId());
                recipeDao.insertAllRecipeIngredients(ingredients);
            }

            if(!Arrays.equals(recipe0.getTags(),recipe1.getTags())){
                RecipeTag[] tags = getRecipeTags(recipe1);
                recipeDao.deleteRecipeTags(recipe1.getId());
                recipeDao.insertAllRecipeTags(tags);
            }

            if(recipe0.getInstructions() != null &&
                    !recipe0.getInstructions().equals(recipe1.getInstructions())){
                RecipeInstruction[] instructions = getRecipeInstructions(recipe1);
                recipeDao.deleteRecipeInstructions(recipe1.getId());
                recipeDao.insertAllRecipeInstructions(instructions);
            }
        }

        private RecipeIngredient[] getRecipeIngredients(Recipe recipe) throws IllegalArgumentException{
            if(recipe == null)
                throw new IllegalArgumentException("Provided recipe is null, cannot retrieve ingredients");
            if(recipe.getIngredients() == null)
                throw new IllegalArgumentException("Provided recipe has no ingredients list");

            RecipeIngredient[] adaptedIngredients = new RecipeIngredient[recipe.getIngredients().size()];
            int i = 0;
            for(Ingredient ingredient: recipe.getIngredients())
                adaptedIngredients[i++] = new RecipeIngredient(recipe.getId(), ingredient);

            return adaptedIngredients;
        }

        private RecipeTag[] getRecipeTags(Recipe recipe) throws IllegalArgumentException{
            if(recipe == null)
                throw new IllegalArgumentException("Provided recipe is null, cannot retrieve tags");
            if(recipe.getTags() == null)
                throw new IllegalArgumentException("Provided recipe has no tags array");

            RecipeTag[] adaptedTags = new RecipeTag[recipe.getTags().length];
            int i = 0;
            for(String tag: recipe.getTags())
                adaptedTags[i++] = new RecipeTag(recipe.getId(), tag);

            return adaptedTags;
        }

        private RecipeInstruction[] getRecipeInstructions(Recipe recipe)
                throws IllegalArgumentException{
            if(recipe == null)
                throw new IllegalArgumentException("Provided recipe is null, cannot retrieve instructions");
            if (recipe.getInstructions() == null)
                return new RecipeInstruction[0];

            List<String> originalInstructions = recipe.getInstructions();
            List<String> newInstructions = new ArrayList<>();
            for(String s: originalInstructions)
                if(!s.trim().isEmpty()) newInstructions.add(s);

            RecipeInstruction[] instructions = new RecipeInstruction[newInstructions.size()];
            for(int i = 0; i < newInstructions.size();i++)
                instructions[i] = new RecipeInstruction(recipe.getId(), i, newInstructions.get(i));

            return instructions;
        }

        private void deleteRecipe(C... recipes){
            if (recipes.length > 0){
                for(C recipe: recipes){
                    if(recipe != null)
                        recipeDao.deleteRecipe((Recipe) recipe);
                }
            }else {
                recipeDao.deleteAll();
            }
        }

        private void addToFavorites(C... recipes){
            for(C recipe: recipes){
                if(recipe != null)
                    recipeDao.insertToFavorites(new FavoriteRecipe(((Recipe) recipe).getId(), -1));
            }
        }

        private void removeFromFavorites(C... recipes){
            for(C recipe: recipes) {
                if (recipe != null)
                    recipeDao.deleteFromFavorites(new FavoriteRecipe(((Recipe) recipe).getId(), -1));
            }
        }

        private void addRecipesToShoppingList(C... ids) throws IllegalArgumentException{
            if(ids.length < 2)
                throw new IllegalArgumentException("At least two IDs should be provided, " +
                        "one for the shopping list and one for the recipe. Length was: "+ids.length);
            int listId = ((Integer) ids[0]); // First id is the shopping list id
            if(!recipeDao.shoppingListExists(listId))
                throw new IllegalArgumentException("There is no shopping list with the provided ID: "+listId);

            for (int i = 1;i < ids.length;i++){
                // Get the recipe info
                Recipe recipe = recipeDao.getRecipeById((Integer) ids[i]);
                if (recipe == null)
                    throw new IllegalArgumentException("There is no recipe with provided ID: "+ids[i]+" (position "+i+")");
                // Add it to the shopping list
                // Check if it is already part of the list, if so, add the people count
                // Maybe at some point issue a warning...
                ShoppingListRecipe r = recipeDao.getShoppingListRecipeById(listId, recipe.getId());
                if (r == null) {
                    insertShoppingListRecipe(new ShoppingListRecipe(listId, recipe.getId(),
                            false, recipe.getPeopleCount()));
                    // Add its ingredients to the list
                    for (RecipeIngredient ingredient: recipeDao.getRecipeIngredients(recipe.getId())){
//                        Log.d(TAG, "addRecipesToShoppingList: inserting ingredient " + ingredient.getName());
                        insertShoppingListIngredient(new ShoppingListRecipeIngredient(listId,
                                ingredient,false));
                    }
                }else
                    if (Recipe.isValidPeopleCount(r.getNrPeople()+recipe.getPeopleCount()))
                        updateRecipeCount(listId,recipe.getId(),recipe.getPeopleCount());
                    else
                        updateRecipeCount(listId,recipe.getId(),Recipe.MAX_PEOPLE_COUNT-r.getNrPeople());
            }
        }

        private void removeRecipeFromShoppingList(C... recipes){
            for(C recipe: recipes) recipeDao.deleteShoppingListRecipe((ShoppingListRecipe) recipe);
        }


        // Shopping lists

        private ShoppingList getShoppingList(int id){
            ShoppingList list = recipeDao.getShoppingListById(id);
            if (list != null) {
                recipeDao.removeCollectedIngredients(id);
                list.setIngredients(recipeDao.getShoppingListIngredients(id));
                list.setRecipies(recipeDao.getShoppingListRecipes(id));
//                if (list.getRecipes().getValue() != null)
//                    Log.d(TAG, "getShoppingList: number of recipes found is " + list.getRecipes().getValue().size());
//                if (list.getIngredients().getValue() != null)
//                    Log.d(TAG, "getShoppingList: number of ingredients found is " + list.getIngredients().getValue().size());
            }

            return list;
        }

        private void insertShoppingLists(C... lists){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.stream(lists).map(ShoppingList.class::cast).forEach(this::insertSingleList);
            }else{
                for(C list: lists) insertSingleList((ShoppingList) list);
            }
        }

        private void insertSingleList(ShoppingList list) throws IllegalArgumentException{
            if(list == null)
                throw new IllegalArgumentException("Provided list is null");

            recipeDao.insertShoppingList(list);

            // See if this will happen, or if it can be done after from within ViewModel
//            // Insert Recipes
//            if (list.getRecipes() != null && list.getRecipes().getValue() != null){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    list.getRecipes().getValue().forEach(r ->
//                            recipeDao.insertShoppingListRecipe(new ShoppingListRecipe(list.getId(),
//                                    r.getId(), false,r.getPeopleCount())));
//                }else{
//                    for(RecipeCollector r: list.getRecipes().getValue()){
//                        recipeDao.insertShoppingListRecipe(new ShoppingListRecipe(list.getId(),
//                                r.getId(), false, r.getPeopleCount()));
//                    }
//                }
//            }
//
//            // Insert ingredients
//            if (list.getIngredients() != null && list.getIngredients().getValue() != null) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    list.getIngredients().getValue().forEach(i ->
//                            recipeDao.insertShoppingListIngredient(new ShoppingListRecipeIngredient(list.getId(), i, false)));
//                }else{
//                    for(RecipeIngredient i: list.getIngredients().getValue()){
//                        recipeDao.insertShoppingListIngredient(new ShoppingListRecipeIngredient(list.getId(), i, false));
//                    }
//                }
//            }
        }

        private void insertShoppingListRecipe(ShoppingListRecipe recipe) throws IllegalArgumentException{
            if (recipe == null)
                throw new IllegalArgumentException("Provided shopping list recipe is null");
            recipeDao.insertShoppingListRecipe(recipe);
        }

        private void insertShoppingListIngredient(ShoppingListIngredient ingredient)
                throws IllegalArgumentException{
            if(ingredient == null)
                throw new IllegalArgumentException("Provided ingredient is null");

            Log.d(TAG, "insertShoppingListIngredient: before normalization: "+ ingredient.getAmount().toString());
            ingredient.getAmount().normalize();
            Log.d(TAG, "insertShoppingListIngredient: after normalization "+ ingredient.getAmount().toString());

            if(ingredient.isFromRecipe()){
                insertShoppingListRecipeIngredient((ShoppingListRecipeIngredient)ingredient);
            }else{
                insertShoppingListPureIngredient((ShoppingListPureIngredient) ingredient);
            }
        }

        private void insertShoppingListRecipeIngredient(ShoppingListRecipeIngredient ingredient)
                throws IllegalArgumentException{
            if(!recipeDao.shoppingListRecipeExists(ingredient.getListId(), ingredient.getRecipeId()))
                throw new IllegalArgumentException("Provided ingredient has an invalid recipe ID");

            recipeDao.insertShoppingListIngredient(ingredient);
        }

        private void insertShoppingListPureIngredient(ShoppingListPureIngredient ingredient){
            if(recipeDao.ingredientAlreadyExists(ingredient.getListId(),ingredient.getName())){
                ShoppingListPureIngredient oldIngredient = recipeDao.getPureIngredient(ingredient.getListId(), ingredient.getName());
                if(oldIngredient.getAmount().getUnit().compatible(ingredient.getAmount().getUnit())) {
                    oldIngredient.getIngredient().add(ingredient.getIngredient());
                    oldIngredient.setCollected(false);
                    recipeDao.updateShoppingListIngredients(oldIngredient);
                }else
                    recipeDao.insertShoppingListIngredient(ingredient);

            }else{
                recipeDao.insertShoppingListIngredient(ingredient);
            }
        }

        private void updateShoppingList(C... lists){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.stream(lists).map(ShoppingList.class::cast).forEach(recipeDao::updateShoppingList);
            }else{
                for(C list: lists) recipeDao.updateShoppingList((ShoppingList) list);
            }
        }

        private void updateSingleList(ShoppingList list){

        }

        private void deleteShoppingList(C... lists){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Arrays.stream(lists).map(ShoppingList.class::cast).forEach(recipeDao::deleteShoppingList);
            }else{
                for(C list: lists)recipeDao.deleteShoppingList((ShoppingList) list);
            }
        }

        private void updateRecipeCount(int listId, int recipeId, int count) throws IllegalArgumentException{
            if(count == 0)
                return;
            if (!recipeDao.shoppingListExists(listId))
                throw new IllegalArgumentException("The provided list ID does not exist: "+listId);
            if (!recipeDao.shoppingListRecipeExists(listId, recipeId))
                throw new IllegalArgumentException("The given list does not contain a recipe with the given ID:" +
                        " listID = "+listId+", recipeID = "+recipeId);

            // Get current count
            ShoppingListRecipe recipe = recipeDao.getShoppingListRecipeById(listId,recipeId);

            // Update recipe count
            int oldCount = recipe.getNrPeople();
            int newCount = oldCount + count;
            if(!Recipe.isValidPeopleCount(newCount))
                throw new IllegalArgumentException("This people count is out of range: " + newCount);

            recipe.setNrPeople(newCount);
            recipe.setCollected(count < 0 && recipe.isCollected()); // If count increases -> uncollect
            recipeDao.updateShoppingListRecipes(recipe);

            // Update ingredients -> uncollect if necessary
            ShoppingListRecipeIngredient[] ingredients = recipeDao.getShoppingListRecipeIngredients(listId,recipeId);
            updateIngredientAmount(ingredients, oldCount, newCount, count);
            recipeDao.updateShoppingListIngredients(ingredients);
        }

        private void updateIngredientAmount(ShoppingListRecipeIngredient[] ingredients, int oldCount,
                                            int newCount, int count)
                    throws IllegalArgumentException, IllegalStateException{
            if(!Recipe.isValidPeopleCount(oldCount))
                throw new IllegalArgumentException("Invalid old count provided: "+oldCount);
            if(!Recipe.isValidPeopleCount(newCount))
                throw new IllegalArgumentException("Invalid new count provided: "+newCount);
            if( newCount-oldCount != count)
                throw new IllegalArgumentException("Invalid count provided: "+count);

            for(ShoppingListRecipeIngredient ingredient: ingredients){
                if(ingredient != null) {
                    Number oldValue = ingredient.getAmount().getValue();
                    if(oldValue == null)
                        throw new IllegalStateException("Ingredient's value is null");
                    Number newValue;
                    if (oldValue instanceof Rational) {
                        newValue = new Rational(((Rational) oldValue).getNumerator() * newCount,
                                ((Rational) oldValue).getDenominator() * oldCount);
                    } else {
                        newValue = (double) Math.round(
                                (oldValue.doubleValue() * newCount / oldCount) * 100) / 100;
                    }

                    ingredient.setAmount(new Amount(newValue, ingredient.getAmount().getUnit()));
                    if (count > 0)
                        ingredient.setCollected(false); // If count increases -> uncollect

                    ingredient.getAmount().normalize();
                }
            }
        }

        private void updateRecipeCollectionStatus(int listId, int recipeId, boolean status)
                throws IllegalArgumentException{
            ShoppingListRecipe recipe = recipeDao.getShoppingListRecipeById(listId,recipeId);
            if(recipe == null)
                throw new IllegalArgumentException("There is no recipe with given ID in the provided list.");

            recipe.setCollected(status);
            recipeDao.updateShoppingListRecipes(recipe);
        }

        private void updateShoppingListIngredient(C... ingredients)
                throws IllegalStateException, IllegalArgumentException{
            for(C ingredient: ingredients) {
                if (!(ingredient instanceof ShoppingListRecipeIngredient))
                    throw new IllegalArgumentException("Ingredient is not of the proper type or null.");

                // Retrieve all recipes that use it

                // Collect ingredient
                // Check if recipes have ingredients left -> mark collected
            }
        }

        private void collectIngredient(C... ingredients){
            for(C ingredient: ingredients){
                // Test for correctness of the input
                updateIngredientCollectionStatus((DbInputPackage) ingredient, true);
            }
        }

        private void uncollectIngredients(C... ingredients){
            for(C ingredient: ingredients){
                // Test for correctness of the input
                updateIngredientCollectionStatus((DbInputPackage) ingredient, false);
            }
        }

        private void updateIngredientCollectionStatus(DbInputPackage ingredient, boolean collected)
                throws IllegalArgumentException{
            if(ingredient == null)
                throw new IllegalArgumentException("Ingredient to (un)collect cannot be null.");
            if(!(ingredient.object instanceof String))
                throw new IllegalArgumentException("Name of the recipe should be a string.");
            int listId = ingredient.id;
            if(!recipeDao.shoppingListExists(listId))
                throw new IllegalArgumentException("The provided list ID does not exist: "+listId);
            String name = (String) ingredient.object;
            if(!recipeDao.ingredientNameExists(listId, name))
                throw new IllegalArgumentException("The provided ingredient is not part of this list: "+name);

            // Update all ingredients with this name
            int[] recipeIds;
            if((recipeIds = recipeDao.getParentRecipeInShoppingList(listId, name)).length > 0) {
                recipeDao.setShoppingListRecipeIngredientCollection(listId, name, collected);
                // Update recipe collection
                for(int recipeId: recipeIds) {
                    if (recipeDao.hasUncollectedIngredients(listId, recipeId))
                        updateRecipeCollectionStatus(listId, recipeId, false);
                    else {
                        updateRecipeCollectionStatus(listId, recipeId, true);
                    }
                }
            }
            if(recipeDao.ingredientAlreadyExists(listId,name))
                recipeDao.setShoppingListPureIngredientCollection(listId, name, collected);

        }

        private void emptyShoppingList(int id) throws IllegalArgumentException{
            if(!recipeDao.shoppingListExists(id))
                throw new IllegalArgumentException("The shopping list to be emptied doesn't exist");

            recipeDao.emptyShoppingListRecipes(id);
            recipeDao.emptyShoppingListIngredients(id);
        }

        private List<String> getEverything(int id) throws IllegalArgumentException{
            if(!recipeDao.shoppingListExists(id))
                throw new IllegalArgumentException("The shopping list to be emptied doesn't exist");

            return recipeDao.getIngredientNamesFromList(id);
        }
    }

    static class DbInputPackage<T>{
        DbInputPackage(int id, T object){
            this.id = id;
            this.object = object;
        }
        public int id;
        public T object;
    }
}
