package com.example.quizzyappmobil.Pantallas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.LoginResponse;
import com.example.quizzyappmobil.service.LoginAPI;
import com.example.quizzyappmobil.data.UsuarioLogin;
import com.example.quizzyappmobil.service.LoginService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaInicioSesion extends AppCompatActivity {

    private EditText editTextEmailAddress, editTextPassword;
    private Button buttonIniciarSesion;
    private LoginService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicio_sesion);

        // Inicialización de vistas
        editTextEmailAddress = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        buttonIniciarSesion = findViewById(R.id.buttonIniciarSesion);
        Button buttonRegistrarse = findViewById(R.id.buttonRegistrarse); // Nueva línea

        apiService = LoginAPI.getAPI();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verificando credenciales...");
        progressDialog.setCancelable(false);

        buttonIniciarSesion.setOnClickListener(v -> {
            String email = editTextEmailAddress.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese email y contraseña", Toast.LENGTH_SHORT).show();
            } else {
                realizarLogin(email, password);
            }
        });

        // Nuevo listener para el botón de registro
        buttonRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicioSesion.this, PantallaRegistroUsuario.class);
            startActivity(intent);
        });
    }

    private void realizarLogin(String email, String password) {
        progressDialog.show();

        UsuarioLogin loginData = new UsuarioLogin(email, password);
        Call<LoginResponse> call = apiService.loginUsuario(loginData);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        guardarSesion(loginResponse.getUsuario_id(), email);
                        iniciarPantallaHome();
                    }
                } else {
                    String errorMessage = "Error: " + response.code();
                    try {
                        String errorBody = response.errorBody().string();
                        errorMessage += " - " + errorBody;
                    } catch (IOException e) {
                        errorMessage += " - Error al procesar el mensaje de error";
                    }
                    Toast.makeText(PantallaInicioSesion.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PantallaInicioSesion.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarSesion(int usuarioId, String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("usuario_id", usuarioId);
        editor.putString("email", email);
        editor.putBoolean("logueado", true);
        editor.apply();
    }

    private void iniciarPantallaHome() {
        Intent intent = new Intent(this, PantallaHome.class);
        startActivity(intent);
        finish();
    }
}
