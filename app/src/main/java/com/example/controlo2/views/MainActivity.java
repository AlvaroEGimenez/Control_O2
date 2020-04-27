package com.example.controlo2.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.example.controlo2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


     @BindView(R.id.imageView_tank)
     ImageView imageViewTank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        imageViewTank.setOnClickListener(view -> {
            Intent intent = new Intent(this, TankActivity.class);
            startActivity(intent);
        });
    }
}
