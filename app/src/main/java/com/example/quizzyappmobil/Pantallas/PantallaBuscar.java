package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.adapter.QuizAdapter;
import com.example.quizzyappmobil.data.Quiz;
import com.example.quizzyappmobil.service.ApiClient;
import com.example.quizzyappmobil.service.QuizService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaBuscar extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private EditText searchField;
    private ImageView iconoFiltros;
    private RecyclerView recyclerViewTop5;
    private QuizAdapter quizAdapter;
    private List<Quiz> allQuizzes = new ArrayList<>();
    private List<Quiz> filteredQuizzes = new ArrayList<>();
    private String[] categorias = {"Matemáticas", "Física", "Química", "Historia", "Literatura"};
    private boolean[] seleccionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_buscar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        searchField = findViewById(R.id.searchField);
        iconoFiltros = findViewById(R.id.iconoFiltros);
        recyclerViewTop5 = findViewById(R.id.recyclerViewTop5);

        recyclerViewTop5.setLayoutManager(new LinearLayoutManager(this));

        // ✅ ARREGLO: pasamos el contexto correcto y el listener (this)
        quizAdapter = new QuizAdapter(filteredQuizzes, this, this);
        recyclerViewTop5.setAdapter(quizAdapter);

        seleccionados = new boolean[categorias.length];

        cargarQuizzes();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                aplicarFiltros();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        iconoFiltros.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PantallaBuscar.this);
            builder.setTitle("Selecciona categorías")
                    .setMultiChoiceItems(categorias, seleccionados, (dialog, which, isChecked) -> {
                        seleccionados[which] = isChecked;
                    })
                    .setPositiveButton("Aceptar", (dialog, which) -> aplicarFiltros())
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    private void cargarQuizzes() {
        QuizService service = ApiClient.getClient().create(QuizService.class);
        Call<List<Quiz>> call = service.getAllQuizzes();

        call.enqueue(new Callback<List<Quiz>>() {
            @Override
            public void onResponse(Call<List<Quiz>> call, Response<List<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allQuizzes.clear();
                    allQuizzes.addAll(response.body());
                    aplicarFiltros();
                } else {
                    Toast.makeText(PantallaBuscar.this, "Error al obtener los quizzes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Quiz>> call, Throwable t) {
                Toast.makeText(PantallaBuscar.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aplicarFiltros() {
        String query = searchField.getText().toString().toLowerCase().trim();
        List<String> categoriasSeleccionadas = new ArrayList<>();
        for (int i = 0; i < seleccionados.length; i++) {
            if (seleccionados[i]) {
                categoriasSeleccionadas.add(categorias[i]);
            }
        }

        filteredQuizzes.clear();
        for (Quiz quiz : allQuizzes) {
            boolean coincideTitulo = quiz.getNombre().toLowerCase().contains(query);
            boolean coincideCategoria = categoriasSeleccionadas.isEmpty() || categoriasSeleccionadas.contains(quiz.getCategorias());
            if (coincideTitulo && coincideCategoria) {
                filteredQuizzes.add(quiz);
            }
        }

        quizAdapter.notifyDataSetChanged();
    }

    // Implementación de la interfaz del adapter
    @Override
    public void onPlayClick(Quiz quiz) {
        // Ya se lanza la pantalla en el adapter, no necesitas hacer nada aquí
    }

    @Override
    public void onFavoriteClick(Quiz quiz, int position) {
        PantallaHome anadirFavorito = new PantallaHome();
        Toast.makeText(this, quiz.isEsFavorito() ? "Agregado a favoritos" : "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        anadirFavorito.onFavoriteClick(quiz, position);
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        Toast.makeText(this, "Seleccionaste: " + quiz.getNombre(), Toast.LENGTH_SHORT).show();
    }
}
