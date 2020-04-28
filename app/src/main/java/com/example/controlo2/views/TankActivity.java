package com.example.controlo2.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.controlo2.R;
import com.example.controlo2.adapters.TankAdapter;
import com.example.controlo2.model.Constants;
import com.example.controlo2.model.ProviderCylinder;
import com.example.controlo2.model.Tank;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.controlo2.R.layout.activity_tank;

public class TankActivity extends AppCompatActivity implements TankAdapter.onClickAdparter, AddTankDialog.AddTankDialogListener, AddEmailDialog.AddMailDialogListener {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tanksReference = db.collection(Constants.COLLECTION);
    private TankAdapter tankAdapter;
    private List<ProviderCylinder> providerCylinderList = new ArrayList<>();

    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinator;
    @BindView(R.id.constraint_empty)
    ConstraintLayout constraintLayout_empty;
    @BindView(R.id.constraint_ok)
    ConstraintLayout constraintLayout_ok;
    @BindView(R.id.toolbarTank)
    Toolbar toolbar;
    @BindView(R.id.recyclerview_tanks)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.lotte_empy_state)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.textView_empty_state)
    TextView textViewEmpty_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_tank);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        runOnUiThread(this::checkCollection);
        configRecyclerview();
        getMails();

        floatingActionButton.setOnClickListener(view -> openDialog());
    }

    private void getMails() {
        db.collection(Constants.PROVIDER).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DocumentSnapshot document : task.getResult()) {
                    ProviderCylinder providerCylinder = document.toObject(ProviderCylinder.class);
                    providerCylinderList.add(providerCylinder);
                }
            }

        });
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


    @SuppressLint("RestrictedApi")
    private void checkCollection() {
        db.collection(Constants.COLLECTION).get().addOnCompleteListener(task -> {
            floatingActionButton.setVisibility(View.VISIBLE);
            if (task.isSuccessful() && task.getResult().isEmpty()) {
                constraintLayout_empty.setVisibility(View.VISIBLE);
                lottieAnimationView.setAnimation("empty-state.json");
                lottieAnimationView.playAnimation();
                textViewEmpty_state.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                constraintLayout_ok.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                constraintLayout_ok.setVisibility(View.VISIBLE);
                constraintLayout_empty.setVisibility(View.GONE);
                lottieAnimationView.pauseAnimation();
                textViewEmpty_state.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void clickTank(Tank tank) {
        Intent intent = new Intent(TankActivity.this, ControlTankActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ControlTankActivity.KEY_N_TANK, tank.getNumber());
        bundle.putSerializable(ControlTankActivity.KEY_PROVIDERS, (Serializable) providerCylinderList);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void deleteTank(Tank tankNumber, int position) {
        AlertDialog.Builder alertDialogDelete = new AlertDialog.Builder(this);
        alertDialogDelete.setTitle(R.string.borrar_cilindro)
                .setMessage("Desea eliminar el cilindro " + tankNumber.getNumber())
                .setPositiveButton(R.string.borrar, (dialog, which) -> {
                    tankAdapter.borrarItem(position);
                    tankAdapter.notifyItemRemoved(position);
                    checkCollection();
                })
                .setNegativeButton(R.string.cancelar, (dialog, which) -> {

                });
        alertDialogDelete.create().show();

    }

    private void openDialog() {
        AddTankDialog addTankDialog = new AddTankDialog();
        addTankDialog.show(getSupportFragmentManager(), "example dialog");
    }

    private void  openDialogMail(){
        AddEmailDialog emailDialog = new AddEmailDialog();
        emailDialog.show(getSupportFragmentManager(),"email dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_mail_provider_menu,menu);
        return true;
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
    public void addTank(int number, int capacity, String provider, String owner, String date, String observ) {
        Tank tank = new Tank();
        tank.setNumber(number);
        tank.setCapacity(capacity);
        tank.setProvider(provider);
        tank.setOwner(owner);
        tank.setDueDate(date);
        tank.setObservations(observ);
        tank.setOnRecharge(false);

        db.collection(Constants.COLLECTION).whereEqualTo("number", number)
                .limit(1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
                        if (!isEmpty) {
                            Snackbar.make(coordinator, R.string.cilindro_existe, Snackbar.LENGTH_SHORT).show();
                        } else {
                            db.collection("Tanks").document(String.valueOf(number))
                                    .set(tank)
                                    .addOnSuccessListener(aVoid -> {
                                        Snackbar.make(coordinator, R.string.tanque_agregado, Snackbar.LENGTH_SHORT).show();
                                        updateConstraints();
                                        Log.d("TAG", "DocumentSnapshot successfully written!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("TAG", "Error writing document", e);
                                        Snackbar.make(coordinator, R.string.error_intente_nuevamente, Snackbar.LENGTH_SHORT).show();
                                    });
                        }
                    }
                });
    }

    private void updateConstraints() {
        Handler handler = new Handler();
        handler.postDelayed(this::checkCollection, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_add_mail:
                openDialogMail();
                break;
        }
        return true;
    }

    @Override
    public void addMail(String mail, String name) {
        db.collection(Constants.PROVIDER).whereEqualTo("mail",mail).limit(1).get().addOnCompleteListener(task -> {
            boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
            if (!isEmpty){
                Snackbar.make(coordinator, R.string.mail_existe, Snackbar.LENGTH_SHORT).show();
            }
            else {
                db.collection("Provider").add(new ProviderCylinder(mail, name)).addOnSuccessListener(documentReference ->
                        Snackbar.make(coordinator, R.string.email_agregado,Snackbar.LENGTH_SHORT).show());
                getMails();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cerrar_app)
                .setCancelable(false)
                .setPositiveButton(R.string.si, (dialog, id) -> TankActivity.this.finish())
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }
}
