package com.example.quizzyappmobil.Pantallas;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzyappmobil.R;
import com.example.quizzyappmobil.data.Pregunta;
import com.example.quizzyappmobil.data.Respuesta;
import com.example.quizzyappmobil.service.QuizAPI;
import com.example.quizzyappmobil.service.QuizService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaQuiz extends AppCompatActivity {

    private TextView preguntaTextView;
    private Button opcion1Button, opcion2Button, opcion3Button, opcion4Button;
    private TextView resultadoTextView;
    private ProgressBar loadingProgressBar;

    private List<Pregunta> preguntas;
    private int preguntaActual;
    private int puntuacion;
    private QuizService apiService;
    private int quizId;
    private int preguntasCompletadas = 0;
    private boolean esperandoRespuestas = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_quiz);

        // Obtener el ID del quiz de los extras
        quizId = getIntent().getIntExtra("QUIZ_ID", 0);
        if (quizId == 0) {
            // Si no hay un ID de quiz válido, mostrar un error y finalizar la actividad
            Toast.makeText(this, "Error: No se especificó un quiz", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        preguntaTextView = findViewById(R.id.preguntaTextView);
        opcion1Button = findViewById(R.id.opcion1Button);
        opcion2Button = findViewById(R.id.opcion2Button);
        opcion3Button = findViewById(R.id.opcion3Button);
        opcion4Button = findViewById(R.id.opcion4Button);
        resultadoTextView = findViewById(R.id.resultadoTextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Aplicar estilo a los botones (color naranja)
        opcion1Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion2Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion3Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion4Button.setBackgroundColor(Color.parseColor("#FFA500"));

        apiService = QuizAPI.getAPI();
        preguntas = new ArrayList<>();
        preguntaActual = 0;
        puntuacion = 0;

        // Mostrar indicador de carga
        mostrarCargando(true);

        // Cargar preguntas desde la API
        cargarPreguntasDesdeAPI();

        // Configurar listeners para las opciones
        opcion1Button.setOnClickListener(v -> manejarRespuesta(0));
        opcion2Button.setOnClickListener(v -> manejarRespuesta(1));
        opcion3Button.setOnClickListener(v -> manejarRespuesta(2));
        opcion4Button.setOnClickListener(v -> manejarRespuesta(3));
    }

    private void mostrarCargando(boolean mostrar) {
        if (mostrar) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            opcion1Button.setEnabled(false);
            opcion2Button.setEnabled(false);
            opcion3Button.setEnabled(false);
            opcion4Button.setEnabled(false);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            opcion1Button.setEnabled(true);
            opcion2Button.setEnabled(true);
            opcion3Button.setEnabled(true);
            opcion4Button.setEnabled(true);
        }
    }

    private void cargarPreguntasDesdeAPI() {
        // Obtener todas las preguntas
        Call<List<Pregunta>> call = apiService.obtenerPreguntas();
        call.enqueue(new Callback<List<Pregunta>>() {
            @Override
            public void onResponse(Call<List<Pregunta>> call, Response<List<Pregunta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pregunta> todasLasPreguntas = response.body();
                    // Filtramos las preguntas que pertenecen a este quiz
                    List<Pregunta> preguntasDelQuiz = new ArrayList<>();
                    for (Pregunta pregunta : todasLasPreguntas) {
                        if (pregunta.getId_quiz() == quizId) {
                            preguntasDelQuiz.add(pregunta);
                        }
                    }

                    if (preguntasDelQuiz.isEmpty()) {
                        mostrarCargando(false);
                        Toast.makeText(PantallaQuiz.this, "No hay preguntas disponibles para este quiz", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    preguntas = preguntasDelQuiz;

                    // Iniciar la carga de respuestas para todas las preguntas
                    for (Pregunta pregunta : preguntas) {
                        cargarRespuestas(pregunta);
                    }
                } else {
                    mostrarCargando(false);
                    Toast.makeText(PantallaQuiz.this, "Error al cargar preguntas: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pregunta>> call, Throwable t) {
                mostrarCargando(false);
                Toast.makeText(PantallaQuiz.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarRespuestas(Pregunta pregunta) {
        Call<List<Respuesta>> call = apiService.obtenerRespuestasPorPregunta(pregunta.getId());
        call.enqueue(new Callback<List<Respuesta>>() {
            @Override
            public void onResponse(Call<List<Respuesta>> call, Response<List<Respuesta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pregunta.setRespuestas(response.body());
                    preguntasCompletadas++;

                    // Si todas las preguntas tienen sus respuestas, mostrar la primera
                    if (preguntasCompletadas == preguntas.size()) {
                        mostrarCargando(false);
                        mostrarPregunta(preguntaActual);
                    }

                    // Si estamos esperando las respuestas de la pregunta actual,
                    // y ya está completa, mostrarla
                    if (esperandoRespuestas && preguntas.indexOf(pregunta) == preguntaActual) {
                        esperandoRespuestas = false;
                        mostrarCargando(false);
                        mostrarPregunta(preguntaActual);
                    }
                } else {
                    // Si falla la carga de respuestas, establecer una lista vacía
                    pregunta.setRespuestas(new ArrayList<>());
                    preguntasCompletadas++;

                    if (preguntasCompletadas == preguntas.size()) {
                        mostrarCargando(false);
                        mostrarPregunta(preguntaActual);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Respuesta>> call, Throwable t) {
                // Si falla la solicitud, contar igualmente como completada
                pregunta.setRespuestas(new ArrayList<>());
                preguntasCompletadas++;

                if (preguntasCompletadas == preguntas.size()) {
                    mostrarCargando(false);
                    mostrarPregunta(preguntaActual);
                }
            }
        });
    }

    private void mostrarPregunta(int indice) {
        if (preguntas.isEmpty() || indice >= preguntas.size()) {
            return;
        }

        Pregunta pregunta = preguntas.get(indice);

        // Verificar si la pregunta tiene sus respuestas cargadas
        if (!pregunta.estaCompleta()) {
            // Si las respuestas no están cargadas, mostrar cargando y esperar
            mostrarCargando(true);
            esperandoRespuestas = true;

            // Mostrar texto de espera
            preguntaTextView.setText(pregunta.getDescripcion());
            opcion1Button.setText("Cargando opciones...");
            opcion2Button.setText("Cargando opciones...");
            opcion3Button.setText("Cargando opciones...");
            opcion4Button.setText("Cargando opciones...");
            return;
        }

        // Mostrar la pregunta y sus opciones
        preguntaTextView.setText(pregunta.getDescripcion());

        // Obtener las opciones de respuesta
        String[] opciones = pregunta.getOpciones();

        // Establecer textos en los botones
        opcion1Button.setText(opciones[0]);
        opcion2Button.setText(opciones[1]);
        opcion3Button.setText(opciones[2]);
        opcion4Button.setText(opciones[3]);

        // Restablecer colores de botones
        opcion1Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion2Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion3Button.setBackgroundColor(Color.parseColor("#FFA500"));
        opcion4Button.setBackgroundColor(Color.parseColor("#FFA500"));
    }

    private void manejarRespuesta(int opcionSeleccionada) {
        if (preguntaActual >= preguntas.size()) {
            return;
        }

        Pregunta pregunta = preguntas.get(preguntaActual);

        // Si la pregunta no tiene respuestas cargadas, no hacer nada
        if (!pregunta.estaCompleta()) {
            return;
        }

        if (opcionSeleccionada == pregunta.getRespuestaCorrecta()) {
            Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
            puntuacion++;
            // Cambiar color de la opción correcta
            cambiarColorOpcion(opcionSeleccionada, Color.GREEN);
        } else {
            Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
            // Cambiar color de la opción seleccionada y la correcta
            cambiarColorOpcion(opcionSeleccionada, Color.RED);
            cambiarColorOpcion(pregunta.getRespuestaCorrecta(), Color.GREEN);
        }

        // Actualizar puntuación
        resultadoTextView.setText(puntuacion + "/" + preguntas.size());

        // Programar el cambio a la siguiente pregunta después de un tiempo
        opcion1Button.postDelayed(() -> {
            // Pasar a la siguiente pregunta
            preguntaActual++;
            if (preguntaActual < preguntas.size()) {
                mostrarPregunta(preguntaActual);
            } else {
                mostrarResultados();
            }
        }, 1500); // Esperar 1.5 segundos para mostrar la siguiente
    }

    private void cambiarColorOpcion(int opcion, int color) {
        switch (opcion) {
            case 0:
                opcion1Button.setBackgroundColor(color);
                break;
            case 1:
                opcion2Button.setBackgroundColor(color);
                break;
            case 2:
                opcion3Button.setBackgroundColor(color);
                break;
            case 3:
                opcion4Button.setBackgroundColor(color);
                break;
        }
    }

    private void mostrarResultados() {
        // Deshabilitar botones de opciones
        opcion1Button.setEnabled(false);
        opcion2Button.setEnabled(false);
        opcion3Button.setEnabled(false);
        opcion4Button.setEnabled(false);

        // Cambiar texto de la pregunta para mostrar fin del quiz
        preguntaTextView.setText("¡Quiz completado!");

        // Mostrar puntuación final
        resultadoTextView.setText("Puntuación final: " + puntuacion + "/" + preguntas.size());

        // Aquí puedes añadir lógica adicional, como guardar resultados,
        // mostrar un botón para volver al menú principal, etc.

        // Por ejemplo, podríamos añadir un botón para volver después de un tiempo
        opcion1Button.postDelayed(() -> {
            finish(); // Cierra la actividad y vuelve a la anterior
        }, 5000); // Esperar 5 segundos antes de volver
    }
}