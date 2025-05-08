package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaBuscar extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private EditText searchField;
    private ImageView iconoFiltros;
    private RecyclerView recyclerViewTop5;
    private RecyclerView recyclerViewNuevos;
    private RecyclerView recyclerViewUsuariosPopulares;
    private TextView textoTop5;
    private TextView textoNuevos;
    private TextView textoUsuariosPopulares;
    private QuizAdapter quizAdapterTop5;
    private QuizAdapter quizAdapterNuevos;
    private QuizAdapter quizAdapterUsuariosPopulares;
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
        recyclerViewNuevos = findViewById(R.id.recyclerViewNuevos);
        recyclerViewUsuariosPopulares = findViewById(R.id.recyclerViewUsuariosPopulares);
        textoTop5 = findViewById(R.id.texto_top5);
        textoNuevos = findViewById(R.id.texto_nuevos);
        textoUsuariosPopulares = findViewById(R.id.texto_usuarios_populares);

        recyclerViewTop5.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewNuevos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewUsuariosPopulares.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        quizAdapterTop5 = new QuizAdapter(new ArrayList<>(), this, this);
        quizAdapterNuevos = new QuizAdapter(new ArrayList<>(), this, this);
        quizAdapterUsuariosPopulares = new QuizAdapter(new ArrayList<>(), this, this);

        recyclerViewTop5.setAdapter(quizAdapterTop5);
        recyclerViewNuevos.setAdapter(quizAdapterNuevos);
        recyclerViewUsuariosPopulares.setAdapter(quizAdapterUsuariosPopulares);

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

                    // Sort quizzes by fecha_creacion (newest first)
                    Collections.sort(allQuizzes, new Comparator<Quiz>() {
                        private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        @Override
                        public int compare(Quiz q1, Quiz q2) {
                            try {
                                Date date1 = sdf.parse(q1.getFecha_creacion());
                                Date date2 = sdf.parse(q2.getFecha_creacion());
                                return date2.compareTo(date1); // Newest first
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0; // Default to no change if parsing fails
                            }
                        }
                    });

                    // Populate initial sections
                    List<Quiz> top5Quizzes = allQuizzes.subList(0, Math.min(5, allQuizzes.size())); // Top 5 (could be sorted by puntuacion)
                    List<Quiz> nuevosQuizzes = allQuizzes.subList(0, Math.min(5, allQuizzes.size())); // Newest 5 quizzes
                    List<Quiz> usuariosPopularesQuizzes = allQuizzes.subList(Math.min(5, allQuizzes.size()), Math.min(10, allQuizzes.size())); // Placeholder for popular authors

                    quizAdapterTop5.updateData(top5Quizzes);
                    quizAdapterNuevos.updateData(nuevosQuizzes);
                    quizAdapterUsuariosPopulares.updateData(usuariosPopularesQuizzes);

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

        // Hide other sections and show only search results
        if (!query.isEmpty()) {
            textoTop5.setVisibility(android.view.View.GONE);
            recyclerViewTop5.setVisibility(android.view.View.GONE);
            textoNuevos.setVisibility(android.view.View.GONE);
            recyclerViewNuevos.setVisibility(android.view.View.GONE);
            textoUsuariosPopulares.setVisibility(android.view.View.GONE);
            recyclerViewUsuariosPopulares.setVisibility(android.view.View.GONE);

            // Reuse recyclerViewTop5 for search results with vertical layout
            recyclerViewTop5.setLayoutManager(new LinearLayoutManager(this));
            quizAdapterTop5.updateData(filteredQuizzes);
            recyclerViewTop5.setVisibility(android.view.View.VISIBLE);
        } else {
            textoTop5.setVisibility(android.view.View.VISIBLE);
            recyclerViewTop5.setVisibility(android.view.View.VISIBLE);
            textoNuevos.setVisibility(android.view.View.VISIBLE);
            recyclerViewNuevos.setVisibility(android.view.View.VISIBLE);
            textoUsuariosPopulares.setVisibility(android.view.View.VISIBLE);
            recyclerViewUsuariosPopulares.setVisibility(android.view.View.VISIBLE);

            // Reset to initial state
            Collections.sort(allQuizzes, new Comparator<Quiz>() {
                private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public int compare(Quiz q1, Quiz q2) {
                    try {
                        Date date1 = sdf.parse(q1.getFecha_creacion());
                        Date date2 = sdf.parse(q2.getFecha_creacion());
                        return date2.compareTo(date1); // Newest first
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return 0; // Default to no change if parsing fails
                    }
                }
            });

            List<Quiz> top5Quizzes = allQuizzes.subList(0, Math.min(5, allQuizzes.size())); // Top 5 (could be sorted by puntuacion)
            List<Quiz> nuevosQuizzes = allQuizzes.subList(0, Math.min(5, allQuizzes.size())); // Newest 5 quizzes
            List<Quiz> usuariosPopularesQuizzes = allQuizzes.subList(Math.min(5, allQuizzes.size()), Math.min(10, allQuizzes.size())); // Placeholder for popular authors

            quizAdapterTop5.updateData(top5Quizzes);
            quizAdapterNuevos.updateData(nuevosQuizzes);
            quizAdapterUsuariosPopulares.updateData(usuariosPopularesQuizzes);
            recyclerViewTop5.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
    }

    @Override
    public void onPlayClick(Quiz quiz) {
    }

    @Override
    public void onFavoriteClick(Quiz quiz, int position) {
        Toast.makeText(this, quiz.isEsFavorito() ? "Agregado a favoritos" : "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onQuizClick(Quiz quiz) {
        Toast.makeText(this, "Seleccionaste: " + quiz.getNombre(), Toast.LENGTH_SHORT).show();
    }
}