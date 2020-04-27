package com.example.controlo2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.controlo2.R;
import com.example.controlo2.model.Tank;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;


public  class TankAdapter extends FirestoreRecyclerAdapter<Tank, TankAdapter.TankHolder> {

    private onClickAdparter clickAdparter;
    private Context context;

    public TankAdapter(FirestoreRecyclerOptions<Tank> options, onClickAdparter adparter) {
        super(options);
        this.clickAdparter = adparter;

    }

    @Override
    protected void onBindViewHolder(@NonNull TankHolder tankHolder, int i, @NonNull Tank recharge) {
        tankHolder.textview_number.setText(String.valueOf(recharge.getNumber()));
        tankHolder.itemView.setOnClickListener(v -> clickAdparter.clickTank(recharge));

        if (recharge.isOnRecharge()) {
            tankHolder.textViewInStock.setText("EN RECARGA");
            tankHolder.textViewInStock.setTextColor(ContextCompat.getColor(context,R.color.colorRed));
            tankHolder.textViewPsi.setText(" - ");
        }
            else {
            tankHolder.textViewInStock.setText("DISPONIBLE");
            tankHolder.textViewInStock.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            tankHolder.textViewPsi.setText(String.valueOf(recharge.getPressure()));
        }

        tankHolder.itemView.setOnLongClickListener(v -> {
            clickAdparter.deleteTank(recharge, i);
            return true;
        });
    }


    @NonNull
    @Override
    public TankHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tank,
                viewGroup, false);
        context = viewGroup.getContext();

        return new TankHolder(view);
    }

    public void borrarItem(int posicion) {
        getSnapshots().getSnapshot(posicion).getReference().delete();

    }


    class TankHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tank_number)
        TextView textview_number;
        @BindView(R.id.textView_psi)
        TextView textViewPsi;
        @BindView(R.id.textView_inStock)
        TextView textViewInStock;

        TankHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

    public interface onClickAdparter{
        void clickTank(Tank tank);
        void deleteTank(Tank tankDelete, int position);
    }

}


