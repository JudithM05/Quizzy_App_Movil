package com.example.quizzyappmobil.service;

import com.example.quizzyappmobil.data.Pregunta;
import com.example.quizzyappmobil.data.Quiz;
import com.example.quizzyappmobil.data.Respuesta;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuizService {
    @GET("quizes/")
    Call<List<Quiz>> getAllQuizzes();

    @GET("quizes/{quiz_id}")
    Call<Quiz> getQuiz(@Path("quiz_id") int quizId);

    // Obtener preguntas
    @GET("preguntas/")
    Call<List<Pregunta>> obtenerPreguntas();

    // Obtener respuestas de una pregunta específica
    @GET("respuesta/pregunta/{id_pregunta}/respuestas")
    Call<List<Respuesta>> obtenerRespuestasPorPregunta(@Path("id_pregunta") int preguntaId);
}