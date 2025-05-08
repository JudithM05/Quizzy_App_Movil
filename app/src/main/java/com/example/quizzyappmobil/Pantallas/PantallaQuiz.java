package com.example.quizzyappmobil.Pantallas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PantallaQuiz extends AppCompatActivity {

    private TextView preguntaTextView;
    private Button opcion1Button, opcion2Button, opcion3Button, opcion4Button;
    private TextView resultadoTextView;
    private TextView tiempoTextView;
    private ProgressBar loadingProgressBar;

    private List<Pregunta> preguntas;
    private int preguntaActual;
    private int puntuacion;
    private QuizService apiService;
    private int quizId;
    private int preguntasCompletadas = 0;
    private boolean esperandoRespuestas = false;
    private CountDownTimer countDownTimer;
    private long tiempoRestanteMs = 30000; // 30 segundos por pregunta
    private boolean tiempoAgotado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantalla_quiz);

        quizId = getIntent().getIntExtra("QUIZ_ID", 0);
        if (quizId == 0) {
            /*Toast.makeText(this, "Error: No se especificó un quiz", Toast.LENGTH_SHORT).show();*/
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
        tiempoTextView = findViewById(R.id.tiempoTextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Configurar colores de botones
        configurarEstiloBotones();

        apiService = QuizAPI.getAPI();
        preguntas = new ArrayList<>();
        preguntaActual = 0;
        puntuacion = 0;

        mostrarCargando(true);
        cargarPreguntasDesdeAPI();

        // Configurar listeners para las opciones
        opcion1Button.setOnClickListener(v -> manejarRespuesta(0));
        opcion2Button.setOnClickListener(v -> manejarRespuesta(1));
        opcion3Button.setOnClickListener(v -> manejarRespuesta(2));
        opcion4Button.setOnClickListener(v -> manejarRespuesta(3));
    }

    private void configurarEstiloBotones() {
        int colorNaranja = Color.parseColor("#FFA500");
        opcion1Button.setBackgroundColor(colorNaranja);
        opcion2Button.setBackgroundColor(colorNaranja);
        opcion3Button.setBackgroundColor(colorNaranja);
        opcion4Button.setBackgroundColor(colorNaranja);
    }

    private void iniciarTemporizador() {
        tiempoAgotado = false;
        tiempoRestanteMs = 30000; // Resetear a 30 segundos

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(tiempoRestanteMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tiempoRestanteMs = millisUntilFinished;
                actualizarTemporizador();
            }

            @Override
            public void onFinish() {
                tiempoAgotado = true;
                tiempoTextView.setText("Tiempo agotado!");
                tiempoTextView.setTextColor(Color.RED);

                mostrarRespuestaCorrecta();

                new android.os.Handler().postDelayed(() -> {
                    siguientePregunta();
                }, 2000);
            }
        }.start();
    }

    private void actualizarTemporizador() {
        long minutos = TimeUnit.MILLISECONDS.toMinutes(tiempoRestanteMs);
        long segundos = TimeUnit.MILLISECONDS.toSeconds(tiempoRestanteMs) -
                TimeUnit.MINUTES.toSeconds(minutos);

        String tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        tiempoTextView.setText("Tiempo: " + tiempoFormateado);

        if (tiempoRestanteMs <= 10000) {
            tiempoTextView.setTextColor(Color.RED);
        } else {
            tiempoTextView.setTextColor(Color.BLACK);
        }
    }

    private void mostrarRespuestaCorrecta() {
        if (preguntaActual >= preguntas.size()) return;

        Pregunta pregunta = preguntas.get(preguntaActual);
        if (!pregunta.estaCompleta()) return;

        deshabilitarBotones();

        int respuestaCorrecta = pregunta.getRespuestaCorrecta();
        cambiarColorOpcion(respuestaCorrecta, Color.GREEN);

        String respuestaTexto = pregunta.getOpciones()[respuestaCorrecta];
        Toast.makeText(this, "Tiempo agotado! La respuesta correcta es: " + respuestaTexto,
                Toast.LENGTH_LONG).show();
    }

    private void deshabilitarBotones() {
        opcion1Button.setEnabled(false);
        opcion2Button.setEnabled(false);
        opcion3Button.setEnabled(false);
        opcion4Button.setEnabled(false);
    }

    private void siguientePregunta() {
        preguntaActual++;
        if (preguntaActual < preguntas.size()) {
            mostrarPregunta(preguntaActual);
        } else {
            mostrarResultados();
        }
    }

    private void mostrarCargando(boolean mostrar) {
        loadingProgressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        opcion1Button.setEnabled(!mostrar);
        opcion2Button.setEnabled(!mostrar);
        opcion3Button.setEnabled(!mostrar);
        opcion4Button.setEnabled(!mostrar);
    }

    private void cargarPreguntasDesdeAPI() {
        Call<List<Pregunta>> call = apiService.obtenerPreguntas();
        call.enqueue(new Callback<List<Pregunta>>() {
            @Override
            public void onResponse(Call<List<Pregunta>> call, Response<List<Pregunta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pregunta> todasLasPreguntas = response.body();
                    filtrarPreguntasDelQuiz(todasLasPreguntas);
                } else {
                    manejarErrorCargaPreguntas(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Pregunta>> call, Throwable t) {
                manejarErrorConexion(t.getMessage());
            }
        });
    }

    private void filtrarPreguntasDelQuiz(List<Pregunta> todasLasPreguntas) {
        List<Pregunta> preguntasDelQuiz = new ArrayList<>();
        for (Pregunta pregunta : todasLasPreguntas) {
            if (pregunta.getId_quiz() == quizId) {
                preguntasDelQuiz.add(pregunta);
            }
        }

        if (preguntasDelQuiz.isEmpty()) {
            mostrarCargando(false);
            Toast.makeText(this, "No hay preguntas disponibles para este quiz", Toast.LENGTH_SHORT).show();
            return;
        }

        preguntas = preguntasDelQuiz;
        cargarRespuestasParaTodasLasPreguntas();
    }

    private void cargarRespuestasParaTodasLasPreguntas() {
        for (Pregunta pregunta : preguntas) {
            cargarRespuestas(pregunta);
        }
    }

    private void cargarRespuestas(Pregunta pregunta) {
        Call<List<Respuesta>> call = apiService.obtenerRespuestasPorPregunta(pregunta.getId());
        call.enqueue(new Callback<List<Respuesta>>() {
            @Override
            public void onResponse(Call<List<Respuesta>> call, Response<List<Respuesta>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pregunta.setRespuestas(response.body());
                } else {
                    pregunta.setRespuestas(new ArrayList<>());
                }
                actualizarEstadoPreguntas(pregunta);
            }

            @Override
            public void onFailure(Call<List<Respuesta>> call, Throwable t) {
                pregunta.setRespuestas(new ArrayList<>());
                actualizarEstadoPreguntas(pregunta);
            }
        });
    }

    private void actualizarEstadoPreguntas(Pregunta pregunta) {
        preguntasCompletadas++;

        if (preguntasCompletadas == preguntas.size()) {
            mostrarCargando(false);
            mostrarPregunta(preguntaActual);
        }

        if (esperandoRespuestas && preguntas.indexOf(pregunta) == preguntaActual) {
            esperandoRespuestas = false;
            mostrarCargando(false);
            mostrarPregunta(preguntaActual);
        }
    }

    private void manejarErrorCargaPreguntas(int codigoError) {
        mostrarCargando(false);
        Toast.makeText(this, "Error al cargar preguntas: " + codigoError, Toast.LENGTH_SHORT).show();
    }

    private void manejarErrorConexion(String mensajeError) {
        mostrarCargando(false);
        Toast.makeText(this, "Error de conexión: " + mensajeError, Toast.LENGTH_SHORT).show();
    }

    private void mostrarPregunta(int indice) {
        if (preguntas.isEmpty() || indice >= preguntas.size()) {
            return;
        }

        Pregunta pregunta = preguntas.get(indice);

        if (!pregunta.estaCompleta()) {
            mostrarCargando(true);
            esperandoRespuestas = true;
            mostrarTextoCarga(pregunta);
            return;
        }

        mostrarContenidoPregunta(pregunta);
        iniciarTemporizador();
    }

    private void mostrarTextoCarga(Pregunta pregunta) {
        preguntaTextView.setText(pregunta.getDescripcion());
        opcion1Button.setText("Cargando opciones...");
        opcion2Button.setText("Cargando opciones...");
        opcion3Button.setText("Cargando opciones...");
        opcion4Button.setText("Cargando opciones...");
    }

    private void mostrarContenidoPregunta(Pregunta pregunta) {
        preguntaTextView.setText(pregunta.getDescripcion());
        String[] opciones = pregunta.getOpciones();

        opcion1Button.setText(opciones[0]);
        opcion2Button.setText(opciones[1]);
        opcion3Button.setText(opciones[2]);
        opcion4Button.setText(opciones[3]);

        resetearEstiloBotones();
    }

    private void resetearEstiloBotones() {
        configurarEstiloBotones();
        opcion1Button.setEnabled(true);
        opcion2Button.setEnabled(true);
        opcion3Button.setEnabled(true);
        opcion4Button.setEnabled(true);
    }

    private void manejarRespuesta(int opcionSeleccionada) {
        if (preguntaActual >= preguntas.size() || tiempoAgotado) {
            return;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Pregunta pregunta = preguntas.get(preguntaActual);

        if (!pregunta.estaCompleta()) {
            return;
        }

        if (opcionSeleccionada == pregunta.getRespuestaCorrecta()) {
            manejarRespuestaCorrecta(opcionSeleccionada);
        } else {
            manejarRespuestaIncorrecta(opcionSeleccionada, pregunta.getRespuestaCorrecta());
        }

        actualizarPuntuacion();
        programarSiguientePregunta();
    }

    private void manejarRespuestaCorrecta(int opcionSeleccionada) {
        Toast.makeText(this, "¡Correcto!", Toast.LENGTH_SHORT).show();
        puntuacion++;
        cambiarColorOpcion(opcionSeleccionada, Color.GREEN);
    }

    private void manejarRespuestaIncorrecta(int opcionSeleccionada, int respuestaCorrecta) {
        Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show();
        cambiarColorOpcion(opcionSeleccionada, Color.RED);
        cambiarColorOpcion(respuestaCorrecta, Color.GREEN);
    }

    private void actualizarPuntuacion() {
        resultadoTextView.setText(puntuacion + "/" + preguntas.size());
    }

    private void programarSiguientePregunta() {
        opcion1Button.postDelayed(() -> {
            siguientePregunta();
        }, 1500);
    }

    private void cambiarColorOpcion(int opcion, int color) {
        switch (opcion) {
            case 0: opcion1Button.setBackgroundColor(color); break;
            case 1: opcion2Button.setBackgroundColor(color); break;
            case 2: opcion3Button.setBackgroundColor(color); break;
            case 3: opcion4Button.setBackgroundColor(color); break;
        }
    }

    private void mostrarResultados() {
        deshabilitarBotones();
        preguntaTextView.setText("¡Quiz completado!");
        resultadoTextView.setText(puntuacion + "/" + preguntas.size());

        guardarResultado();

        new android.os.Handler().postDelayed(() -> {
            // Opción 1: Volver a la pantalla anterior (activada)
            finish();

            // Opción 2: Navegar a PantallaResultados (comentada por si acaso)
        /*Intent intent = new Intent(this, PantallaResultados.class);
        intent.putExtra("QUIZ_ID", quizId);
        intent.putExtra("PUNTAJE", puntuacion);
        intent.putExtra("TOTAL_PREGUNTAS", preguntas.size());
        startActivity(intent);
        finish();*/
        }, 1500);
    }

    private void guardarResultado() {
        getSharedPreferences("QuizzyPrefs", MODE_PRIVATE)
                .edit()
                .putInt("ULTIMO_PUNTAJE", puntuacion)
                .apply();
    }

    /*private void navegarAPantallaResultados() {
        Intent intent = new Intent(this, PantallaResultados.class);
        intent.putExtra("QUIZ_ID", quizId);
        intent.putExtra("PUNTAJE", puntuacion);
        intent.putExtra("TOTAL_PREGUNTAS", preguntas.size());
        startActivity(intent);
        finish();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}