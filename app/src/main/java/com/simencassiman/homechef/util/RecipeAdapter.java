package com.simencassiman.homechef.util;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Recipe;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends PagedListAdapter<Recipe, RecipeAdapter.RecipeViewHolder> {

    private static final String TAG = "RecipeAdapter";

    private OnItemClickListener mListener;
    private boolean inSelectionMode;
    private SparseBooleanArray selectedItems;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public RecipeAdapter() {
        super(DIFF_CALLBACK);
        inSelectionMode = false;
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_list, parent, false);

        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = getItem(position);
        //Recipe can be null if it's a placeholder
        if(recipe != null){
            holder.bindTo(recipe, position, inSelectionMode);

        }
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

    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private View card;
        private TextView title;
        private ImageView image, veganIcon, veggieIcon;

        public RecipeViewHolder(View v) {
            super(v);
            card = v;
            title = v.findViewById(R.id.title);
            image = v.findViewById(R.id.image);
            veganIcon = v.findViewById(R.id.icon_vegan);
            veggieIcon = v.findViewById(R.id.icon_veggie);
        }

        public void bindTo(Recipe recipe, int position, boolean inSelectionMode) {
            title.setText(StringUtil.capitalizeWords(recipe.getTitle()));
            setBackground(position);

            // Define click listener for the ViewHolder's View.
            card.setOnClickListener(view -> {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                if(mListener != null && position != RecyclerView.NO_POSITION){
                    if(!inSelectionMode)
                        mListener.onItemClick(position);
                    else {
                        toggleSelected(position);
                        Log.d(TAG, "bindTo: recipe id is "+recipe.getId());
                    }
                }
            });
            card.setOnLongClickListener(view -> {
                if(mListener != null && position != RecyclerView.NO_POSITION) {
                    mListener.onItemLongClick(position);
                    toggleSelected(position);
                }
                return true;
            });

            // Icons
            Log.d(TAG, "bindTo: recipe "+recipe.getTitle()+" is veggie: "+
                    (recipe.isVegetarian() || recipe.isVegan())+
                    ", this gives integer "+
                    ((recipe.isVegetarian() || recipe.isVegan())? View.VISIBLE:View.GONE)+
                    " (visible is "+View.VISIBLE+")");
            veggieIcon.setVisibility((recipe.isVegetarian() || recipe.isVegan())? View.VISIBLE:View.GONE);
            veganIcon.setVisibility(recipe.isVegan()? View.VISIBLE:View.GONE);
        }

        private void toggleSelected(int pos){
            toggleSelection(pos);
            setBackground(pos);
        }

        private void setBackground(int pos){
            if (isSelected(pos))
                card.findViewById(R.id.container)
                        .setBackgroundResource(R.color.selected_background);
            else
                card.findViewById(R.id.container)
                        .setBackgroundResource(R.color.default_background);
        }
    }

    private static DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recipe>() {

                @Override
                public boolean areItemsTheSame(@NotNull Recipe oldItem, @NotNull Recipe newItem) {
                    // The ID property identifies when items are the same.

                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NotNull Recipe oldItem, @NotNull Recipe newItem) {
                    // Don't use the "==" operator here. Either implement and use .equals(),
                    // or write custom data comparison logic here.
                    return oldItem.equals(newItem);
                }
            };
}
