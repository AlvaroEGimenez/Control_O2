package com.example.controlo2.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.controlo2.R;
import com.example.controlo2.utils.NetworkUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorSplash)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.splash_animation)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.splash_animation_internet)
    LottieAnimationView lottieAnimationViewInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        checkInternet(lottieAnimationView, lottieAnimationViewInternet);

    }


    private void checkInternet(LottieAnimationView lottieAnimationView, LottieAnimationView lottieAnimationViewInternet) {
        if (NetworkUtils.isNetworkConnected(this)) {
            lottieAnimationView.setVisibility(View.VISIBLE);
            lottieAnimationViewInternet.setVisibility(View.INVISIBLE);
            lottieAnimationView.setAnimation("hospital.json");
            lottieAnimationView.playAnimation();
            Intent intent = new Intent(this,TankActivity.class);
            Handler handler = new Handler();
            handler.postDelayed(() -> startActivity(intent),3000);
        } else {
            lottieAnimationViewInternet.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.INVISIBLE);
            lottieAnimationViewInternet.setAnimation("internet_connection.json");
            lottieAnimationViewInternet.playAnimation();
            Snackbar.make(coordinatorLayout, "Sin conexion a intennet", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reintentar", v -> checkInternet(lottieAnimationView, lottieAnimationViewInternet)).show();
        }
    }
}
