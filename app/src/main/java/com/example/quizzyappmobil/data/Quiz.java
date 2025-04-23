package com.example.quizzyappmobil.data;

// Modelo de Quiz para representar los datos del servidor
public class Quiz {
    private int id;
    private String nombre;
    private int id_usuario;
    private String categorias;
    private int num_total_preguntas;
    private String fecha_creacion;
    private int puntuacion;
    private String imagen;
    private boolean esFavorito;
    private String nombreAutor; // Opcional, depende de tu API

    // Constructor, getters y setters
    public Quiz(int id, String nombre, int id_usuario, String categorias,
                int num_total_preguntas, String fecha_creacion, int puntuacion, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.id_usuario = id_usuario;
        this.categorias = categorias;
        this.num_total_preguntas = num_total_preguntas;
        this.fecha_creacion = fecha_creacion;
        this.puntuacion = puntuacion;
        this.imagen = imagen;
        this.esFavorito = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getCategorias() {
        return categorias;
    }

    public void setCategorias(String categorias) {
        this.categorias = categorias;
    }

    public int getNum_total_preguntas() {
        return num_total_preguntas;
    }

    public void setNum_total_preguntas(int num_total_preguntas) {
        this.num_total_preguntas = num_total_preguntas;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isEsFavorito() {
        return esFavorito;
    }

    public void setEsFavorito(boolean esFavorito) {
        this.esFavorito = esFavorito;
    }

    public String getNombreAutor() {
        return nombreAutor != null ? nombreAutor : "Prof1"; // Valor predeterminado
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }
}

