package com.simencassiman.homechef.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.simencassiman.homechef.model.Note;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.RecipeTest;
import com.simencassiman.homechef.model.ShoppingList;
import com.simencassiman.homechef.model.User;

@Database(entities =   {Recipe.class,
                        RecipeIngredient.class,
                        RecipeTag.class,
                        RecipeInstruction.class,
                        FavoriteRecipe.class,
                        ShoppingList.class,
                        ShoppingListRecipe.class,
                        ShoppingListRecipeIngredient.class,
                        ShoppingListPureIngredient.class,
                        Note.class,
                        NoteIngredient.class,
                        NoteStep.class,
                        User.class,
                        RecipeTest.class},
          exportSchema = false,
          version = 6)
@TypeConverters(Converter.class)
public abstract class RecipeDb extends RoomDatabase {

    public abstract RecipeDao recipeDao();

    private static RecipeDb instance;

    public static synchronized RecipeDb get(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    RecipeDb.class, "recipe_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private static void fillInDb(Context context){}
}
