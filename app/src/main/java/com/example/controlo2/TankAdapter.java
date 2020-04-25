package com.example.controlo2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public  class TankAdapter extends FirestoreRecyclerAdapter<Tank, TankAdapter.TankHolder> {

    private onClickAdparter clickAdparter;

    public TankAdapter(FirestoreRecyclerOptions<Tank> options, onClickAdparter adparter) {
        super(options);
        this.clickAdparter = adparter;

    }

    @Override
    protected void onBindViewHolder(@NonNull TankHolder tankHolder, int i, @NonNull Tank recharge) {
        tankHolder.textview_number.setText(String.valueOf(recharge.getNumber()));
        tankHolder.itemView.setOnClickListener(v -> clickAdparter.clickTank(recharge));

        tankHolder.itemView.setOnLongClickListener(v -> {
            clickAdparter.deleteTank(i);
            return true;
        });
    }


    @NonNull
    @Override
    public TankHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_tank,
                viewGroup, false);

        return new TankHolder(view);
    }

    public void borrarItem(int posicion) {
        getSnapshots().getSnapshot(posicion).getReference().delete();

    }


    class TankHolder extends RecyclerView.ViewHolder {
        TextView textview_number;

        TankHolder(@NonNull View itemView) {
            super(itemView);
            textview_number = itemView.findViewById(R.id.tank_number);

        }
    }

    public interface onClickAdparter{
        void clickTank(Tank tank);
        void deleteTank(int position);
    }

}


