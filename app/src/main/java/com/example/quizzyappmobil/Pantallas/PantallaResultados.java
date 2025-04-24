package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.adapter.ResultadosAdapter;
import com.example.quizzyappmobil.data.ResultadoQuiz;
import com.example.quizzyappmobil.data.Usuario;
import com.example.quizzyappmobil.service.QuizAPI;
import com.example.quizzyappmobil.service.QuizService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaResultados extends AppCompatActivity {

    private RecyclerView recyclerViewResultados;
    private ResultadosAdapter adapter;
    private List<ResultadoQuiz> listaResultados;
    private TextView tvTituloQuiz;
    private ImageView btnVolver;
    private QuizService apiService;
    private int quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_resultados);

        // Inicializar vistas
        tvTituloQuiz = findViewById(R.id.tvTituloQuiz);
        btnVolver = findViewById(R.id.btnVolver);
        recyclerViewResultados = findViewById(R.id.recyclerViewResultados);

        // Configurar botón de volver
        btnVolver.setOnClickListener(v -> {
            // Volver a la pantalla principal o a donde sea apropiado
            Intent intent = new Intent(PantallaResultados.this, PantallaHome.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Limpia el stack de actividades
            startActivity(intent);
            finish();
        });

        // Obtener datos del intent
        quizId = getIntent().getIntExtra("QUIZ_ID", 0);
        int puntajeUsuario = getIntent().getIntExtra("PUNTAJE", 0);
        int totalPreguntas = getIntent().getIntExtra("TOTAL_PREGUNTAS", 0);

        if (quizId == 0) {
            Toast.makeText(this, "Error: No se especificó un quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar servicio API
        apiService = QuizAPI.getAPI();

        // Configurar RecyclerView
        listaResultados = new ArrayList<>();
        adapter = new ResultadosAdapter(this, listaResultados);
        recyclerViewResultados.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResultados.setAdapter(adapter);

        // Obtener información del usuario y guardar su resultado
        SharedPreferences prefs = getSharedPreferences("QuizzyPrefs", MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", 0);

        if (userId > 0) {
            // Enviar resultado al servidor (solo si tenemos ID de usuario)
            enviarResultado(userId, quizId, puntajeUsuario);
        }

        // Cargar todos los resultados para este quiz
        cargarResultados(quizId);
    }

    private void enviarResultado(int userId, int quizId, int puntaje) {
        // Crear objeto para enviar
        ResultadoQuiz nuevoResultado = new ResultadoQuiz();
        nuevoResultado.setUsuarioId(userId);
        nuevoResultado.setQuizId(quizId);
        nuevoResultado.setPuntaje(puntaje);

        // Enviar a la API
        Call<ResultadoQuiz> call = apiService.enviarResultadoQuiz(nuevoResultado);
        call.enqueue(new Callback<ResultadoQuiz>() {
            @Override
            public void onResponse(Call<ResultadoQuiz> call, Response<ResultadoQuiz> response) {
                if (response.isSuccessful()) {
                    // Recargar resultados después de enviar el nuevo
                    cargarResultados(quizId);
                } else {
                    Toast.makeText(PantallaResultados.this,
                            "Error al guardar resultado: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResultadoQuiz> call, Throwable t) {
                Toast.makeText(PantallaResultados.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarResultados(int quizId) {
        Call<List<ResultadoQuiz>> call = apiService.obtenerResultadosPorQuiz(quizId);
        call.enqueue(new Callback<List<ResultadoQuiz>>() {
            @Override
            public void onResponse(Call<List<ResultadoQuiz>> call, Response<List<ResultadoQuiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaResultados.clear();
                    listaResultados.addAll(response.body());

                    // Cargar información de usuarios para cada resultado
                    for (ResultadoQuiz resultado : listaResultados) {
                        cargarInfoUsuario(resultado);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PantallaResultados.this,
                            "Error al cargar resultados: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResultadoQuiz>> call, Throwable t) {
                Toast.makeText(PantallaResultados.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarInfoUsuario(ResultadoQuiz resultado) {
        Call<Usuario> call = apiService.obtenerUsuarioPorId(resultado.getUsuarioId());
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resultado.setUsuario(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                // Si falla, simplemente continuamos sin info del usuario
            }
        });
    }
}