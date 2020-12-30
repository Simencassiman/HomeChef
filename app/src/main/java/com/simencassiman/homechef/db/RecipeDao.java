package com.simencassiman.homechef.db;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.RecipeTest;
import com.simencassiman.homechef.model.ShoppingList;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RecipeDao {

    int FALSE = 0;
    int TRUE  = 1;

    /*************
     * Boolean Checks
     *************/
    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM recipe" +
            "    WHERE id = :id "+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean recipeExists(int id);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM favorite_recipe" +
            "    WHERE recipe_id = :id" +
            ")" +
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean inFavorites(int id);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM shopping_list" +
            "    WHERE id = :id "+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean shoppingListExists(int id);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM shopping_list_recipe" +
            "    WHERE list_id = :listId AND recipe_id = :recipeId "+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean shoppingListRecipeExists(int listId, int recipeId);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM shopping_list_recipe_ingredient" +
            "    WHERE list_id = :listId AND recipe_id = :recipeId AND collected = "+FALSE+" "+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean hasUncollectedIngredients(int listId, int recipeId);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM ( SELECT name " +
            "           FROM shopping_list_recipe_ingredient " +
            "           WHERE list_id = :listId AND name = :ingredient " +
            "           UNION " +
            "           SELECT name " +
            "           FROM shopping_list_pure_ingredient " +
            "           WHERE list_id = :listId AND name = :ingredient" +
            "         ) all_ingredients"+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean ingredientNameExists(int listId, String ingredient);

    @Query("SELECT CASE WHEN EXISTS (" +
            "    SELECT *" +
            "    FROM shopping_list_pure_ingredient" +
            "    WHERE list_id = :listId AND name = :ingredient"+
            ") "+
            "THEN CAST(1 AS BIT)\n" +
            "ELSE CAST(0 AS BIT) END")
    boolean ingredientAlreadyExists(int listId, String ingredient);

    /*************
     * QUERIES
     *************/

    /*
     * Recipes
     */
    @Query("SELECT * FROM recipe")
    DataSource.Factory<Integer, Recipe> getAll();

    @Query( "SELECT id, title, peopleCount, time, vegetarian, vegan " +
            "FROM recipe ORDER BY title ASC")
    DataSource.Factory<Integer, Recipe>getAllTitles();

    @Query("SELECT * FROM recipe WHERE state = 'PUBLISHED'")
    DataSource.Factory<Integer, Recipe>getAllPublished();

    @Query("SELECT * FROM recipe WHERE state = 'DRAFT'")
    DataSource.Factory<Integer, Recipe> getAllDrafts();

    @Query("SELECT * FROM recipe WHERE state = 'EDITED'")
    DataSource.Factory<Integer, Recipe> getAllEdited();

    @Query("SELECT * FROM recipe WHERE id IN (:recipeIds)")
    DataSource.Factory<Integer, Recipe> loadAllByIds(int[] recipeIds);

    @Query("SELECT * FROM recipe WHERE title LIKE :title")
    DataSource.Factory<Integer, Recipe> findByTitle(String title);

    @Query("SELECT r.id, r.title, r.imageURI, r.description, r.author, r.state, " +
            "r.peopleCount, r.time, r.vegetarian, r.vegan " +
            "FROM recipe as r, recipe_tag as t " +
            "WHERE r.id = t.recipe_id AND t.tag LIKE :tag")
    DataSource.Factory<Integer, Recipe> findByTags(String tag);

    @Query( "SELECT r.id, r.title, r.imageURI, r.description, r.author, r.state, " +
            "r.peopleCount, r.time, r.vegetarian, r.vegan " +
            "FROM recipe as r, recipe_ingredient as i " +
            "WHERE r.id = i.recipe_id AND i.name LIKE :ingredient")
    DataSource.Factory<Integer, Recipe> findByIngredient(String ingredient);

    @Query("SELECT * FROM recipe WHERE id = :id")
    Recipe getRecipeById(int id);

    @Query("SELECT * FROM recipe_ingredient WHERE recipe_id = :id")
    RecipeIngredient[] getRecipeIngredients(int id);

    @Query("SELECT * FROM recipe_tag WHERE recipe_id = :id")
    RecipeTag[] getRecipeTags(int id);

    @Query("SELECT * FROM recipe_step WHERE recipe_id = :id ORDER BY step_nr")
    RecipeInstruction[] getRecipeInstructions(int id);

    /*
     * Favorites
     */
    @Query("SELECT id, title, peopleCount, time, vegetarian, vegan " +
            "FROM recipe " +
            "WHERE id IN (SELECT recipe_id FROM favorite_recipe) " +
            "ORDER BY title ASC")
    DataSource.Factory<Integer, Recipe>getFavoriteTitles();

    /*
     * Shopping list
     */
    @Query( "SELECT id, name, reminder " +
            "FROM shopping_list")
    LiveData<List<ShoppingList>> getShoppingLists();

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    ShoppingList getShoppingListById(int id);

    @Query( "SELECT r.id, r.title, sr.nr_people as peopleCount, sr.collected, " +
            "r.time, r.vegetarian, r.vegan " +
            "FROM recipe AS r, shopping_list_recipe AS sr " +
            "WHERE r.id = sr.recipe_id AND sr.list_id = :id AND sr.list_id != -1")
    LiveData<List<RecipeCollector>> getShoppingListRecipes(int id);

    @Query( "SELECT * " +
            "FROM shopping_list_recipe " +
            "WHERE recipe_id = :recipeId AND list_id = :listId")
    ShoppingListRecipe getShoppingListRecipeById(int listId, int recipeId);

    @Query( "SELECT name, group_concat(value) AS value, unit " +
            "FROM " +
            "   (SELECT name, value, unit " +
            "    FROM shopping_list_recipe_ingredient " +
            "    WHERE list_id = :id AND collected = "+ FALSE +" " +
            "   UNION ALL " +
            "    SELECT name, value, unit " +
            "    FROM shopping_list_pure_ingredient " +
            "    WHERE list_id = :id AND collected = "+FALSE+" " +
            "   ) ingredient " +
            "GROUP BY name, unit ")
            //"ORDER BY ANY(position)")
    LiveData<List<Ingredient>> getShoppingListIngredients(int id);

    @Query( "SELECT unit " +
            "FROM " +
            "   (SELECT name, value, unit " +
            "    FROM shopping_list_recipe_ingredient " +
            "    WHERE list_id = :id " +
            "   UNION ALL " +
            "    SELECT name, value, unit " +
            "    FROM shopping_list_pure_ingredient " +
            "    WHERE list_id = :id " +
            "   ) ingredient " +
            "GROUP BY name, unit ")
        //"ORDER BY ANY(position)")
    List<String> getShoppingListIngredientsValues(int id);

    @Query( "SELECT * " +
            "FROM shopping_list_recipe_ingredient " +
            "WHERE list_id = :id AND name = :name")
    ShoppingListRecipeIngredient[] getShoppingListRecipeIngredientsByName(int id, String name);

    @Query("SELECT recipe_id FROM shopping_list_recipe WHERE list_id = :id")
    List<Integer> getRecipIdsFromList(int id);

    @Query("SELECT name FROM shopping_list_recipe_ingredient WHERE list_id = :id AND collected = "+FALSE)
    List<String> getIngredientNamesFromList(int id);

    @Query( "SELECT * " +
            "FROM shopping_list_recipe_ingredient " +
            "WHERE list_id = :listId AND recipe_id = :recipeId")
    ShoppingListRecipeIngredient[] getShoppingListRecipeIngredients(int listId, int recipeId);

    @Query( "SELECT * " +
            "FROM shopping_list_pure_ingredient " +
            "WHERE list_id = :listId AND name = :ingredient")
    ShoppingListPureIngredient getPureIngredient(int listId, String ingredient);

    @Query( "SELECT recipe_id " +
            "FROM shopping_list_recipe_ingredient " +
            "WHERE list_id = :listId AND name = :ingredientName")
    int[] getParentRecipeInShoppingList(int listId, String ingredientName);

    @Query( "UPDATE shopping_list_recipe_ingredient " +
            "SET collected = :collected " +
            "WHERE list_id = :listId AND name = :ingredient;")
    void setShoppingListRecipeIngredientCollection(int listId, String ingredient, boolean collected);

    @Query( "UPDATE shopping_list_pure_ingredient " +
            "SET collected = :collected " +
            "WHERE list_id = :listId AND name = :ingredient;")
    void setShoppingListPureIngredientCollection(int listId, String ingredient, boolean collected);



    /*************
     * INSERTS
     *************/

    /*
     * Recipes
     */
    @Insert(onConflict = REPLACE)
    void insertRecipe(Recipe recipe);

    @Insert(onConflict = REPLACE)
    void insertAllRecipes(Recipe... recipes);

    @Insert(onConflict = REPLACE)
    void insertAllRecipeIngredients(RecipeIngredient... ingredients);

    @Insert(onConflict = REPLACE)
    void insertAllRecipeTags(RecipeTag... tags);

    @Insert(onConflict = REPLACE)
    void insertAllRecipeInstructions(RecipeInstruction... instructions);

    @Insert(onConflict = REPLACE)
    void insertToFavorites(FavoriteRecipe fav);

    /*
     * Shopping list
     */
    @Insert(onConflict = REPLACE)
    void insertShoppingList(ShoppingList list);

    @Insert(onConflict = REPLACE)
    void insertShoppingListRecipe(ShoppingListRecipe recipe);

    @Insert(onConflict = REPLACE)
    void insertShoppingListIngredient(ShoppingListRecipeIngredient ingredient);

    @Insert(onConflict = REPLACE)
    void insertShoppingListIngredient(ShoppingListPureIngredient ingredient);


    /*************
     * UPDATE
     *************/

    @Update
    void updateRecipe(Recipe recipe);

    @Update
    void updateIngredients(RecipeIngredient... ingredients);

    @Update
    void updateTags(RecipeTag... tags);

    @Update
    void updateShoppingList(ShoppingList list);

    @Update
    void updateShoppingListRecipes(ShoppingListRecipe... recipes);

    @Update
    void updateShoppingListIngredients(ShoppingListRecipeIngredient... ingredients);

    @Update
    void updateShoppingListIngredients(ShoppingListPureIngredient... ingredients);


    /*************
     * DELETE
     *************/

    @Delete
    void deleteRecipe(Recipe recipe);

    @Delete
    void deleteFromFavorites(FavoriteRecipe fav);

    @Query("DELETE FROM recipe")
    void deleteAll();

    @Query("DELETE FROM recipe_ingredient WHERE recipe_id = :id")
    void deleteRecipeIngredients(int id);

    @Query("DELETE FROM recipe_tag WHERE recipe_id = :id")
    void deleteRecipeTags(int id);

    @Query("DELETE FROM recipe_step WHERE recipe_id = :id")
    void deleteRecipeInstructions(int id);

    @Delete
    void deleteShoppingList(ShoppingList list);

    @Delete
    void deleteShoppingListRecipe(ShoppingListRecipe recipe);

    @Query( "DELETE " +
            "FROM shopping_list_recipe " +
            "WHERE list_id = :listId")
    void emptyShoppingListRecipes(int listId);

    @Query( "DELETE " +
            "FROM shopping_list_pure_ingredient " +
            "WHERE list_id = :listId")
    void emptyShoppingListIngredients(int listId);

    @Query( "DELETE " +
            "FROM shopping_list_pure_ingredient " +
            "WHERE list_id = :listId AND collected = "+TRUE)
    void removeCollectedIngredients(int listId);
    @Delete
    void deleteShoppingListIngredient(ShoppingListRecipeIngredient ingredient);

    @Delete
    void deleteShoppingListIngredient(ShoppingListPureIngredient ingredient);


    /*************
     * TEST
     *************/

    @Insert(onConflict = REPLACE)
    void insertRecipeTest(RecipeTest recipeTest);

    @Query("SELECT * FROM recipe_test")
    DataSource.Factory<Integer, RecipeTest> getAllRecipeTests();
}
