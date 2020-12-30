package com.simencassiman.homechef.ui.fragments;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;
import com.simencassiman.homechef.R;
import com.simencassiman.homechef.db.NumberFactory;
import com.simencassiman.homechef.model.Unit;
import com.simencassiman.homechef.viewmodels.ConverterViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

public class ConverterFragment extends DaggerFragment implements View.OnClickListener {

    private static final String TAG = "ConverterFragment";

    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private TextInputLayout input;
    private TextView output;

    // Spinner
    private ArrayAdapter<String> spinnerFromAdapter;
    private ArrayAdapter<String> spinnerToAdapter;
    private AdapterView.OnItemSelectedListener spinnerFromListener;
    private AdapterView.OnItemSelectedListener spinnerToListener;

    private ConverterViewModel viewModel;
    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreate: started");

        viewModel = new ViewModelProvider(this, providerFactory).get(ConverterViewModel.class);
        initializeSpinners();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        spinnerFrom = view.findViewById(R.id.spinner_from);
        spinnerTo = view.findViewById(R.id.spinner_to);
        input = view.findViewById(R.id.input_value_from);
        output = view.findViewById(R.id.output_value_to);

        view.findViewById(R.id.converter_icon).setOnClickListener(this);
        input.getEditText().addTextChangedListener(inputWatcher);

        return view;
    }

    private void initializeSpinners(){
        spinnerFromAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, viewModel.getAllUnits());
        spinnerToAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, viewModel.getAllUnits());
        spinnerFromListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setFromUnit(Unit.values()[i]);
                if(!viewModel.isSwapping())
                    setSpinnerTo();
                else
                    viewModel.setSwapping(false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        spinnerToListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setToUnit(viewModel.getFromUnit().getCompatibleUnits().get(i));
                convert();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        //Spinner from
        spinnerFrom.setAdapter(spinnerFromAdapter);
        spinnerFrom.setOnItemSelectedListener(spinnerFromListener);

        //Spinner to
        spinnerTo.setAdapter(spinnerToAdapter);
        spinnerTo.setOnItemSelectedListener(spinnerToListener);
    }

    private void setSpinnerTo(){
        spinnerToAdapter.clear();
        String[] units;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            units = viewModel.getFromUnit().getCompatibleUnits().stream().map(Unit::getSymbol).toArray(String[]::new);
        }else{
            List<Unit> temp = viewModel.getFromUnit().getCompatibleUnits();
            units = new String[temp.size()];
            for(int i = 0; i< units.length; i++) units[i] = temp.get(i).toString();
        }
        spinnerToAdapter.addAll(units);
        spinnerTo.setAdapter(spinnerToAdapter);
    }

    private void swapUnits(){
        viewModel.setSwapping(true);
        Unit fromTemp = viewModel.getFromUnit();
        Unit toTemp = viewModel.getToUnit();
        spinnerFrom.setSelection(viewModel.getAllUnits().indexOf(viewModel.getToUnit().getSymbol()));
        int pos = toTemp.getCompatibleUnits().indexOf(fromTemp);
        spinnerTo.setSelection(toTemp.getCompatibleUnits().indexOf(fromTemp));
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String value = editable.toString().trim();
            if(!value.isEmpty()){
                // Maybe some checks for format... //TODO
                viewModel.setFromValue(NumberFactory.getNumber(value));
                convert();
            }
        }
    };

    private void convert(){
        viewModel.convert();
        if (viewModel.getToValue() != null)
            output.setText(viewModel.getToValue().toString());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.converter_icon:
                swapUnits();
                break;
        }
    }
}
