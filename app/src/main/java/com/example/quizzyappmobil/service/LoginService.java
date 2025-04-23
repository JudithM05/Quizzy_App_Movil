package com.example.quizzyappmobil.service;

import com.example.quizzyappmobil.data.Usuario;
import com.example.quizzyappmobil.data.UsuarioLogin;
import com.example.quizzyappmobil.data.LoginResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LoginService {

    @POST("usuarios/")
    Call<Usuario> crearUsuario(@Body Usuario usuario);

    @POST("usuarios/login")
    Call<LoginResponse> loginUsuario(@Body UsuarioLogin usuarioLogin);

    @GET("usuarios/")
    Call<List<Usuario>> getAllUsuarios();

    @GET("usuarios/{usuario_id}")
    Call<Usuario> getUsuario(@Path("usuario_id") int usuarioId);

    @PUT("usuarios/{usuario_id}")
    Call<Usuario> actualizarUsuario(@Path("usuario_id") int usuarioId, @Body Usuario usuario);

    @DELETE("usuarios/{usuario_id}")
    Call<Void> eliminarUsuario(@Path("usuario_id") int usuarioId);
}
