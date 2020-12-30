package com.simencassiman.homechef.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.TypeConverters;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simencassiman.homechef.R;
import com.simencassiman.homechef.db.Converter;
import com.simencassiman.homechef.model.Ingredient;
import com.simencassiman.homechef.model.Recipe;
import com.simencassiman.homechef.model.Unit;
import com.simencassiman.homechef.ui.DialogCancel;
import com.simencassiman.homechef.util.RecipeInstructionAdapter;
import com.simencassiman.homechef.viewmodels.AddEditRecipeViewModel;
import com.simencassiman.homechef.viewmodels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

@TypeConverters(Converter.class)
public class AddEditRecipeFragment extends DaggerFragment implements View.OnClickListener, DialogCancel.OnInputListener {

    private static final String TAG = "AddEditRecipeFragment";
    private static final int SAVE = 1;
    public static final int PUBLISH = 2;

    private LinearLayout layout;

    private TextInputLayout titleInput;
    private ArrayList<LinearLayout> ingredientItems;
    private EditText descriptionEditText;
    private TextInputEditText tagsEditText;
    private ChipGroup tagGroup;
    private CheckBox vegetarian;
    private CheckBox vegan;
    private RecyclerView instructions;
    private RecipeInstructionAdapter instructionsAdapter;

    // Spinner
    private ArrayAdapter<String> spinnerAdapter;
    private AdapterView.OnItemSelectedListener spinnerListener;

    private AddEditRecipeViewModel viewModel;
    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onCreate: started");

        viewModel = new ViewModelProvider(this, providerFactory).get(AddEditRecipeViewModel.class);
        viewModel.setLastIngredientPosition(initialLastIngredientPosition());

        int id = AddEditRecipeFragmentArgs.fromBundle(getArguments()).getRecipeId();

        loadEditionMode(id, view);

        // Add tags watcher
        tagsEditText.addTextChangedListener(tagsWatcher);

        // Set people counter
        updatePeopleCounter();
        instructionsAdapter.setInstructions(viewModel.getInstructions());

