package com.simencassiman.homechef.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.simencassiman.homechef.R;

public class DialogCancel extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "DialogCancel";

    public interface OnInputListener{
        int NO = 0;
        int YES = 1;
        void sendInput(int decision);
    }

    public OnInputListener onInputListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cancel,container, false);

        view.findViewById(R.id.dialog_cancel).findViewById(R.id.button_yes).setOnClickListener(this);
        view.findViewById(R.id.dialog_cancel).findViewById(R.id.button_no).setOnClickListener(this);

        Log.d(TAG, "onCreateView: Dialog inflated");
        return view;
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.button_yes:
                    onInputListener.sendInput(OnInputListener.YES);
                    break;
                case R.id.button_no:
                    onInputListener.sendInput(OnInputListener.NO);
                    break;
            }
        } catch (IllegalStateException e){
            Log.d(TAG, "onClick: IllegalStateException: " + e.getMessage());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onInputListener = (OnInputListener) context;
        }catch (ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
