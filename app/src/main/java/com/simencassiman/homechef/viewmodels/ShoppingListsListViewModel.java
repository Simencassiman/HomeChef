package com.simencassiman.homechef.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.simencassiman.homechef.db.Repository;
import com.simencassiman.homechef.model.ShoppingList;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class ShoppingListsListViewModel extends ViewModel {

    private LiveData<List<ShoppingList>> shoppingLists;

    private Repository repository;

    @Inject
    public ShoppingListsListViewModel(Repository repo) {
        this.repository = repo;
        loadShoppingLists();
    }

    private void loadShoppingLists(){
        shoppingLists = repository.getShoppingLists();
    }

    public LiveData<List<ShoppingList>> getShoppingLists() {
        return shoppingLists;
    }

    private ShoppingList[] getSelectedItems(@NotNull List<Integer> indices){
        ShoppingList[] items = new ShoppingList[indices.size()];
        int i = 0;
        for(Integer ind: indices){
            items[i++] = getShoppingLists().getValue().get(ind);
        }
        return items;
    }

    public void deleteShoppingList(List<Integer> indices){
        repository.deleteShoppingList(getSelectedItems(indices));
    }

    public void addToShoppingList(List<Integer> indices){

    }
}