        // This callback will only be called when fragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                Log.d(TAG, "handleOnBackPressed: "+ "going to cancel?");
                DialogCancel dialog = new DialogCancel();
                dialog.show(getActivity().getSupportFragmentManager(), "NoticeDialogFragment");
//                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
//                        .navigate(AddEditRecipeFragmentDirections.actionAddEditToCancelDialog());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        setHasOptionsMenu(true);

    }

    private void loadEditionMode(int id, View view){
        if(id == -1){
            viewModel.setRequest(AddEditRecipeViewModel.ADD);
            for(int i = 0; i < 3; i++) addIngredientField();
        } else{
            viewModel.setRequest(AddEditRecipeViewModel.EDIT);
            loadRecipe(id);
            view.findViewById(R.id.bt_delete_recipe).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_edit_recipe, container, false);

        bindLayout(view);
        setClickListeners();

        ingredientItems = new ArrayList<>();

        initializeSpinnerItems();
        bindInstructions();

        return view;
    }

    private void bindLayout(View view){
        layout = view.findViewById(R.id.container);
        titleInput = layout.findViewById(R.id.text_input_title);
        descriptionEditText = layout.findViewById(R.id.et_recipe_description);
        tagsEditText = layout.findViewById(R.id.input_tags);
        tagGroup = layout.findViewById(R.id.chip_group_tags);
        vegetarian = layout.findViewById(R.id.checkbox_veggie);
        vegan = layout.findViewById(R.id.checkbox_vegan);
        instructions = layout.findViewById(R.id.rv_instructions);
    }

    private void setClickListeners(){
        layout.findViewById(R.id.bt_people_counter_minus).setOnClickListener(this);
        layout.findViewById(R.id.bt_people_counter_plus).setOnClickListener(this);
        layout.findViewById(R.id.bt_delete_recipe).setOnClickListener(this);
        vegetarian.setOnClickListener(this);
        vegan.setOnClickListener(this);
    }

    private int initialLastIngredientPosition() {
        return layout.indexOfChild(layout.findViewById(R.id.cl_people_counter));
    }

    private void initializeSpinnerItems(){
        List<String> items = new ArrayList<>();
        for(Unit u: Unit.values()) items.add(u.getSymbol());
        spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);

        spinnerListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int id;
                if(adapterView.getTag() == null){
                    id = viewModel.getValidIdTag(adapterView.getId());
                    adapterView.setTag(id);
                }else{
                    id = (int) adapterView.getTag();
                }

                viewModel.addSpinnerItem(id, Unit.values()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
    }

    private void setItems(LinearLayout ll){
        setEditText(((TextInputLayout)ll.findViewById(R.id.input_ingredient_name)).getEditText());
        setSpinner(ll.findViewById(R.id.spinner_unit));
    }

    private void setEditText(EditText et){
        et.addTextChangedListener(ingredientsWatcher);
    }

    private void setSpinner(Spinner spinner){
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerListener);
    }

    private void bindInstructions(){
        instructions.setLayoutManager(new LinearLayoutManager(getActivity()));
        instructionsAdapter = new RecipeInstructionAdapter();

        instructionsAdapter.setMode(RecipeInstructionAdapter.EDIT);
        instructions.setAdapter(instructionsAdapter);
        instructions.setHasFixedSize(true);
        instructionsAdapter.setOnTextChangeListener(position -> {
            new Runnable() {
                public void run() {
                    instructions.removeViewAt(position);
                    instructionsAdapter.notifyItemRemoved(position);
                    instructionsAdapter.notifyItemRangeChanged(position, instructionsAdapter.getItemCount());
                }
            };
        });
    }

    private TextWatcher ingredientsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            boolean allFieldsFilled = areAllFieldsFilled();
            if (allFieldsFilled) addIngredientField();
            //else removeEmptyIngredientItems();
        }
    };

    private boolean areAllFieldsFilled(){
        for(LinearLayout ll: ingredientItems)
            if(((TextInputLayout)ll.findViewById(R.id.input_ingredient_name)).getEditText()
                .getText().toString().trim().isEmpty())
                    return false;
        return true;
    }

    private TextWatcher tagsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.toString().endsWith(","))
                createTag(charSequence.subSequence(0,charSequence.length()-1).toString().trim().toLowerCase());
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void createTag(String tag){
        if(tag.isEmpty()) return;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        Chip chip = (Chip) inflater.inflate(R.layout.item_chip_entry,null,false);
        chip.setText(tag);
        chip.setOnCloseIconClickListener(view -> {
            tagGroup.removeView(view);
            viewModel.removeTag(((Chip)view).getText().toString());
        });
        tagGroup.addView(chip);
        tagsEditText.setText("");
        viewModel.addTag(tag);
    }

    private void updatePeopleCounter(){
        ((TextView)layout.findViewById(R.id.tv_people_counter)).setText(
                String.valueOf(viewModel.getPeopleCounter()));
    }

    private void addIngredientField(){
        //Inflate layout to include
        View ingredient = getLayoutInflater()
                .inflate(R.layout.item_add_ingredient, layout, false);

        // Add editText watcher and spinner adapter
        setItems((LinearLayout) ingredient);

        //Add the inflated View to the layout
        layout.addView(ingredient, viewModel.getLastIngredientPosition()+1);

        //Update the last position
        viewModel.incrementLastIngredientPosition();
        //Add layout to array
        ingredientItems.add((LinearLayout) ingredient);

    }

    private void loadRecipe(int id){
        viewModel.loadRecipe(id);
        titleInput.getEditText().setText(viewModel.getOldRecipe().getTitle());
        titleInput.setHint("");
        ((EditText) layout.findViewById(R.id.et_recipe_description))
                .setText(viewModel.getOldRecipe().getDescription());
        for(int i = 0; i < viewModel.getOldRecipe().getIngredients().size(); i++) {
            addIngredientField();
            loadIngredient(i);
        }
        attachTags();
        vegetarian.setChecked(viewModel.getOldRecipe().isVegetarian());
        vegan.setChecked(viewModel.getOldRecipe().isVegan());
    }

    private void loadIngredient(int position) {
        LinearLayout ingredient_layout = ingredientItems.get(position);
        Ingredient ingredient = viewModel.getOldRecipe().getIngredients().get(position);
        ((TextInputLayout) ingredient_layout.findViewById(R.id.input_ingredient_name)).getEditText()
                .setText(ingredient.getName());
        ((TextInputLayout) ingredient_layout.findViewById(R.id.input_ingredient_amount)).getEditText()
                .setText(String.valueOf(ingredient.getAmount().getValue()));
        ((Spinner) ingredient_layout.findViewById(R.id.spinner_unit)).setSelection(
                Arrays.asList(Unit.values()).indexOf(ingredient.getAmount().getUnit()));
    }

    private void attachTags(){
        for(String tag: viewModel.getOldRecipe().getTags()){
            createTag(tag);
        }
    }

    private void saveRecipe(int action){
        if(!allFieldsCorrectlyFilled())
            return;

        String recipeTitle = titleInput.getEditText().getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String tags = tagsEditText.getText().toString().trim();

        Recipe.EditState state = null;
        if(action == SAVE)  state = Recipe.EditState.DRAFT;
        else if(action == PUBLISH) state = Recipe.EditState.PUBLISHED;

        // Do checks
        //TODO

        // Generate ingredients
        ArrayList<Ingredient> ingredients = generateIngredients();

        // Clean instructions
        List<String> cleanInstructions = new ArrayList<>();
        for(String s: viewModel.getInstructions())
            if(!s.trim().isEmpty()) cleanInstructions.add(s);

        // Build recipe
        viewModel.setNewRecipe(Recipe.builder()
                .setId(recipeTitle.hashCode())
                .setTitle(recipeTitle.toLowerCase())
                .setPeopleCounter(viewModel.getPeopleCounter())
                .setDescription(description)
                .setIngredients(ingredients)
                .setTags(viewModel.getTags())
                .setVegetarian(viewModel.isVegetarian())
                .setVegan(viewModel.isVegan())
                .setState(state)
                .setSteps(cleanInstructions)
                .build());

        int code = viewModel.saveNewRecipe();
        if(code == 0)
            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Couldn't save", Toast.LENGTH_SHORT).show();
        navigateToRecipe();
    }

    private boolean allFieldsCorrectlyFilled(){
        if(!isValidTitle() | !areValidIngredients() | !isValidDescription() | !areValidTags())
            return false;
        return true;
    }

    private boolean isValidTitle(){
        if(titleInput.getEditText().getText().toString().trim().isEmpty()){
            titleInput.setError("Title field can't be empty");
            return false;
        }else{
            titleInput.setError(null);
            return true;
        }
    }

    private boolean areValidIngredients() throws IllegalStateException{
        if(ingredientItems.size() == 0)
            throw new IllegalStateException("There are no ingredient input items.");
        boolean isValid = true;
        boolean hasOneIngredient = false;
        for(LinearLayout ingredient: ingredientItems){
            // Get input
            int id = (int) ingredient.findViewById(R.id.spinner_unit).getTag();
            Unit unit = viewModel.getSpinnerItem(id) != null ?
                    viewModel.getSpinnerItem(id) : Unit.UNKNOWN;
            String ingredientName = ((TextInputLayout)ingredient.findViewById(R.id.input_ingredient_name))
                    .getEditText().getText().toString().trim();
            String ingredientAmount = ((TextInputLayout)ingredient.findViewById(R.id.input_ingredient_amount))
                    .getEditText().getText().toString().trim();

            // Check input
            if(ingredientName.isEmpty()){ // If name is empty
                if(!ingredientAmount.isEmpty()) { // But has an amount
                    ((TextInputLayout) ingredient.findViewById(R.id.input_ingredient_name))
                            .setError("Ingredient name can't be empty");
                    isValid = false;
                }
            }else if(!Ingredient.isValidIngredient(ingredientName, ingredientAmount, unit)){
                // If amount is not decent (empty name has already been checked)
                ((TextInputLayout)ingredient.findViewById(R.id.input_ingredient_amount))
                        .setError("Invalid amount");
                isValid = false;
            }else{
                ((TextInputLayout)ingredient.findViewById(R.id.input_ingredient_name)).setError(null);
                ((TextInputLayout)ingredient.findViewById(R.id.input_ingredient_amount)).setError(null);
                // Don't return, go through all ingredients first
                hasOneIngredient = true;
            }
        }
        if(!hasOneIngredient){
            ((TextInputLayout)ingredientItems.get(0).findViewById(R.id.input_ingredient_name))
                    .getEditText().setError("Recipe should have at least one ingredient");
            isValid = false;
        }
        return isValid;
    }

    private boolean isValidDescription(){
        return true;
    }

    private boolean areValidTags(){
        return true;
    }

    private void navigateToRecipe(){
        if(viewModel.getRequest() == AddEditRecipeViewModel.ADD){
            AddEditRecipeFragmentDirections.ViewNewRecipeAction action =
                    AddEditRecipeFragmentDirections.viewNewRecipeAction();
            action.setRecipeId(viewModel.getNewRecipe().getId());
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(action);
        }else if(viewModel.getRequest() == AddEditRecipeViewModel.EDIT){
            AddEditRecipeFragmentDirections.ViewEditedRecipeAction action =
                    AddEditRecipeFragmentDirections.viewEditedRecipeAction();
            action.setRecipeId(viewModel.getNewRecipe().getId());
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                    .navigate(action);
        }
    }

    private ArrayList<Ingredient> generateIngredients(){
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        for(LinearLayout l: ingredientItems){
            String ingredient = ((TextInputLayout)l.findViewById(R.id.input_ingredient_name)).getEditText()
                    .getText().toString().trim();
            if(!ingredient.isEmpty()) {
                int id = (int) l.findViewById(R.id.spinner_unit).getTag();

                Unit unit = viewModel.getSpinnerItem(id) != null ?
                        viewModel.getSpinnerItem(id) : Unit.UNKNOWN;
                ingredients.add(Ingredient.builder()
                        .setValue(((TextInputLayout) l.findViewById(R.id.input_ingredient_amount)).getEditText()
                                .getText().toString().trim())
                        .setName(ingredient.toLowerCase())
                        .setUnit(unit)
                        .build());
            }
        }

        return ingredients;
    }

    private void setVegetarianState(boolean state){
        if(state && !viewModel.containsTag(getResources().getString(R.string.vegetarian_lower)))
                createTag(getResources().getString(R.string.vegetarian_lower));
        else if(!state){
            vegan.setChecked(false);
            if(viewModel.containsTag(getResources().getString(R.string.vegetarian_lower)) ||
                    viewModel.containsTag(getResources().getString(R.string.vegan_lower))){
                List<Chip> toDelete = new ArrayList<>();
                for(int i = 0; i < tagGroup.getChildCount(); i++){
                    Chip chip = (Chip) tagGroup.getChildAt(i);
                    if(chip.getText().toString().equals(getResources().getString(R.string.vegetarian_lower)) ||
                            chip.getText().toString().equals(getResources().getString(R.string.vegan_lower)))
                        toDelete.add(chip);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    toDelete.forEach(Chip::performCloseIconClick);
                }else{
                    for(Chip chip: toDelete) chip.performCloseIconClick();
                }
            }
        }
        viewModel.setVegetarian(state);
    }

    private void setVeganState(boolean state){
        if(state){
            vegetarian.setChecked(true);
            if(!viewModel.containsTag(getResources().getString(R.string.vegan_lower)))
                createTag(getResources().getString(R.string.vegan_lower));
            if(!viewModel.containsTag(getResources().getString(R.string.vegetarian_lower)))
                createTag(getResources().getString(R.string.vegetarian_lower));
        } else{
            if(viewModel.containsTag(getResources().getString(R.string.vegan_lower))){
                List<Chip> toDelete = new ArrayList<>();
                for(int i = 0; i < tagGroup.getChildCount(); i++){
                    Chip chip = (Chip) tagGroup.getChildAt(i);
                    if(chip.getText().toString().equals(getResources().getString(R.string.vegan_lower)))
                        toDelete.add(chip);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    toDelete.forEach(Chip::performCloseIconClick);
                }else{
                    for(Chip chip: toDelete) chip.performCloseIconClick();
                }
            }
        }
        viewModel.setVegan(state);
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.bt_people_counter_minus:
                    viewModel.decreasePeopleCounter();
                    updatePeopleCounter();
                    break;
                case R.id.bt_people_counter_plus:
                    viewModel.incrementPeopleCounter();
                    updatePeopleCounter();
                    break;
                case R.id.bt_delete_recipe:
                    viewModel.deleteRecipe();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                                    .popBackStack(R.id.recipeFragment, true);
                    break;
                case R.id.checkbox_veggie:
                    setVegetarianState(((CheckBox)view).isChecked());
                    Log.d(TAG, "onClick: checkbox veggie: "+viewModel.isVegetarian());
                    break;
                case R.id.checkbox_vegan:
                    setVeganState(((CheckBox)view).isChecked());
                    Log.d(TAG, "onClick: checkbox vegan: "+viewModel.isVegan());
                    break;
            }
        } catch (IllegalStateException e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_add_recipe, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.save:
                saveRecipe(SAVE);
                break;
            case R.id.publish:
                saveRecipe(PUBLISH);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendInput(int decision) {
        Log.d(TAG, "sendInput: got decision!");
    }
}
