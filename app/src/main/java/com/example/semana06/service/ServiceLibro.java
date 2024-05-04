package com.example.semana06.service;

import com.example.semana06.entity.Libro;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceLibro {
    @POST("servicio/libro")
    public Call<Libro> registrarLibro(@Body Libro objLibro);
}
