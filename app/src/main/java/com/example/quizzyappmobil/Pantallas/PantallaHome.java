package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.adapter.QuizAdapter;
import com.example.quizzyappmobil.data.Quiz;
import com.example.quizzyappmobil.service.QuizAPI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaHome extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private RecyclerView recyclerViewRecomendados;
    private RecyclerView recyclerViewJugadosRecientemente;
    private RecyclerView recyclerViewFavoritos;

    private QuizAdapter recomendadosAdapter;
    private QuizAdapter jugadosRecientementeAdapter;
    private QuizAdapter favoritosAdapter;

    private List<Quiz> listaQuizzes = new ArrayList<>();
    private List<Quiz> listaJugadosRecientemente = new ArrayList<>();
    private List<Quiz> listaFavoritos = new ArrayList<>();

    private SharedPreferences sharedPreferences;
    private static final String PREF_FAVORITOS = "quizzes_favoritos";
    private static final String PREF_JUGADOS = "quizzes_jugados";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("QuizzyApp", MODE_PRIVATE);

        // Configurar RecyclerViews
        setupRecyclerViews();

        // Cargar datos
        cargarQuizzes();
    }

    private void setupRecyclerViews() {
        // Configurar RecyclerView Recomendados
        recyclerViewRecomendados = findViewById(R.id.recyclerViewRecomendados);
        recyclerViewRecomendados.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recomendadosAdapter = new QuizAdapter(new ArrayList<>(), this, this);
        recyclerViewRecomendados.setAdapter(recomendadosAdapter);

        // Configurar RecyclerView Jugados Recientemente
        recyclerViewJugadosRecientemente = findViewById(R.id.recyclerViewJugadosRecientemente);
        recyclerViewJugadosRecientemente.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        jugadosRecientementeAdapter = new QuizAdapter(new ArrayList<>(), this, this);
        recyclerViewJugadosRecientemente.setAdapter(jugadosRecientementeAdapter);

        // Configurar RecyclerView Favoritos
        recyclerViewFavoritos = findViewById(R.id.recyclerViewFavoritos);
        recyclerViewFavoritos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        favoritosAdapter = new QuizAdapter(new ArrayList<>(), this, this);
        recyclerViewFavoritos.setAdapter(favoritosAdapter);
    }

    private void cargarQuizzes() {
        // Llamada a la API para obtener todos los quizzes
        QuizAPI.getAPI().getAllQuizzes().enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaQuizzes = response.body();

                    // Cargar estado de favoritos desde SharedPreferences
                    Set<String> favoritosIds = sharedPreferences.getStringSet(PREF_FAVORITOS, new HashSet<>());
                    Set<String> jugadosIds = sharedPreferences.getStringSet(PREF_JUGADOS, new HashSet<>());

                    // Marcar favoritos
                    for (Quiz quiz : listaQuizzes) {
                        if (favoritosIds.contains(String.valueOf(quiz.getId()))) {
                            quiz.setEsFavorito(true);
                        }
                    }

                    // Actualizar listas
                    actualizarAdaptadores(favoritosIds, jugadosIds);
                } else {
                    Toast.makeText(PantallaHome.this,
                            "Error al cargar quizzes: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Quiz>> call, Throwable t) {
                Toast.makeText(PantallaHome.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarAdaptadores(Set<String> favoritosIds, Set<String> jugadosIds) {
        // Filtrar listas
        listaFavoritos = listaQuizzes.stream()
                .filter(Quiz::isEsFavorito)
                .collect(Collectors.toList());

        listaJugadosRecientemente = listaQuizzes.stream()
                .filter(q -> jugadosIds.contains(String.valueOf(q.getId())))
                .collect(Collectors.toList());

        // Actualizar adaptadores
        recomendadosAdapter.updateData(listaQuizzes);
        favoritosAdapter.updateData(listaFavoritos);
        jugadosRecientementeAdapter.updateData(listaJugadosRecientemente);
    }

    private void guardarFavorito(Quiz quiz) {
        Set<String> favoritosIds = sharedPreferences.getStringSet(PREF_FAVORITOS, new HashSet<>());
        Set<String> nuevosFavoritos = new HashSet<>(favoritosIds); // Crear copia para modificar

        String quizId = String.valueOf(quiz.getId());
        if (quiz.isEsFavorito()) {
            nuevosFavoritos.add(quizId);
        } else {
            nuevosFavoritos.remove(quizId);
        }

        // Guardar cambios
        sharedPreferences.edit().putStringSet(PREF_FAVORITOS, nuevosFavoritos).apply();

        // Actualizar lista de favoritos
        if (quiz.isEsFavorito()) {
            if (!listaFavoritos.contains(quiz)) {
                listaFavoritos.add(quiz);
            }
        } else {
            listaFavoritos.remove(quiz);
        }

        // Actualizar adaptador
        favoritosAdapter.updateData(listaFavoritos);
    }

    private void registrarJugado(Quiz quiz) {
        Set<String> jugadosIds = sharedPreferences.getStringSet(PREF_JUGADOS, new HashSet<>());
        Set<String> nuevosJugados = new HashSet<>(jugadosIds); // Crear copia para modificar

        nuevosJugados.add(String.valueOf(quiz.getId()));

        // Guardar cambios
        sharedPreferences.edit().putStringSet(PREF_JUGADOS, nuevosJugados).apply();

        // Actualizar lista jugados recientemente si no está ya
        if (!listaJugadosRecientemente.contains(quiz)) {
            listaJugadosRecientemente.add(0, quiz); // Agregar al inicio
            jugadosRecientementeAdapter.updateData(listaJugadosRecientemente);
        }
    }

    // Implementación de los métodos de la interfaz OnQuizClickListener
    @Override
    public void onPlayClick(Quiz quiz) {
        // Aquí iniciarías la actividad para jugar el quiz
        Toast.makeText(this, "Iniciando quiz: " + quiz.getNombre(), Toast.LENGTH_SHORT).show();
        registrarJugado(quiz);

        // Iniciar actividad de juego
        Intent intent = new Intent(this, PantallaQuiz.class);
        intent.putExtra("quiz_id", quiz.getId());
        startActivity(intent);
    }

    @Override
    public void onFavoriteClick(Quiz quiz, int position) {
        guardarFavorito(quiz);
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        // Aquí podrías mostrar detalles del quiz
        Toast.makeText(this, "Detalles de: " + quiz.getNombre(), Toast.LENGTH_SHORT).show();

        // Iniciar actividad de detalles
        // Intent intent = new Intent(this, DetallesQuizActivity.class);
        // intent.putExtra("quiz_id", quiz.getId());
        // startActivity(intent);
    }
}