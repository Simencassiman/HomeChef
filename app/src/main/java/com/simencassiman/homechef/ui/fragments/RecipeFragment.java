package com.simencassiman.homechef.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.util.CustomScrollLinearLayoutManager;
import com.simencassiman.homechef.util.RecipeInstructionAdapter;
import com.simencassiman.homechef.util.StringUtil;
import com.simencassiman.homechef.viewmodels.RecipeViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;


public class RecipeFragment extends DaggerFragment implements View.OnClickListener {

    private static final String TAG = "RecipeFragment";


    private RecipeViewModel viewModel;
    @Inject
    ViewModelProviderFactory providerFactory;

    private TextView title;
    private ChipGroup tags;
    private TextView description;
    private LinearLayout ingredientsContainer;
    private RecyclerView instructions;
    private RecipeInstructionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this, providerFactory).get(RecipeViewModel.class);

        viewModel.setRecipeId(RecipeFragmentArgs.fromBundle(getArguments()).getRecipeId());

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe, container, false);

        title = v.findViewById(R.id.tv_title);
        tags = v.findViewById(R.id.chip_group_tags);
        description = v.findViewById(R.id.tv_description);
        ingredientsContainer = v.findViewById(R.id.ll_ingredients_container);

        //Set click listeners on buttons
        ingredientsContainer.findViewById(R.id.cl_people_counter)
                .findViewById(R.id.bt_people_counter_minus).setOnClickListener(this);
        ingredientsContainer.findViewById(R.id.cl_people_counter)
                .findViewById(R.id.bt_people_counter_plus).setOnClickListener(this);

        instructions = v.findViewById(R.id.rv_instructions);

        bindInstructions();
        loadRecipe();
        return v;
    }

    private void bindInstructions(){
        instructions.setLayoutManager(new CustomScrollLinearLayoutManager(getActivity()));
        adapter = new RecipeInstructionAdapter();

        adapter.setMode(RecipeInstructionAdapter.VIEW);
        instructions.setAdapter(adapter);
        instructions.setHasFixedSize(true);
    }

    private void loadRecipe(){
        viewModel.loadRecipe();
        if (viewModel.getRecipe() != null){
            title.setText(StringUtil.capitalizeWords(viewModel.getRecipe().getTitle()));
            description.setText(viewModel.getRecipe().getDescription());
            ((TextView) ingredientsContainer.findViewById(R.id.cl_people_counter)
                    .findViewById(R.id.tv_people_counter))
                        .setText(String.valueOf(viewModel.getRecipe().getPeopleCount()));
            attachIngredients();
            attachTags();
            adapter.setInstructions(viewModel.getRecipe().getInstructions());
        }else{ title.setText(R.string.no_recipe);}
    }

    private void attachIngredients(){
        int i = ingredientsContainer.indexOfChild(
                ingredientsContainer.findViewById(R.id.cl_people_counter)) + 1;
        for(Ingredient ingredient: viewModel.getRecipe().getIngredients()){
            View ingredient_layout = getLayoutInflater()
                    .inflate(R.layout.item_ingredient, ingredientsContainer, false);

            ((TextView)ingredient_layout.findViewById(R.id.tv_ingredient))
                    .setText(StringUtil.capitalize(ingredient.getName()));
            ((TextView)ingredient_layout.findViewById(R.id.tv_amount))
                    .setText(ingredient.getAmount().toString());

            //Add the inflated View to the layout
            ingredientsContainer.addView(ingredient_layout, i++);
        }
    }

    private void attachTags(){
        for(String tag: viewModel.getRecipe().getTags()){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            Chip chip = (Chip) inflater.inflate(R.layout.item_chip,null,false);
            chip.setText(tag);
            tags.addView(chip);
        }
    }

    private void updatePeopleCount(boolean increment){
        //Update recipe
        try{
            viewModel.updatePeopleCount(increment);
        }catch (IllegalStateException e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Update UI
        ((TextView) ingredientsContainer.findViewById(R.id.cl_people_counter)
                .findViewById(R.id.tv_people_counter))
                    .setText(String.valueOf(viewModel.getRecipe().getPeopleCount()));
        updateIngredients();
    }

    private void updateIngredients() {
        int i = ingredientsContainer.indexOfChild(
                ingredientsContainer.findViewById(R.id.cl_people_counter)) + 1;
        for(Ingredient ingredient: viewModel.getRecipe().getIngredients()){
            ((TextView) ingredientsContainer.getChildAt(i++).findViewById(R.id.tv_amount))
                    .setText(ingredient.getAmount().toString());
        }
    }

    private void addToFavorites(){
        viewModel.addToFavorites();
        getActivity().invalidateOptionsMenu();
    }

    private void removeFromFavorites(){
        viewModel.removeFromFavorites();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_people_counter_minus:
                updatePeopleCount(false);
                break;
            case R.id.bt_people_counter_plus:
                updatePeopleCount(true);
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipe, menu);
        menu.findItem(R.id.recipe_favourite).setVisible(!viewModel.getRecipe().isFavorite());
        menu.findItem(R.id.recipe_unFavourite).setVisible(viewModel.getRecipe().isFavorite());
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.recipe_favourite).setVisible(!viewModel.getRecipe().isFavorite());
        menu.findItem(R.id.recipe_unFavourite).setVisible(viewModel.getRecipe().isFavorite());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.recipe_edit:
                RecipeFragmentDirections.EditAction action =
                        RecipeFragmentDirections.editAction();
                action.setRecipeId(viewModel.getRecipe().getId());
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(action);
                break;
            case R.id.recipe_favourite:
                addToFavorites();
                break;
            case R.id.recipe_unFavourite:
                removeFromFavorites();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
