package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


import com.example.controlo2.R;
import com.example.controlo2.utils.InputFilterMinMax;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UpdatePsiDialog extends AppCompatDialogFragment {

    private EditText editText;
    private TextInputLayout textInputLayout;
    private UpdatePsiDialogListener listener;


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.update_psi_dialog, null, false);

        builder.setView(view)
                .setTitle("Actualizar valor")
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {

                })
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                });

        editText = view.findViewById(R.id.edittext_update_psi);
        textInputLayout = view.findViewById(R.id.textinput_error_psi);
        editText.setFilters(new InputFilter[]{new InputFilterMinMax("1", "200")});
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                boolean wantToCloseDialog = false;

                if (editText.getText().toString().trim().equals("")) {
                    textInputLayout.setError(getString(R.string.error_ingresar_valor));
                } else {
                    String number = (editText.getText().toString());
                    listener.updatePsi(Integer.parseInt(number));
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
            listener = (UpdatePsiDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface UpdatePsiDialogListener {
        void updatePsi(int tank);
    }
}