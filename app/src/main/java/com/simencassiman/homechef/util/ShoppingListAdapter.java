package com.simencassiman.homechef.util;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.ShoppingList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder> {

    private static final String TAG = "ShoppingListAdapter";
    private List<ShoppingList> lists = new ArrayList<>();

    private ShoppingListAdapter.OnItemClickListener mListener;
    private boolean inSelectionMode = false;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(ShoppingListAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.ShoppingListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);

        return new ShoppingListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListAdapter.ShoppingListViewHolder holder, int position) {
        holder.bindTo(lists.get(position), position);
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public void setLists(List<ShoppingList> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    public  boolean inSelectionMode(){ return inSelectionMode;}

    public void setInSelectionMode(){ this.inSelectionMode = true;}

    public void deassertSelectionMode(){
        this.inSelectionMode = false;
        clearSelections();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
    }

    public boolean isSelected(int pos){
        return selectedItems.get(pos, false);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    class ShoppingListViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView date;
        View card;

        ShoppingListViewHolder(@NotNull View v){
            super(v);
            card = v;
            title = v.findViewById(R.id.container_layout).findViewById(R.id.title);
            date = v.findViewById(R.id.date_number);
        }

        void bindTo(ShoppingList list, int position){
            title.setText(list.getName());
            if(list.getShoppingDate() != null)
                date.setText(list.getShoppingDate().toString());
            else
                date.setText("");

            setBackground(position);

//            card.setOnClickListener(view -> {mListener.onItemClick(position);});

            // Define click listener for the ViewHolder's View.
            card.setOnClickListener(view -> {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                if(mListener != null && position != RecyclerView.NO_POSITION){
                    if(!inSelectionMode)
                        mListener.onItemClick(position);
                    else
                        toggleSelected(position);
                }
            });

            card.setOnLongClickListener(view -> {
                if(mListener != null && position != RecyclerView.NO_POSITION) {
                    mListener.onItemLongClick(position);
                    toggleSelected(position);
                }
                return true;
            });
        }

        private void toggleSelected(int pos){
            toggleSelection(pos);
            setBackground(pos);
        }

        private void setBackground(int pos){
            if (isSelected(pos))
                card.findViewById(R.id.container_layout)
                        .setBackgroundResource(R.color.selected_background);
            else
                card.findViewById(R.id.container_layout)
                        .setBackgroundResource(R.color.default_background);
        }

    }

}
