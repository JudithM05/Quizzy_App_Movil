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
import com.example.quizzyappmobil.adapter.ResultadoQuizAdapter;
import com.example.quizzyappmobil.data.ResultadoQuiz;
import com.example.quizzyappmobil.data.Usuario;
import com.example.quizzyappmobil.service.QuizAPI;
import com.example.quizzyappmobil.service.QuizService;
import com.example.quizzyappmobil.service.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
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

    private RecyclerView recyclerView;
    private ResultadoQuizAdapter adapter;
    private List<ResultadoQuiz> listaResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_resultados);

        recyclerView = findViewById(R.id.recyclerResultados);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listaResultados = new ArrayList<>();
        adapter = new ResultadoQuizAdapter(listaResultados);
        recyclerView.setAdapter(adapter);

        cargarResultados();
    }

    private void cargarResultados() {
        int quizId = getIntent().getIntExtra("QUIZ_ID", -1);
        if (quizId == -1) return;

        QuizService quizService = RetrofitClient.getInstance().create(QuizService.class);
        Call<List<ResultadoQuiz>> call = quizService.obtenerResultadosPorQuiz(quizId);

        call.enqueue(new Callback<List<ResultadoQuiz>>() {
            @Override
            public void onResponse(Call<List<ResultadoQuiz>> call, Response<List<ResultadoQuiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaResultados = response.body();

                    // Ordenar por puntaje descendente
                    Collections.sort(listaResultados, (r1, r2) -> Integer.compare(r2.getPuntaje(), r1.getPuntaje()));

                    mostrarTop3();
                    adapter.updateLista(listaResultados); // Mostrar el resto
                }
            }

            @Override
            public void onFailure(Call<List<ResultadoQuiz>> call, Throwable t) {
                Toast.makeText(PantallaResultados.this, "Error al cargar resultados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarTop3() {
        if (listaResultados.size() < 3) return;

        ResultadoQuiz primero = listaResultados.get(0);
        ResultadoQuiz segundo = listaResultados.get(1);
        ResultadoQuiz tercero = listaResultados.get(2);

        cargarEnPodio(primero, R.id.imgPrimero, R.id.tvNombrePrimero, R.id.tvPuntajePrimero);
        cargarEnPodio(segundo, R.id.imgSegundo, R.id.tvNombreSegundo, R.id.tvPuntajeSegundo);
        cargarEnPodio(tercero, R.id.imgTercero, R.id.tvNombreTercero, R.id.tvPuntajeTercero);

        listaResultados = listaResultados.subList(3, listaResultados.size()); // El resto
    }

    private void cargarEnPodio(ResultadoQuiz r, int imgId, int nombreId, int puntajeId) {
        if (r.getUsuario() == null) return;

        Glide.with(this)
                .load(getAvatarUrl(r))
                .placeholder(R.drawable.avatar_predeterminado)
                .circleCrop()
                .into((ImageView) findViewById(imgId));

        ((TextView) findViewById(nombreId)).setText(r.getUsuario().getNombre());
        ((TextView) findViewById(puntajeId)).setText(r.getPuntaje() + " pts");
    }

    private String getAvatarUrl(ResultadoQuiz r) {
        if (r.getUsuario() != null && r.getUsuario().getAvatar() != null) {
            return "http://192.168.38.2:8000/uploads/users/" + r.getUsuario().getAvatar();
        }
        return "";
    }
}
