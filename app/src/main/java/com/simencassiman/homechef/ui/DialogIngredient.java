package com.simencassiman.homechef.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.simencassiman.homechef.R;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Rational;
import com.simencassiman.homechef.model.Unit;
import com.simencassiman.homechef.viewmodels.AddIngredientViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerDialogFragment;

public class DialogIngredient extends DaggerDialogFragment implements View.OnClickListener{

    private static final String TAG = "DialogIngredient";

    private AddIngredientViewModel viewModel;
    private TextInputLayout name;
    private TextInputLayout amount;

    // Spinner
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private AdapterView.OnItemSelectedListener spinnerListener;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: dialog created");
        
        if(providerFactory == null)
            Log.d(TAG, "onViewCreated: providerFactory is null");

        viewModel = new ViewModelProvider(this, providerFactory).get(AddIngredientViewModel.class);
        setSpinner();
        int listId = DialogIngredientArgs.fromBundle(getArguments()).getListId();
        if(listId < 0)
            Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigateUp();
        viewModel.setListId(listId);

        Log.d(TAG, "onViewCreated: "+ Rational.gcd(5,7));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_ingredient,container, false);
        name = v.findViewById(R.id.input_ingredient_name);
        amount = v.findViewById(R.id.input_ingredient_amount);
        spinner = v.findViewById(R.id.spinner_unit);

        v.findViewById(R.id.button_cancel).setOnClickListener(this);
        v.findViewById(R.id.button_ok).setOnClickListener(this);

        name.getEditText().addTextChangedListener(nameWatcher);
        amount.getEditText().addTextChangedListener(amountWatcher);

        initializeSpinnerItems();

        return v;
    }

    private TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // save name
            viewModel.setName(editable.toString().trim().toLowerCase());
        }
    };

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // save amount
            viewModel.setAmount(editable.toString().trim().toLowerCase());
        }
    };

    private void initializeSpinnerItems(){
        List<String> items = new ArrayList<>();
        for(Unit u: Unit.values()) items.add(u.getSymbol());
        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);

        spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setSelectedUnit(Unit.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setSpinner(){
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerListener);
    }

    private void saveIngredient(){
        if(isValidIngredient()){
            // Save stuff to db
            viewModel.saveIngredient();
            back();
        }
    }

    private boolean isValidIngredient(){
        // Check input
        if(viewModel.getName().isEmpty()){ // If name is empty
           name.setError("Ingredient name can't be empty");
           return false;
        }else if(!Ingredient.isValidIngredient(viewModel.getName(), viewModel.getAmount(), viewModel.getUnit())){
            // If amount is not decent (empty name has already been checked)
            amount.setError("Invalid amount");
            return false;
        }else{
            name.setError(null);
            name.setError(null);
        }
        return true;
    }

    private void back(){
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigateUp();
    }

    @Override
    public void onClick(View view) {
        switch ((view.getId())){
            case R.id.button_ok:
                saveIngredient();
                break;
            case R.id.button_cancel:
                back();
                break;
        }
    }
}
