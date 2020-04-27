package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.controlo2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEmailDialog extends AppCompatDialogFragment {

    private AddMailDialogListener listener;

    @BindView(R.id.edittext_name)
    TextInputEditText edittextName;
    @BindView(R.id.textinput_error_name)
    TextInputLayout textinputErrorName;
    @BindView(R.id.edittext_mail)
    TextInputEditText edittextMail;
    @BindView(R.id.textinput_error_mail)
    TextInputLayout textinputErrorMail;





    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.add_mail_dialog, null);

        ButterKnife.bind(this,view);


        builder.setView(view)
                .setTitle("Ingresar datos del proveedor")
                .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                })
                .setPositiveButton("Guardar", (dialogInterface, i) -> {
                });

        edittextMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        return builder.create();
    }


    private boolean validateEmail() {
        String emailInput = textinputErrorMail.getEditText().getText().toString().trim();
        String nameImput = textinputErrorName.getEditText().getText().toString().trim();

        if (emailInput.isEmpty() || nameImput.isEmpty()) {
            textinputErrorMail.setError(getString(R.string.error_ingresar_valor));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textinputErrorMail.setError("Ingrese un email valido");
            return false;
        } else {
            textinputErrorMail.setError(null);
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                boolean wantToCloseDialog = false;

                if (validateEmail()) {
                    String mail = edittextMail.getText().toString();
                    String name = edittextName.getText().toString();
                    listener.addMail(mail, name);
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
            listener = (AddMailDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface AddMailDialogListener {
        void addMail(String mail, String name);
    }
}
