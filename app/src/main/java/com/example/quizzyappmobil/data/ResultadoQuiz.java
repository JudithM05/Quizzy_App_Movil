package com.example.quizzyappmobil.data;

import com.google.gson.annotations.SerializedName;

public class ResultadoQuiz {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int usuarioId;

    @SerializedName("quiz_id")
    private int quizId;

    @SerializedName("score")
    private int puntaje;

    // Campo que no viene de la API, lo llenamos nosotros
    private Usuario usuario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
