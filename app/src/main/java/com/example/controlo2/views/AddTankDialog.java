package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.controlo2.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddTankDialog extends AppCompatDialogFragment {

    private EditText editText;
    private ExampleDialogListener listener;


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.add_tank_dialog, null);

        builder.setView(view)
                .setTitle("Agregar Tanque")
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {

                })
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                    String number = (editText.getText().toString());
                    listener.applyTexts(Integer.parseInt(number));
                });

        editText = view.findViewById(R.id.edittext_add);

        return builder.create();
    }


    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(int tank);
    }
}


