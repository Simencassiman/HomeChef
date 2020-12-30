package com.simencassiman.homechef.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.viewmodels.HomeViewModel;
import com.simencassiman.homechef.viewmodels.RecipeListViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class HomeFragment extends DaggerFragment implements View.OnClickListener{

    private HomeViewModel viewModel;
    private GridLayout gridLayout;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this, providerFactory).get(HomeViewModel.class);

        gridLayout = view.findViewById(R.id.grid_layout);
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        for(int i = 0; i < gridLayout.getChildCount(); i++){
            gridLayout.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.recipes:
                HomeFragmentDirections.ToRecipeListAction actionAll =
                        HomeFragmentDirections.toRecipeListAction();
                actionAll.setListMode(RecipeListViewModel.ALL_RECIPIES_MODE);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(actionAll);
                break;
            case R.id.recipe_edit:
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(HomeFragmentDirections.toAddEditRecipeFragment());
                break;
            case R.id.favorites:
                HomeFragmentDirections.ToRecipeListAction actionFavorites =
                        HomeFragmentDirections.toRecipeListAction();
                actionFavorites.setListMode(RecipeListViewModel.FAVORITE_RECIPIES_MODE);

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(actionFavorites);
                break;
            case R.id.converter:
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(HomeFragmentDirections.toConverterFragment());
                break;
            case R.id.my_lists:
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(HomeFragmentDirections.toShoppingListsList());
                break;
            case R.id.find_recipe:
                //Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.recipeList);
                break;
        }
    }
}
