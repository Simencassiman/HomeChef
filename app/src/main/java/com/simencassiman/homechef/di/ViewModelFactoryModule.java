package com.simencassiman.homechef.di;

import androidx.lifecycle.ViewModelProvider;

import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelFactoryModule {

    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelProviderFactory viewModelFactory);

}
