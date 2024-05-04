package com.example.semana06.service;

import com.example.semana06.entity.Categoria;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ServiceCategoria {
    @GET("servicio/util/listaCategoriaDeLibro")
    public Call<List<Categoria>> listaCategoria();
}
