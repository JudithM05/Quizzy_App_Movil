package com.example.quizzyappmobil.data;

public class Resultado {
    private String nombreUsuario;
    private int puntos;
    private String avatar;

    public Resultado(String nombreUsuario, int puntos, String avatar) {
        this.nombreUsuario = nombreUsuario;
        this.puntos = puntos;
        this.avatar = avatar;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getAvatar() {
        return avatar;
    }
}

