package com.example.quizzyappmobil.service;

import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class QuizAPI {
    private static QuizService mAPI = null;
    public static final String BASE_URL = "http://192.168.38.2:8000/";
    public static synchronized QuizService getAPI() {
        if (mAPI == null) {
            OkHttpClient client = getOkHttpClient();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.38.2:8000/") // Para dispositivos físicos
                    //.baseUrl("http://10.0.2.2:8000/") // Para emuladores
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .client(client)
                    .build();

            mAPI = retrofit.create(QuizService.class);
        }
        return mAPI;
    }

    private static OkHttpClient getOkHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(logging)
                .build();
    }
}