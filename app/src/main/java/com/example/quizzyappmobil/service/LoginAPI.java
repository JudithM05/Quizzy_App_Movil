package com.example.quizzyappmobil.service;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAPI {
    private static LoginService mAPI = null;

    public static synchronized LoginService getAPI() {
        if (mAPI == null) {
            OkHttpClient client = getUnsafeOkHttpClient();

            GsonBuilder gsonBuilder = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Retrofit retrofit = new Retrofit.Builder()
                    //.baseUrl("http://10.0.2.2:8000/")
                    .baseUrl("http://13.53.35.179:8000/")
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .client(client)
                    .build();

            mAPI = retrofit.create(LoginService.class);
        }
        return mAPI;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)      // Aumenta a 30 segundos
                .readTimeout(30, TimeUnit.SECONDS)         // Aumenta a 30 segundos
                .writeTimeout(30, TimeUnit.SECONDS);       // Aumenta a 30 segundos

        builder.hostnameVerifier((hostname, session) -> true);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);

        return builder.build();
    }
}
