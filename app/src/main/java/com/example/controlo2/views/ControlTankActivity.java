package com.example.controlo2.views;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.controlo2.R;
import com.example.controlo2.model.Tank;
import com.example.controlo2.utils.InputFilterMinMax;
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

public class ControlTankActivity extends AppCompatActivity {
    public static final String KEY_N_TANK = "numero_tanque";
    public static final String TAG = "controlActivity";



    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Long level;
    private Map<String, Object> recharge = new HashMap<>();
    private int nTank;
    private Tank tankValues = new Tank();

    @BindView(R.id.constraintlayoutControl)
    ConstraintLayout constraintlayoutControl;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button2)
    Button button2;
    @BindView(R.id.button_sendMail)
    Button buttonSendMail;
    @BindView(R.id.button_toRecharge)
    Button buttonToRecharge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tank);
        ButterKnife.bind(this);


        editText.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "200")});

        nTank = Objects.requireNonNull(getIntent().getExtras()).getInt(KEY_N_TANK);
        button2.setOnClickListener(view -> {
            level = Long.valueOf(editText.getText().toString());
            if (editText.getText().toString().equals("")) {
                Toast.makeText(this, "Ingrear numero", Toast.LENGTH_SHORT).show();
            } else {
                recharge.put("pressure", level);
                recharge.put("number", nTank);
                setLevels();
            }

        });


        buttonSendMail.setOnClickListener(v -> {
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

        buttonToRecharge.setOnClickListener(v -> {
            if (tankValues.isOnRecharge()) {
                recharge.put("onRecharge", false);
                recharge.put("pressure", 100);
                buttonToRecharge.setText("Enviar a recarga");
            }
            else {
                recharge.put("onRecharge", true);
                recharge.put("pressure", 0);
                buttonToRecharge.setText("ingresar");
            }
            db.collection("Tanks").document(String.valueOf(nTank)).update(recharge)
                    .addOnCompleteListener(task ->
                    {
                        if (task.isSuccessful()) {
                            Snackbar.make(constraintlayoutControl, "Valores actualizados", LENGTH_SHORT).show();
                        }
                        getLevel();

                    }).addOnFailureListener(e -> Snackbar.make(constraintlayoutControl, "Ocurrio un error intente nuevamente", LENGTH_SHORT).show());
        });

        getLevel();

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
            int pressure =  tankValues.getPressure();
            waveLoadingView.setProgressValue(pressure / 2);
            textView3.setText(String.valueOf(tankValues.getPressure()));
            startEvent(tankValues.getPressure());
            checkStock(tankValues.isOnRecharge());
        });

    }

    private void checkStock(boolean onRecharge) {
        if (onRecharge)
            buttonToRecharge.setText("Ingresar ");
        else if (tankValues.getPressure() < 30)
            buttonToRecharge.setText("Enviar a recarga");
    }

    private void startEvent(int pressure) {
        if (pressure < 30) {
            buttonSendMail.setVisibility(View.VISIBLE);
        } else {
            buttonSendMail.setVisibility(View.INVISIBLE);
        }

    }
}

