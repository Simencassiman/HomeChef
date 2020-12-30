package com.simencassiman.homechef.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.util.RecipeAdapter;
import com.simencassiman.homechef.viewmodels.RecipeListViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.simencassiman.homechef.viewmodels.RecipeListViewModel.SHOPPING_LIST_MODE;


public class RecipeListFragment extends DaggerFragment implements View.OnClickListener {

    private static final String TAG = "RecipeListFragment";

    public static final boolean SELECTION_MODE_ON = true;
    public static final boolean SELECTION_MODE_OFF = false;

    private RecipeListViewModel viewModel;
    private RecyclerView recyclerView;
    private RecipeAdapter adapter;
    private LinearLayoutManager llManager;
    private Button buttonOk;

    OnBackPressedCallback callback;

    @Inject
    ViewModelProviderFactory providerFactory;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreate: started");

        int listMode = RecipeListFragmentArgs.fromBundle(getArguments()).getListMode();
        viewModel = new ViewModelProvider(this, providerFactory).get(RecipeListViewModel.class);

        viewModel.setMode(listMode);
        viewModel.getRecipeList().observe(this, adapter::submitList);

        callback = new OnBackPressedCallback(false /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                exitSelectionMode();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()

        setHasOptionsMenu(true);

        // If it's to add to the shopping list, enable selection mode immediately
        if (listMode == SHOPPING_LIST_MODE) {
            viewModel.setShoppingListId(RecipeListFragmentArgs.fromBundle(getArguments()).getListId());
            enterSelectionMode();
            // Make ok button visible
            buttonOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list, container, false);

        buildRecyclerview(v);

        buttonOk = v.findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(this);

        return v;
    }

    private void buildRecyclerview(View v){
        adapter = new RecipeAdapter();
        llManager = new LinearLayoutManager(getActivity());
        recyclerView = v.findViewById(R.id.recyclerView_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llManager);
        adapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                goToRecipeFragment(position);
            }

            @Override
            public void onItemLongClick(int position) {
                enterSelectionMode();
            }
        });
    }

    private void goToRecipeFragment(int position){
        try {
            Recipe recipe = viewModel.getRecipeList().getValue().get(position);
            if(recipe != null){
                RecipeListFragmentDirections.RecipeAction action =
                        RecipeListFragmentDirections.recipeAction();
                action.setRecipeId(recipe.getId());

                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(action);
            }
        }catch (NullPointerException e){
            Log.d(TAG, "goToRecipeFragment: NullPointerException on item selection, " + e.getMessage());
//            Toast.makeText(getActivity(), "NullPointer", Toast.LENGTH_SHORT).show();
        }
    }

    private void enterSelectionMode(){
        // Deactivate back button press (1st press)
        if (viewModel.getMode() != SHOPPING_LIST_MODE)
            callback.setEnabled(true);

        // Update items (onClick events)
        adapter.setInSelectionMode();
        adapter.notifyDataSetChanged();

        // Activate options menu
        getActivity().invalidateOptionsMenu();
    }

    private void exitSelectionMode(){
        // Activate back button press (1st press)
        callback.setEnabled(false);

        // Update items (onClick events)
        adapter.deassertSelectionMode();
        adapter.notifyDataSetChanged();

        // Deactivate options menu
        getActivity().invalidateOptionsMenu();
    }

    private void deleteRecipes(){
        if(adapter.inSelectionMode())
            viewModel.deleteRecipes(adapter.getSelectedItems());
        else
            Toast.makeText(getActivity(),"No recipes selected", Toast.LENGTH_SHORT).show();

        exitSelectionMode();
    }

    private void addToFavorites(){
        if(adapter.inSelectionMode())
            viewModel.addToFavorites(adapter.getSelectedItems());
        else
            Toast.makeText(getActivity(),"No recipes selected", Toast.LENGTH_SHORT).show();

        exitSelectionMode();
    }

    private void removeFromFavorites(){
        if(adapter.inSelectionMode())
            viewModel.removeFromFavorites(adapter.getSelectedItems());
        else
            Toast.makeText(getActivity(),"No recipes selected", Toast.LENGTH_SHORT).show();

        exitSelectionMode();
    }

    private void addToShoppingList(){
        if(adapter.inSelectionMode()){
            if(viewModel.getMode() == SHOPPING_LIST_MODE)
                viewModel.addToShoppingList(adapter.getSelectedItems());
            else{
                // Go to shopping list selection
                viewModel.addToShoppingList(adapter.getSelectedItems());
            }
        }else
            Toast.makeText(getActivity(),"No recipes selected", Toast.LENGTH_SHORT).show();

        exitSelectionMode();
        if(viewModel.getMode() == SHOPPING_LIST_MODE) {
            RecipeListFragmentDirections.ToShoppingList action = RecipeListFragmentDirections.toShoppingList();
            action.setListId(viewModel.getShoppingListId());
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_recipe_list, menu);
        setMenuItemsVisibility(menu, SELECTION_MODE_OFF);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(adapter.inSelectionMode()) {
            if(viewModel.getMode() == SHOPPING_LIST_MODE)
                hideMenu(menu);
            else
                setMenuItemsVisibility(menu, SELECTION_MODE_ON);
        }else{
            setMenuItemsVisibility(menu, SELECTION_MODE_OFF);
        }
    }

    private void setMenuItemsVisibility(Menu menu, boolean visible){
        menu.findItem(R.id.add_recipe).setVisible(!visible);
        menu.findItem(R.id.delete).setVisible(visible);
        menu.findItem(R.id.add_to_shopping_list).setVisible(visible);

        boolean inFavorites = viewModel.getMode() == RecipeListViewModel.FAVORITE_RECIPIES_MODE;
        menu.findItem(R.id.add_to_favorites).setVisible(visible && !inFavorites);
        menu.findItem(R.id.remove_from_favorites).setVisible(visible && inFavorites);
    }

    private void hideMenu(Menu menu){
        menu.findItem(R.id.add_recipe).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.add_to_shopping_list).setVisible(false);
        menu.findItem(R.id.add_to_favorites).setVisible(false);
        menu.findItem(R.id.remove_from_favorites).setVisible(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_recipe:
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.addEditRecipeFragment);
                break;
            case R.id.delete:
                deleteRecipes();
                break;
            case R.id.add_to_favorites:
                addToFavorites();
                break;
            case R.id.remove_from_favorites:
                removeFromFavorites();
                break;
            case R.id.add_to_shopping_list:
                addToShoppingList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_ok:
                Log.d(TAG, "onClick: button ok pressed");
                addToShoppingList();
                break;
        }
    }
}
