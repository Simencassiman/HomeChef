package com.simencassiman.homechef.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.util.ShoppingListAdapter;
import com.simencassiman.homechef.viewmodels.ShoppingListsListViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ShoppingListsListFragment extends DaggerFragment {

    private static final String TAG = "ShoppingListsFragment";

    public static final boolean SELECTION_MODE_ON = true;
    public static final boolean SELECTION_MODE_OFF = false;

    private ShoppingListsListViewModel viewModel;
    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private LinearLayoutManager llManager;

    OnBackPressedCallback callback;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: started");

        viewModel = new ViewModelProvider(this, providerFactory).get(ShoppingListsListViewModel.class);

        viewModel.getShoppingLists().observe(this, adapter::setLists);

        callback = new OnBackPressedCallback(false /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                exitSelectionMode();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_list_shopping, container, false);

        buildRecyclerview(v);

        return v;
    }

    private void buildRecyclerview(View v){
        recyclerView = v.findViewById(R.id.recyclerView_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ShoppingListAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        adapter.setOnItemClickListener(new ShoppingListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                goToShoppingListFragment(position);
//                Toast.makeText(getActivity(), "Go to list", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position) {
                enterSelectionMode();
            }
        });
    }

    private void goToShoppingListFragment(int position){
        try {
            int listId = viewModel.getShoppingLists().getValue().get(position).getId();

            ShoppingListsListFragmentDirections.ToShoppingList action =
                    ShoppingListsListFragmentDirections.toShoppingList();
            action.setListId(listId);

            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(action);

        }catch (NullPointerException e){
            Log.d(TAG, "goToShoppingListFragment: NullPointerException on item select, " + e.getMessage());
            Toast.makeText(getActivity(), "NullPointer", Toast.LENGTH_SHORT).show();
        }
    }

    public void enterSelectionMode(){
        // Deactivate back button press (1st press)
        callback.setEnabled(true);

        // Update items (onClick events)
        adapter.setInSelectionMode();
        adapter.notifyDataSetChanged();

        // Activate options menu
        getActivity().invalidateOptionsMenu();
    }

    public void exitSelectionMode(){
        // Activate back button press (1st press)
        callback.setEnabled(false);

        // Update items (onClick events)
        adapter.deassertSelectionMode();
        adapter.notifyDataSetChanged();

        // Deactivate options menu
        getActivity().invalidateOptionsMenu();
    }

    private void deleteLists(){
        if(adapter.inSelectionMode())
            viewModel.deleteShoppingList(adapter.getSelectedItems());
        else
            Toast.makeText(getActivity(),"No recipes selected", Toast.LENGTH_SHORT).show();

        exitSelectionMode();
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_shopping_lists, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(adapter.inSelectionMode()) {
            setMenuItemsVisibility(menu, SELECTION_MODE_ON);
        }else{
            setMenuItemsVisibility(menu, SELECTION_MODE_OFF);
        }
    }

    private void setMenuItemsVisibility(Menu menu, boolean selection){
        menu.findItem(R.id.create).setVisible(!selection);
        menu.findItem(R.id.delete).setVisible(selection);
        menu.findItem(R.id.add_to_list).setVisible(selection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.create:
                // Go to create shopping list
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(ShoppingListsListFragmentDirections.toShoppingList());
                break;
            case R.id.delete:
                // Delete shopping list
                deleteLists();
                break;
            case R.id.add_to_list:
                // Include to another shopping list
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
