package com.example.controlo2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    @BindView(R.id.guideline2)
    Guideline guideline2;
    @BindView(R.id.guideline3)
    Guideline guideline3;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.editText)
    EditText editText;
    @BindView(R.id.button2)
    Button button2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_tank);
        ButterKnife.bind(this);


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
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tankValues = documentSnapshot.toObject(Tank.class);
                waveLoadingView.setProgressValue(tankValues.getPressure());
                textView3.setText(String.valueOf(tankValues.getPressure()));
                startEvent(tankValues.getPressure());
            }
        });

    }

    private void startEvent(int pressure) {
        if (pressure <30){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"emaildelproveedor@proveedor.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Recarga Tanque " + nTank);
            i.putExtra(Intent.EXTRA_TEXT   , "El tanque "+ nTank + " se encuetra en un nivel bajo se require la recarga, presion actual " + tankValues.getPressure());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(ControlTankActivity.this, "No dispone de un cliente de email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
