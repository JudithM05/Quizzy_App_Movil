package com.example.quizzyappmobil.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    //public static final String BASE_URL = "http://10.0.2.2:8000";
    public static final String BASE_URL = "http://51.20.35.158:8000";// O tu IP real si estás en un dispositivo físico

    private static Retrofit retrofit = null;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // ajusta si tu backend no usa /api
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
