package com.example.quizzyappmobil.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pregunta {
    private int id;

    @SerializedName("id_quiz")
    private int idQuiz;

    private String descripcion;
    private String imagen;
    private int tiempo;

    private List<Respuesta> respuestas;

    public Pregunta() {
    }

    public Pregunta(int id, int idQuiz, String descripcion, String imagen) {
        this.id = id;
        this.idQuiz = idQuiz;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public int getId_quiz() {
        return idQuiz;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public int getTiempo() {
        return tiempo;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId_quiz(int idQuiz) {
        this.idQuiz = idQuiz;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    public String[] getOpciones() {
        if (respuestas == null || respuestas.size() == 0) {
            return new String[]{"Cargando...", "Cargando...", "Cargando...", "Cargando..."};
        }

        String[] opciones = new String[4];
        for (int i = 0; i < opciones.length; i++) {
            if (i < respuestas.size()) {
                opciones[i] = respuestas.get(i).getDescripcion();
            } else {
                opciones[i] = "Opción " + (i + 1);
            }
        }
        return opciones;
    }

    public int getRespuestaCorrecta() {
        if (respuestas == null) return 0;

        for (int i = 0; i < respuestas.size(); i++) {
            if (respuestas.get(i).isValido()) {
                return i;
            }
        }
        return 0;
    }

    public boolean estaCompleta() {
        return respuestas != null && !respuestas.isEmpty();
    }
}
