package com.simencassiman.homechef.di.main;

import com.simencassiman.homechef.ui.DialogIngredient;
import com.simencassiman.homechef.ui.fragments.AddEditRecipeFragment;
import com.simencassiman.homechef.ui.fragments.ConverterFragment;
import com.simencassiman.homechef.ui.fragments.HomeFragment;
import com.simencassiman.homechef.ui.fragments.LoginFragment;
import com.simencassiman.homechef.ui.fragments.ProfileFragment;
import com.simencassiman.homechef.ui.fragments.RecipeFragment;
import com.simencassiman.homechef.ui.fragments.RecipeListFragment;
import com.simencassiman.homechef.ui.fragments.ShoppingListFragment;
import com.simencassiman.homechef.ui.fragments.ShoppingListsListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector
    abstract HomeFragment contributeHomeFragment();

    @ContributesAndroidInjector
    abstract RecipeListFragment contributeRecipeListFragment();

    @ContributesAndroidInjector
    abstract RecipeFragment contributeRecipeFragment();

    @ContributesAndroidInjector
    abstract AddEditRecipeFragment contributeAddEditRecipeFragment();

    @ContributesAndroidInjector
    abstract ShoppingListFragment contributeListFragment();

    @ContributesAndroidInjector
    abstract ShoppingListsListFragment contributeListsListFragment();

    @ContributesAndroidInjector
    abstract DialogIngredient contributeDialogIngredient();

    @ContributesAndroidInjector
    abstract ConverterFragment contributeConverterFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract LoginFragment contributeLoginFragment();

}
