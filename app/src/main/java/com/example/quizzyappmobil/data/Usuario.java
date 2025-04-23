package com.example.quizzyappmobil.data;

public class Usuario {
    private int id;
    private String mail;
    private String nombre;
    private String password; // Nuevo campo
    private int edad;
    private String tipo;
    private String categoria; // Puede ser null
    private String avatar;
    private int pts_x_quiz;

    public int getId() {
        return id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getPts_x_quiz() {
        return pts_x_quiz;
    }

    public void setPts_x_quiz(int pts_x_quiz) {
        this.pts_x_quiz = pts_x_quiz;
    }
}