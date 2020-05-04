package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.ekn.gruzer.gaugelibrary.HalfGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.example.controlo2.R;
import com.example.controlo2.model.Constants;
import com.example.controlo2.model.ProviderCylinder;
import com.example.controlo2.model.Tank;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class ControlTankActivity extends AppCompatActivity implements UpdatePsiDialog.UpdatePsiDialogListener {
    public static final String KEY_N_TANK = "numero_tanque";
    public static final String KEY_PROVIDERS = "providers";
    public static final String TAG = "controlActivity";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TankActivity tankActivity = new TankActivity();
    private Map<String, Object> recharge = new HashMap<>();
    private int nTank;
    private Tank tankValues = new Tank();
    private List<ProviderCylinder> providerCylinderList = new ArrayList<>();
    private ArrayList<String> mailList = new ArrayList<>();
    private Range range = new Range();
    private Range range1 = new Range();
    private Range range2 = new Range();

    @BindView(R.id.coordinatorlayoutControl)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.progressBarControl)
    ProgressBar progressBarControl;
    @BindView(R.id.halfGauge)
    HalfGauge halfGauge;
    @BindView(R.id.textViewDueDate)
    TextView textViewDueDate;
    @BindView(R.id.textViewProvider)
    TextView textViewProvider;
    @BindView(R.id.textViewOwner)
    TextView textViewOwner;
    @BindView(R.id.textViewObserv)
    TextView textViewObserv;
    @BindView(R.id.editTextObserv)
    EditText editTextObserv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tank);
        ButterKnife.bind(this);

        editTextObserv.setVisibility(View.GONE);

        editTextObserv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    updateObservations(editTextObserv.getText().toString());
                    return true;
                }
                return false;
            }
        });

        if (getIntent().getExtras() != null) {
            nTank = Objects.requireNonNull(getIntent().getExtras()).getInt(KEY_N_TANK);
            providerCylinderList = (List<ProviderCylinder>) getIntent().getExtras().getSerializable(KEY_PROVIDERS);
        }
        if (providerCylinderList != null) {
            for (ProviderCylinder providerCylinder : providerCylinderList) {
                String mail = providerCylinder.getMail();
                mailList.add(mail);
            }
        }
        halfGauge.setMaxValue(200);
        halfGauge.setMinValue(0);

        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(0.0);
        range.setTo(66.0);

        range1.setColor(Color.parseColor("#E3E500"));
        range1.setFrom(66.00);
        range1.setTo(132.0);

        range2.setColor(Color.parseColor("#00b20b"));
        range2.setFrom(132.0);
        range2.setTo(200.0);


        halfGauge.addRange(range);
        halfGauge.addRange(range1);
        halfGauge.addRange(range2);


        String mails = mailList.toString().replaceAll("[\\[\\]]", "");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cilindro " + nTank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.showOverflowMenu();


        floatingActionButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{mails});
            i.putExtra(Intent.EXTRA_SUBJECT, "Recarga Tanque " + nTank);
            i.putExtra(Intent.EXTRA_TEXT, "El cilinro " + nTank + " se encuetra en un nivel bajo se require la recarga, presion actual " + tankValues.getPressure());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(ControlTankActivity.this, "No dispone de un cliente de email", Toast.LENGTH_SHORT).show();
            }
        });

        getLevel();
        checkColection();

    }

    private void updateObservations(String observations) {
        recharge.put("observations",observations);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

        final Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.guardando, LENGTH_SHORT);
        snackbar.show();

        db.collection(Constants.COLLECTION).document(String.valueOf(nTank)).update(recharge)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        snackbar.dismiss();
                        Snackbar.make(coordinatorLayout, R.string.valores_actualizados, LENGTH_SHORT).show();
                        textViewObserv.setVisibility(View.VISIBLE);
                        editTextObserv.setVisibility(View.GONE);
                        getLevel();

                    }

                }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.ocurrio_error, LENGTH_SHORT).show());
    }


    private void checkColection() {
        db.collection(Constants.COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                halfGauge.setVisibility(View.INVISIBLE);
                progressBarControl.setVisibility(View.INVISIBLE);
            } else {
                progressBarControl.setVisibility(View.GONE);
                halfGauge.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openDialog() {
        UpdatePsiDialog updatePsiDialog = new UpdatePsiDialog();
        updatePsiDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.update_psi_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.send_to_provider);
        if (tankValues.isOnRecharge())
            item.setTitle(R.string.ingresar_cilindro);
        else
            item.setTitle(R.string.enviar_recarga);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                openDialog();
                break;
            case R.id.send_to_provider:
                confirmAction();
                break;
            case R.id.action_add_observ:
                addObservation();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    private void addObservation() {
        textViewObserv.setVisibility(View.INVISIBLE);
        editTextObserv.setVisibility(View.VISIBLE);
        if (tankValues.getObservations() != null)
        editTextObserv.setText(tankValues.getObservations());
    }

    private void confirmAction() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String positiveButton;

        if (tankValues.isOnRecharge())
            positiveButton = getString(R.string.ingresar);
        else
            positiveButton = getString(R.string.enviar);

        // Add the buttons
        builder.setTitle("Confirmar")
                .setMessage("Cilindro " + nTank);

        builder.setPositiveButton(positiveButton, (dialog, id) -> {
            // User clicked OK button
            sendToProvider();
        });
        builder.setNegativeButton(R.string.cancelar, (dialog, id) -> {
            // User cancelled the dialog
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void sendToProvider() {
        if (tankValues.isOnRecharge()) {
            recharge.put("onRecharge", false);

        } else {
            recharge.put("onRecharge", true);
            recharge.put("pressure", 0);
        }
        db.collection("Tanks").document(String.valueOf(nTank)).update(recharge)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        Snackbar.make(coordinatorLayout, R.string.valores_actualizados, LENGTH_SHORT).show();
                    }
                    getLevel();
                }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.ocurrio_error, LENGTH_SHORT).show());
    }

    private void setLevels() {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.guardando, LENGTH_SHORT);
        snackbar.show();

        db.collection(Constants.COLLECTION).document(String.valueOf(nTank)).update(recharge)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        snackbar.dismiss();
                        Snackbar.make(coordinatorLayout, R.string.valores_actualizados, LENGTH_SHORT).show();
                        getLevel();
                    }
                }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.ocurrio_error, LENGTH_SHORT).show());

    }

    private void getLevel() {
        DocumentReference docRef = db.collection(Constants.COLLECTION).document(String.valueOf(nTank));
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            tankValues = documentSnapshot.toObject(Tank.class);
            int pressure = 0;
            if (tankValues != null) {
                pressure = tankValues.getPressure();
                textViewDueDate.setText(tankValues.getDueDate());
                textViewProvider.setText(tankValues.getProvider());
                textViewOwner.setText(tankValues.getOwner());
                if (tankValues.getObservations() == null || tankValues.getObservations().equals(""))
                    textViewObserv.setText(R.string.sin_observaciones);
                else
                    textViewObserv.setText(tankValues.getObservations());
            }
            halfGauge.setValue(pressure);
            startEvent(tankValues.getPressure());
            checkStock(tankValues.isOnRecharge());
        }).addOnFailureListener(e -> Snackbar.make(coordinatorLayout, R.string.ocurrio_error, LENGTH_SHORT).show());

    }

    private void checkStock(boolean onRecharge) {

    }

    @SuppressLint("RestrictedApi")
    private void startEvent(int pressure) {
        if (pressure < 30) {
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            floatingActionButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void updatePsi(int tank) {
        Long level = (long) tank;
        recharge.put("pressure", level);
        recharge.put("number", nTank);
        setLevels();
    }

}


