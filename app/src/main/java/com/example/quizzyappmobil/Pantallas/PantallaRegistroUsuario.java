package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.Usuario;
import com.example.quizzyappmobil.service.LoginAPI;
import com.example.quizzyappmobil.service.LoginService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaRegistroUsuario extends AppCompatActivity {

    private EditText nombreEditText, emailEditText, passwordEditText, repeatPasswordEditText, edadEditText;
    private RadioGroup tipoRadioGroup;
    private CheckBox recordarmeCheckBox;
    private Button registroButton;
    private LoginService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_registro_usuario);

        // Inicializar vistas
        nombreEditText = findViewById(R.id.nombreEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPasswordEditText);
        edadEditText = findViewById(R.id.edadEditText);
        tipoRadioGroup = findViewById(R.id.tipoRadioGroup);
        recordarmeCheckBox = findViewById(R.id.recordarmeCheckBox);
        registroButton = findViewById(R.id.registroButton);

        // Inicializar servicio API
        apiService = LoginAPI.getAPI();

        // Configurar listener del botón de registro
        registroButton.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Obtener valores de los campos
        String nombre = nombreEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String repeatPassword = repeatPasswordEditText.getText().toString().trim();
        String edadStr = edadEditText.getText().toString().trim();

        // Validar campos obligatorios
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar contraseñas coincidentes
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar edad
        int edad;
        try {
            edad = Integer.parseInt(edadStr);
            if (edad <= 0) {
                Toast.makeText(this, "Edad no válida", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Edad no válida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener tipo de usuario
        int selectedId = tipoRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);

        if (radioButton == null) {
            Toast.makeText(this, "Seleccione un tipo de usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        String tipo = radioButton.getText().toString();

        // Crear objeto Usuario (categoría puede ser null)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setMail(email);
        nuevoUsuario.setPassword(password); // Asegúrate de incluir la contraseña
        nuevoUsuario.setEdad(edad);
        nuevoUsuario.setTipo(tipo);
        nuevoUsuario.setCategoria(null); // Categoría inicialmente nula
        nuevoUsuario.setAvatar("avatar_predeterminado.png");
        nuevoUsuario.setPts_x_quiz(0);

        // Enviar registro al servidor
        enviarRegistroAServidor(nuevoUsuario);
    }

    private void enviarRegistroAServidor(Usuario usuario) {
        Call<Usuario> call = apiService.crearUsuario(usuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PantallaRegistroUsuario.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    // Si el checkbox está marcado, guardar preferencias
                    if (recordarmeCheckBox.isChecked()) {
                        guardarPreferenciasUsuario(usuario);
                    }

                    // Redirigir a pantalla de inicio
                    Intent intent = new Intent(PantallaRegistroUsuario.this, PantallaInicioSesion.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PantallaRegistroUsuario.this,
                            "Error en el registro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(PantallaRegistroUsuario.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarPreferenciasUsuario(Usuario usuario) {
        // Implementa según tus necesidades para recordar al usuario
        // Puedes usar SharedPreferences aquí
    }
}