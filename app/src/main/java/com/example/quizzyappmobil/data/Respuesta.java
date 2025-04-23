package com.example.quizzyappmobil.data;

public class Respuesta {
    private int id;
    private int id_pregunta;
    private String descripcion;
    private boolean valido;

    // Constructor
    public Respuesta(int id, int id_pregunta, String descripcion, boolean valido) {
        this.id = id;
        this.id_pregunta = id_pregunta;
        this.descripcion = descripcion;
        this.valido = valido;
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }
}