package com.simencassiman.homechef.ui.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.util.ShoppingListIngredientsAdapter;
import com.simencassiman.homechef.util.ShoppingListRecipesAdapter;
import com.simencassiman.homechef.viewmodels.ShoppingListViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.simencassiman.homechef.viewmodels.RecipeListViewModel.SHOPPING_LIST_MODE;


public class ShoppingListFragment extends DaggerFragment implements View.OnClickListener {

    private static final String TAG = "ShoppingListFragment";

    private ShoppingListViewModel viewModel;

    @Inject
    ViewModelProviderFactory providerFactory;

    private RecyclerView recipesRecyclerView;
    private RecyclerView ingredientsRecyclerView;
    private ShoppingListIngredientsAdapter ingredientsAdapter;
    private ShoppingListRecipesAdapter recipesAdapter;
    private LinearLayoutManager recipellManager;
    private LinearLayoutManager ingredientllManager;
    private EditText listTile;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: started");

        viewModel = new ViewModelProvider(this, providerFactory).get(ShoppingListViewModel.class);

        int listId = ShoppingListFragmentArgs.fromBundle(getArguments()).getListId();
        if(listId >= 0)
            viewModel.loadList(ShoppingListFragmentArgs.fromBundle(getArguments()).getListId());
        else if (!viewModel.hasList())
            viewModel.newList(getResources().getString(R.string.new_list));

        viewModel.getRecipes().observe(this, recipesAdapter::setRecipes);
        viewModel.getIngredients().observe(this, ingredientsAdapter::setIngredients);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        listTile = v.findViewById(R.id.scrollview).findViewById(R.id.container).findViewById(R.id.title_list);
        v.findViewById(R.id.button_add_recipe).setOnClickListener(this);
        v.findViewById(R.id.button_add_ingredient).setOnClickListener(this);

        buildRecipeRecyclerview(v);
        buildIngredientRecyclerview(v);

        return v;
    }

    private void buildRecipeRecyclerview(View v){
        recipesRecyclerView = v.findViewById(R.id.rv_shoppingList_recipes);
        recipesAdapter = new ShoppingListRecipesAdapter();
        recipellManager = new LinearLayoutManager(getActivity());

        new ItemTouchHelper(recipeItemTouchHelperCallback).attachToRecyclerView(recipesRecyclerView);
        recipesRecyclerView.setAdapter(recipesAdapter);
        recipesRecyclerView.setLayoutManager(recipellManager);
        recipesAdapter.setOnItemClickListener(new ShoppingListRecipesAdapter.OnItemClickListener() {
            @Override
            public void onTitleClick(int position) {
                Log.d(TAG, "onItemClick: pressed item "+position);
            }

            @Override
            public void onButtonPlus(int position) {
                incrementPeopleCount(position);
            }

            @Override
            public void onButtonMin(int position) {
                decrementPeopleCount(position);
            }
        });
    }

    private void buildIngredientRecyclerview(View v){
        ingredientsRecyclerView = v.findViewById(R.id.rv_shoppingList_ingredients);
        ingredientsAdapter = new ShoppingListIngredientsAdapter();
        ingredientllManager = new LinearLayoutManager(getActivity());

        new ItemTouchHelper(ingredientItemTouchHelperCallback).attachToRecyclerView(ingredientsRecyclerView);
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
        ingredientsRecyclerView.setLayoutManager(ingredientllManager);
    }

    private void addRecipe(){
        ShoppingListFragmentDirections.ToRecipeList action = ShoppingListFragmentDirections.toRecipeList();
        action.setListMode(SHOPPING_LIST_MODE);
        action.setListId(viewModel.getListId());
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(action);
    }

    private void incrementPeopleCount(int position){
        int count = viewModel.getRecipes().getValue().get(position).getPeopleCount();
        if (Recipe.isValidPeopleCount(count+1))
            viewModel.incrementPeopleCount(position);
        else
            Toast.makeText(getActivity(), "The maximum number of people is 20", Toast.LENGTH_SHORT).show();
    }

    private void decrementPeopleCount(int position){
        int count = viewModel.getRecipes().getValue().get(position).getPeopleCount();
        if (Recipe.isValidPeopleCount(count-1))
            viewModel.decrementPeopleCount(position);
        else
            Toast.makeText(getActivity(), "The minimum number of people is 1", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_add_recipe:
                Log.d(TAG, "onClick: add recipe");
                addRecipe();
                break;
            case R.id.button_add_ingredient:
                // Make dialog pop up
                Log.d(TAG, "onClick: add ingredient");
                ShoppingListFragmentDirections.ToDialogIngredient action =
                        ShoppingListFragmentDirections.toDialogIngredient();
                action.setListId(viewModel.getListId());
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(action);
//                countEverything();
//                viewModel.removeRecipes();
                break;
        }
    }

    ItemTouchHelper.SimpleCallback ingredientItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            viewModel.collectIngredient(viewHolder.getAdapterPosition());
            ingredientsAdapter.notifyDataSetChanged();
        }
    };

    ItemTouchHelper.SimpleCallback recipeItemTouchHelperCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    viewModel.removeRecipe(viewHolder.getAdapterPosition());
                    ingredientsAdapter.notifyDataSetChanged();
                }
            };

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shopping_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.undo:
                viewModel.undo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void countEverything(){
        viewModel.countEverything();
    }
}
