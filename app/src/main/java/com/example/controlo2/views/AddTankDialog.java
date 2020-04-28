package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.controlo2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTankDialog extends AppCompatDialogFragment {

    @BindView(R.id.edittext_add_code)
    TextInputEditText edittextAddCode;
    @BindView(R.id.textinput_error_addtank)
    TextInputLayout textinputErrorAddtank;
    @BindView(R.id.edittext_add_capacity)
    TextInputEditText edittextAddCapacity;
    @BindView(R.id.textInputLayout_error_capacity)
    TextInputLayout textInputLayoutErrorCapacity;
    @BindView(R.id.edittext_add_provider)
    TextInputEditText edittextAddProvider;
    @BindView(R.id.textInputLayout_error_provider)
    TextInputLayout textInputLayoutErrorProvider;
    @BindView(R.id.edittext_add_owner)
    TextInputEditText edittextAddOwner;
    @BindView(R.id.textInputLayout_error_owner)
    TextInputLayout textInputLayoutErrorOwner;
    @BindView(R.id.edittext_add_date)
    TextInputEditText edittextAddDate;
    @BindView(R.id.textinputlayout_error_date)
    TextInputLayout textinputlayoutErrorDate;
    @BindView(R.id.edittext_add_observ)
    TextInputEditText edittextAddObserv;

    private AddTankDialogListener listener;


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

        LayoutInflater inflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.add_tank_dialog, null);

        ButterKnife.bind(this, view);


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

        TextWatcher tw = new TextWatcher() {
            private String current = "";
            private Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        String ddmmyyyy = "DDMMYYYY";
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        mon = mon < 1 ? 1 : Math.min(mon, 12);
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : Math.min(year, 2100);
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = Math.max(sel, 0);
                    current = clean;
                    edittextAddDate.setText(current);
                    edittextAddDate.setSelection(Math.min(sel, current.length()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edittextAddDate.addTextChangedListener(tw);

        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                boolean wantToCloseDialog = false;

                if (validateInputs()) {
                    String number = (edittextAddCode.getText().toString());
                    listener.addTank(Integer.parseInt(number), Integer.parseInt(edittextAddCapacity.getText().toString())
                            , edittextAddProvider.getText().toString()
                            , edittextAddOwner.getText().toString()
                            , edittextAddDate.getText().toString()
                            ,edittextAddObserv.getText().toString());
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
        void addTank(int tank, int capacity, String provider, String owner, String date, String observations);
    }

    private boolean validateInputs() {
        List<Integer> errorlist = new ArrayList<>();

        if (Objects.requireNonNull(edittextAddCode.getText()).toString().trim().equals(""))
            errorlist.add(1);
        else textinputErrorAddtank.setError(null);

        if (Objects.requireNonNull(edittextAddCapacity.getText()).toString().trim().equals(""))
            errorlist.add(2);
        else textInputLayoutErrorCapacity.setError(null);

        if (Objects.requireNonNull(edittextAddOwner.getText()).toString().trim().equals(""))
            errorlist.add(3);
        else textInputLayoutErrorOwner.setError(null);

        if (Objects.requireNonNull(edittextAddProvider.getText()).toString().trim().equals(""))
            errorlist.add(4);
        else textInputLayoutErrorProvider.setError(null);

        if (Objects.requireNonNull(edittextAddDate.getText()).toString().trim().equals(""))
            errorlist.add(5);
        else textinputlayoutErrorDate.setError(null);

        for (Integer control : errorlist) {
            switch (control) {
                case 1:
                    textinputErrorAddtank.setError("ingresar codigo");
                case 2:
                    textInputLayoutErrorCapacity.setError("ingresar capacidad");
                case 3:
                    textInputLayoutErrorOwner.setError("ingresar propietario");
                case 4:
                    textInputLayoutErrorProvider.setError("ingresar proveedor");
                case 5:
                    textinputlayoutErrorDate.setError("ingresar fecha");
            }
        }

        return errorlist.isEmpty();
    }
}


