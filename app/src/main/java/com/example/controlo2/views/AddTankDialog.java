package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.controlo2.R;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddTankDialog extends AppCompatDialogFragment {

    private EditText editText;
    private AddTankDialogListener listener;
    private TextInputLayout textInputLayout;


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.add_tank_dialog, null);

        editText = view.findViewById(R.id.edittext_add);
        textInputLayout = view.findViewById(R.id.textinput_error_addtank);


        builder.setView(view)
                .setTitle("Agregar Cilindro")
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                boolean wantToCloseDialog = false;

                if (editText.getText().toString().trim().equals("")) {
                    textInputLayout.setError(getString(R.string.error_ingresar_valor));
                } else {
                    String number = (editText.getText().toString());
                    listener.addTank(Integer.parseInt(number));
                    wantToCloseDialog = true;
                }
                if (wantToCloseDialog)
                    dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            });
        }

    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddTankDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface AddTankDialogListener {
        void addTank(int tank);
    }
}


