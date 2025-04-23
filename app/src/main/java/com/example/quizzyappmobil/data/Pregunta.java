package com.example.quizzyappmobil.data;

import java.util.List;

public class Pregunta {
    private int id;
    private int id_quiz;
    private String descripcion;
    private String imagen;
    private List<Respuesta> respuestas;

    // Constructor
    public Pregunta(int id, int id_quiz, String descripcion, String imagen) {
        this.id = id;
        this.id_quiz = id_quiz;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_quiz() {
        return id_quiz;
    }

    public void setId_quiz(int id_quiz) {
        this.id_quiz = id_quiz;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<Respuesta> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }

    // Método para obtener las opciones como array para la pantalla de quiz
    public String[] getOpciones() {
        if (respuestas == null || respuestas.size() == 0) {
            return new String[]{"Cargando...", "Cargando...", "Cargando...", "Cargando..."};
        }

        // Aseguramos que siempre tengamos 4 opciones
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

    // Método para obtener el índice de la respuesta correcta
    public int getRespuestaCorrecta() {
        if (respuestas == null) {
            return 0;
        }

        for (int i = 0; i < respuestas.size(); i++) {
            if (respuestas.get(i).isValido()) {
                return i;
            }
        }
        return 0; // Por defecto, la primera opción
    }

    // Método para comprobar si la pregunta está lista para mostrarse
    public boolean estaCompleta() {
        return respuestas != null && !respuestas.isEmpty();
    }
}