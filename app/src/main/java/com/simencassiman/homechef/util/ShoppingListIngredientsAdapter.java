package com.simencassiman.homechef.util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListIngredientsAdapter extends RecyclerView.Adapter<ShoppingListIngredientsAdapter.IngredientViewHolder> {

    private static final String TAG = "ShoppingListIngAdap";
    private List<Ingredient> ingredients = new ArrayList<>();

    private ShoppingListIngredientsAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ShoppingListIngredientsAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list_ingredient, parent, false);

        return new IngredientViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        holder.bindTo(ingredients.get(position), position);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void setIngredients(List<Ingredient> ingredients){
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder{

        private TextView name, value, unit;

        public IngredientViewHolder(@NonNull View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(view -> {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
//                if(mListener != null) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION)
//                        mListener.onItemClick(position);
//                }
            });
            name = v.findViewById(R.id.tv_ingredient);
            value = v.findViewById(R.id.tv_value);
            unit = v.findViewById(R.id.tv_unit);
        }

        public void bindTo(Ingredient ingredient, int position) {
            name.setText(StringUtil.capitalize(ingredient.getName()));
            value.setText(ingredient.getAmount().getValue().toString());
            unit.setText(ingredient.getAmount().getUnit().getSymbol());
        }
    }
}
