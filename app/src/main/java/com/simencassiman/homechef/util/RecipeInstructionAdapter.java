package com.simencassiman.homechef.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simencassiman.homechef.R;

import java.util.ArrayList;
import java.util.List;

public class RecipeInstructionAdapter extends RecyclerView.Adapter<RecipeInstructionAdapter.InstructionHolder> {

    private static final String TAG = "RecipeInstructionAdapte";

    public static final int VIEW = 0;
    public static final int EDIT = 1;

    private int mode = -1;
    List<String> instructions = new ArrayList<>();
    OnTextChange listener;

    public interface OnTextChange {
        void onRemove(int position);
    }

    public void setOnTextChangeListener(RecipeInstructionAdapter.OnTextChange listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public InstructionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(!inValidMode())
            throw new IllegalStateException("Instructions adaptor is not in a valid mode");

        View v;
        if(mode == VIEW)
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe_instruction, parent, false);
        else
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recipe_instruction_edit, parent, false);


        return new InstructionHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionHolder holder, int position) {
        holder.bindTo(instructions.get(position), position);
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    public void setMode(int mode) throws IllegalArgumentException{
        if(!isValidMode(mode))
            throw new IllegalArgumentException("Invalid mode for Instructions adapter");
        this.mode = mode;
    }

    public void setInstructions(List<String> inst){
        this.instructions = inst;
        if(mode == EDIT &&
            (instructions.size() == 0 || !instructions.get(instructions.size()-1).isEmpty())) {
            addEmptyInstruction();
            Log.d(TAG, "setInstructions: adding empty ingredient");
        }

        notifyDataSetChanged();
        Log.d(TAG, "setInstructions: length of instructions is now " + instructions.size());
    }

    public void addEmptyInstruction(){
        if(mode == EDIT)
            instructions.add("");
    }

    public static boolean isValidMode(int mode){
        return mode == VIEW || mode == EDIT;
    }

    public boolean inValidMode(){
        return isValidMode(mode);
    }

    class InstructionHolder extends RecyclerView.ViewHolder{

        private TextView number;
        private EditText text;
        private TextView instruction;

        public InstructionHolder(@NonNull View v){
            super(v);

            number = v.findViewById(R.id.tv_instruction_nr);
            if (mode == VIEW)
                instruction = v.findViewById(R.id.tv_instruction_text);
            else if(mode == EDIT)
                text = v.findViewById(R.id.et_instruction_text);
        }

        public void bindTo(String instruction, int position) throws IllegalStateException{
            if(!inValidMode())
                throw new IllegalStateException("Adaptor is neither in VIEW nor EDIT mode");

            number.setText(String.valueOf(position + 1)); // List indices start at 0, but instr at 1
            if(mode == VIEW)
                this.instruction.setText(instruction);
            else {
                text.setText(instruction);
                text.addTextChangedListener(new TextWatcher() {
                    boolean wasEmpty = false;

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        wasEmpty = charSequence.toString().trim().isEmpty();
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (wasEmpty && !charSequence.toString().trim().isEmpty() &&
                            position == instructions.size()-1) {
//                            addEmptyInstruction();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String inst = editable.toString().trim();
                        instructions.set(position, editable.toString());
                        if (inst.isEmpty() && instructions.size() > 1){
                            if(position == instructions.size()-1){
                                if(instructions.get(position-1).isEmpty()) {
                                    instructions.remove(position);
                                    listener.onRemove(position);
                                }
                            }else{
                                instructions.remove(position);
                                listener.onRemove(position);
                            }
                        }
                    }
                });
            }
        }
    }
}
