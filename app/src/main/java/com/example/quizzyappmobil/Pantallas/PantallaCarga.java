package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizzyappmobil.R;

public class PantallaCarga extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_carga);

        progressBar = findViewById(R.id.progressBar);

        // Configuración de los insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Simular progreso
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                    try {
                        Thread.sleep(50); // Ajusta la velocidad (5000ms / 100 = 50ms por paso)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Cuando termina, ir a MainActivity
                Intent intent = new Intent(PantallaCarga.this, PantallaInicioSesion.class);
                startActivity(intent);
                finish();
            }
        }).start();
    }
}