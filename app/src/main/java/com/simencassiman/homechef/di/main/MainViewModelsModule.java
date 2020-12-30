package com.simencassiman.homechef.di.main;

import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.di.ViewModelKey;
import com.simencassiman.homechef.viewmodels.AddEditRecipeViewModel;
import com.simencassiman.homechef.viewmodels.AddIngredientViewModel;
import com.simencassiman.homechef.viewmodels.ConverterViewModel;
import com.simencassiman.homechef.viewmodels.HomeViewModel;
import com.simencassiman.homechef.viewmodels.LoginViewModel;
import com.simencassiman.homechef.viewmodels.MainViewModel;
import com.simencassiman.homechef.viewmodels.RecipeListViewModel;
import com.simencassiman.homechef.viewmodels.RecipeViewModel;
import com.simencassiman.homechef.viewmodels.ShoppingListViewModel;
import com.simencassiman.homechef.viewmodels.ShoppingListsListViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class MainViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    public abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    public abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeListViewModel.class)
    public abstract ViewModel bindRecipeListViewModel(RecipeListViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RecipeViewModel.class)
    public abstract ViewModel bindRecipeViewModel(RecipeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddEditRecipeViewModel.class)
    public abstract ViewModel bindAddEditRecipeViewModel(AddEditRecipeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ShoppingListViewModel.class)
    public abstract ViewModel bindShoppingListViewModel(ShoppingListViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ShoppingListsListViewModel.class)
    public abstract ViewModel bindShoppingListsListViewModel(ShoppingListsListViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ConverterViewModel.class)
    public abstract ViewModel bindConverterViewModel(ConverterViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AddIngredientViewModel.class)
    public abstract ViewModel bindAddIngredientViewModel(AddIngredientViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    public abstract ViewModel bindLoginViewModel(LoginViewModel viewModel);

}
