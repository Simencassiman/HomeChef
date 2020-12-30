package com.simencassiman.homechef.di;

import com.simencassiman.homechef.di.main.MainFragmentBuildersModule;
import com.simencassiman.homechef.di.main.MainViewModelsModule;
import com.simencassiman.homechef.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
            modules = { MainFragmentBuildersModule.class,
                        MainViewModelsModule.class}
    )
    abstract MainActivity contributeMainActivity();
}
