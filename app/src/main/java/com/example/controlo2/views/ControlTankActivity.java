package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.controlo2.R;
import com.example.controlo2.model.Tank;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.itangqi.waveloadingview.WaveLoadingView;

import static com.google.android.material.snackbar.Snackbar.LENGTH_SHORT;

public class ControlTankActivity extends AppCompatActivity implements UpdatePsiDialog.UpdatePsiDialogListener {
    public static final String KEY_N_TANK = "numero_tanque";
    public static final String TAG = "controlActivity";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Long level;
    private Map<String, Object> recharge = new HashMap<>();
    private int nTank;
    private Tank tankValues = new Tank();

    @BindView(R.id.constraintlayoutControl)
    ConstraintLayout constraintlayoutControl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tank);
        ButterKnife.bind(this);
        nTank = Objects.requireNonNull(getIntent().getExtras()).getInt(KEY_N_TANK);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cilindro numero " + nTank);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.showOverflowMenu();



        floatingActionButton.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"emaildelproveedor@proveedor.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Recarga Tanque " + nTank);
            i.putExtra(Intent.EXTRA_TEXT, "El tanque " + nTank + " se encuetra en un nivel bajo se require la recarga, presion actual " + tankValues.getPressure());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(ControlTankActivity.this, "No dispone de un cliente de email", Toast.LENGTH_SHORT).show();
            }
        });

        getLevel();

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
                sendToProvider();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    private void sendToProvider() {
        if (tankValues.isOnRecharge()) {
            recharge.put("onRecharge", false);
            recharge.put("pressure", 200);

        } else {
            recharge.put("onRecharge", true);
            recharge.put("pressure", 0);
        }
        db.collection("Tanks").document(String.valueOf(nTank)).update(recharge)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        Snackbar.make(constraintlayoutControl, "Valores actualizados", LENGTH_SHORT).show();
                    }
                    getLevel();

                }).addOnFailureListener(e -> Snackbar.make(constraintlayoutControl, "Ocurrio un error intente nuevamente", LENGTH_SHORT).show());
    }

    private void setLevels() {
        final Snackbar snackbar = Snackbar.make(constraintlayoutControl, "Guardando...", LENGTH_SHORT);
        snackbar.show();

        db.collection("Tanks").document(String.valueOf(nTank)).update(recharge)
                .addOnCompleteListener(task ->
                {
                    if (task.isSuccessful()) {
                        snackbar.dismiss();
                        Snackbar.make(constraintlayoutControl, "Valores actualizados", LENGTH_SHORT).show();
                        getLevel();
                    }
                }).addOnFailureListener(e -> Snackbar.make(constraintlayoutControl, "Ocurrio un error intente nuevamente", LENGTH_SHORT).show());

    }

    private void getLevel() {
        DocumentReference docRef = db.collection("Tanks").document(String.valueOf(nTank));
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            tankValues = documentSnapshot.toObject(Tank.class);
            int pressure = tankValues.getPressure();
            waveLoadingView.setProgressValue(pressure / 2);
            waveLoadingView.setTopTitle("PSI");
            waveLoadingView.setTopTitleSize(40);
            waveLoadingView.setTopTitleColor(getResources().getColor(R.color.colorWhite));
            waveLoadingView.setCenterTitle(String.valueOf(pressure));
            waveLoadingView.setCenterTitleSize(30);
            startEvent(tankValues.getPressure());
            checkStock(tankValues.isOnRecharge());
        });

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
        level = (long) tank;
        recharge.put("pressure", level);
        recharge.put("number", nTank);
        setLevels();
    }

}


