package com.simencassiman.homechef.di;

import android.app.Application;

import com.simencassiman.homechef.db.RecipeDb;
import com.simencassiman.homechef.db.Repository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Singleton
    @Provides
    static RecipeDb provideRecipeDb(Application application){
        return RecipeDb.get(application);
    }

    @Singleton
    @Provides
    static Repository providesRepository(RecipeDb db){
        return new Repository(db.recipeDao());
    }
}
