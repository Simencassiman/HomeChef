package com.simencassiman.homechef.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.db.RecipeCollector;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListRecipesAdapter extends RecyclerView.Adapter<ShoppingListRecipesAdapter.RecipeViewHolder> {

    private static final String TAG = "ShoppingListRecipesAdap";
    private List<RecipeCollector> recipes = new ArrayList<>();

    private ShoppingListRecipesAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onTitleClick(int position);
        void onButtonPlus(int position);
        void onButtonMin(int position);
    }

    public void setOnItemClickListener(ShoppingListRecipesAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list_recipe, parent, false);

        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        holder.bindTo(recipes.get(position), position);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setRecipes(List<RecipeCollector> recipes){
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout card;
        private TextView title;
        private TextView peopleCount;
        private ConstraintLayout buttonPlus;
        private ConstraintLayout buttonMin;

        RecipeViewHolder(@NonNull View v) {
            super(v);

            card = v.findViewById(R.id.container);
            buttonPlus = v.findViewById(R.id.bt_people_counter_plus);
            buttonMin = v.findViewById(R.id.bt_people_counter_minus);
            title = v.findViewById(R.id.title);
            peopleCount = v.findViewById(R.id.tv_people_counter);
        }

        void bindTo(RecipeCollector recipe, int position) {
            title.setText(StringUtil.capitalizeWords(recipe.getTitle()));
            peopleCount.setText(String.valueOf(recipe.getPeopleCount()));
            setBackground(recipe);

            // Define click listener for the ViewHolder's View.
            buttonPlus.setOnClickListener(view -> mListener.onButtonPlus(position));
            buttonMin.setOnClickListener(view -> mListener.onButtonMin(position));

//            v.setOnClickListener(view -> {
//                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
//                if(mListener != null) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION)
//                        mListener.onItemClick(position);
//                }
//            });
        }

        private void setBackground(RecipeCollector recipe){
            if (recipe.collected)
                card.setBackgroundResource(R.color.selected_background);
            else
                card.setBackgroundResource(R.color.default_background);
        }
    }
}
