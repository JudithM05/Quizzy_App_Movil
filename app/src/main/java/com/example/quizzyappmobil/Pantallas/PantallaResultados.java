package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.adapter.ResultadoQuizAdapter;
import com.example.quizzyappmobil.data.ResultadoQuiz;
import com.example.quizzyappmobil.service.QuizService;
import com.example.quizzyappmobil.service.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaResultados extends AppCompatActivity {

    private ImageView btnVolver;
    private TextView tvTituloQuiz;
    private RecyclerView recyclerResultados;
    private ResultadoQuizAdapter adapter;
    private List<ResultadoQuiz> listaResultados = new ArrayList<>();
    private int quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_resultados);

        // Obtener datos del intent
        quizId = getIntent().getIntExtra("QUIZ_ID", -1);
        String nombreQuiz = getIntent().getStringExtra("QUIZ_NOMBRE");
        int puntajeUsuario = getIntent().getIntExtra("PUNTAJE", 0);
        int totalPreguntas = getIntent().getIntExtra("TOTAL_PREGUNTAS", 0);

        // Inicializar vistas
        btnVolver = findViewById(R.id.btnVolver);
        tvTituloQuiz = findViewById(R.id.tvTituloQuiz);
        recyclerResultados = findViewById(R.id.recyclerResultados);

        // Configurar título del quiz
        tvTituloQuiz.setText(nombreQuiz != null ? nombreQuiz : "Resultados");

        // Configurar RecyclerView
        adapter = new ResultadoQuizAdapter(listaResultados);
        recyclerResultados.setLayoutManager(new LinearLayoutManager(this));
        recyclerResultados.setAdapter(adapter);

        // Configurar botón de volver
        btnVolver.setOnClickListener(v -> volverAHome());

        // Cargar resultados
        cargarResultados();
    }

    private void volverAHome() {
        Intent intent = new Intent(this, PantallaHome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void cargarResultados() {
        if (quizId == -1) {
            Toast.makeText(this, "Error: Quiz no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        QuizService quizService = RetrofitClient.getInstance().create(QuizService.class);
        Call<List<ResultadoQuiz>> call = quizService.obtenerResultadosPorQuiz(quizId);

        call.enqueue(new Callback<List<ResultadoQuiz>>() {
            @Override
            public void onResponse(Call<List<ResultadoQuiz>> call, Response<List<ResultadoQuiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaResultados = response.body();
                    mostrarResultados(listaResultados);
                } else {
                    Toast.makeText(PantallaResultados.this, "Error al cargar resultados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResultadoQuiz>> call, Throwable t) {
                Toast.makeText(PantallaResultados.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarResultados(List<ResultadoQuiz> resultados) {
        // Ordenar por puntaje descendente
        Collections.sort(resultados, (r1, r2) -> Integer.compare(r2.getPuntaje(), r1.getPuntaje()));

        // Mostrar podio (primeros 3 puestos)
        mostrarPodio(resultados);

        // Mostrar el resto de resultados (a partir del puesto 4)
        if (resultados.size() > 3) {
            listaResultados = resultados.subList(3, resultados.size());
            adapter.updateLista(listaResultados);
        } else {
            listaResultados.clear();
            adapter.updateLista(listaResultados);
        }
    }

    private void mostrarPodio(List<ResultadoQuiz> resultados) {
        if (resultados.size() >= 1) {
            ResultadoQuiz primero = resultados.get(0);
            cargarEnPodio(primero, R.id.imgPrimero, R.id.tvNombrePrimero, R.id.tvPuntajePrimero);
        }
        if (resultados.size() >= 2) {
            ResultadoQuiz segundo = resultados.get(1);
            cargarEnPodio(segundo, R.id.imgSegundo, R.id.tvNombreSegundo, R.id.tvPuntajeSegundo);
        }
        if (resultados.size() >= 3) {
            ResultadoQuiz tercero = resultados.get(2);
            cargarEnPodio(tercero, R.id.imgTercero, R.id.tvNombreTercero, R.id.tvPuntajeTercero);
        }
    }

    private void cargarEnPodio(ResultadoQuiz resultado, int imgId, int nombreId, int puntajeId) {
        ImageView imgAvatar = findViewById(imgId);
        TextView tvNombre = findViewById(nombreId);
        TextView tvPuntaje = findViewById(puntajeId);

        if (resultado.getUsuario() != null) {
            tvNombre.setText(resultado.getUsuario().getNombre());
            tvPuntaje.setText(resultado.getPuntaje() + " pts");

            Glide.with(this)
                    .load(getAvatarUrl(resultado))
                    .placeholder(R.drawable.avatar_predeterminado)
                    .circleCrop()
                    .into(imgAvatar);
        }
    }

    private String getAvatarUrl(ResultadoQuiz r) {
        if (r.getUsuario() != null && r.getUsuario().getAvatar() != null) {
            return "http://192.168.38.2:8000/uploads/users/" + r.getUsuario().getAvatar();
        }
        return "";
    }
}