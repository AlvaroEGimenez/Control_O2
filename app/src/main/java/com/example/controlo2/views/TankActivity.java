package com.example.controlo2.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controlo2.R;
import com.example.controlo2.model.Tank;
import com.example.controlo2.adapters.TankAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.controlo2.R.layout.activity_tank;

public class TankActivity extends AppCompatActivity implements TankAdapter.onClickAdparter, AddTankDialog.AddTankDialogListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tanksReference = db.collection("Tanks");
    private TankAdapter tankAdapter;

    @BindView(R.id.constraintlayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.recyclerview_tanks)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_tank);

        ButterKnife.bind(this);


        runOnUiThread(this::checkCollection);
        configRecyclerview();

        floatingActionButton.setOnClickListener(view -> openDialog());
    }

    private void configRecyclerview() {
        Query query = tanksReference.orderBy("number", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Tank> options = new FirestoreRecyclerOptions.Builder<Tank>()
                .setQuery(query, Tank.class)
                .build();

        tankAdapter = new TankAdapter(options, this);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(tankAdapter);
    }


    private void checkCollection() {
        db.collection("Tanks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void clickTank(Tank tank) {
        Intent intent = new Intent(TankActivity.this, ControlTankActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ControlTankActivity.KEY_N_TANK, tank.getNumber());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteTank(int position) {

    }

    private void openDialog() {
        AddTankDialog addTankDialog = new AddTankDialog();
        addTankDialog.show(getSupportFragmentManager(), "example dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        tankAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tankAdapter.stopListening();
    }

    @Override
    public void addTank(int tank) {
        db.collection("Tanks").whereEqualTo("number", tank)
                .limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
                        if (!isEmpty) {
                            Snackbar.make(constraintLayout, "El  tanque ya existe", Snackbar.LENGTH_SHORT).show();
                        } else {
                            db.collection("Tanks").document(String.valueOf(tank))
                                    .set(new Tank(Integer.parseInt(String.valueOf(tank)), 0))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar.make(constraintLayout, "Tanque agregado", Snackbar.LENGTH_SHORT).show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(this::checkCollection, 1000);
                                            Log.d("TAG", "DocumentSnapshot successfully written!");
                                        }

                                        private void checkCollection() {
                                            db.collection("Tanks").get().addOnCompleteListener(task -> {
                                                if (task.isSuccessful() && task.getResult().isEmpty()) {
                                                    recyclerView.setVisibility(View.INVISIBLE);
                                                    progressBar.setVisibility(View.INVISIBLE);
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("TAG", "Error writing document", e);
                                        Snackbar.make(constraintLayout, "Error intente nuevamente", Snackbar.LENGTH_SHORT).show();

                                    });

                        }
                    }
                });
    }
}
