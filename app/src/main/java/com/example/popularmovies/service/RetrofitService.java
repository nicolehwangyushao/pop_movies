package com.example.popularmovies.service;

import com.example.popularmovies.BuildConfig;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private final Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    );

    private final Retrofit retrofit =
            builder
                    .client(
                            httpClient.addInterceptor(addApiKey()).build()
                    )
                    .build();
    private final MovieApiClient movieApiClient = retrofit.create(MovieApiClient.class);

    private Interceptor addApiKey() {
        return chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.API_KEY)
                    .build();

            // Request customization: add request headers
            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }

    public MovieApiClient getMovieApiClient() {
        return movieApiClient;
    }
}

