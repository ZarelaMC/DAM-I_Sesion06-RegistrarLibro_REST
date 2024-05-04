package com.example.semana06;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.semana06.entity.Categoria;
import com.example.semana06.entity.Libro;
import com.example.semana06.entity.Pais;
import com.example.semana06.service.ServiceCategoria;
import com.example.semana06.service.ServiceLibro;
import com.example.semana06.service.ServicePais;
import com.example.semana06.util.ConnectionRest;
import com.example.semana06.util.FunctionUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //Declarar Spinners, ArrayList donde se cargarán los datos y Adapter para relacionarlos elementos
    //Pais
    Spinner spnPais;
    ArrayAdapter<String> adaptadorPais;
    ArrayList<String> paises = new ArrayList<>();

    //Categoria
    Spinner spnCategoria;
    ArrayAdapter<String> adaptadorCategoria;
    ArrayList<String> categorias = new ArrayList<>();

    //Servicios
    ServiceLibro serviceLibro;
    ServicePais servicePais;
    ServiceCategoria serviceCategoriaLibro;


    //Otros elementos
    Button btnRegistra;
    EditText txtTitulo, txtAnio, txtSerie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Implementar los Adapters
        adaptadorPais = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, paises);
        spnPais = findViewById(R.id.spnRegLibPais);
        spnPais.setAdapter(adaptadorPais);

        adaptadorCategoria = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categorias);
        spnCategoria = findViewById(R.id.spnRegLibCategoria);
        spnCategoria.setAdapter(adaptadorCategoria);

        //Inicializar los Service para acceder a la conexión -- .class indica que se está pasando la clase
        servicePais = ConnectionRest.getConnection().create(ServicePais.class);
        serviceCategoriaLibro = ConnectionRest.getConnection().create(ServiceCategoria.class);
        serviceLibro = ConnectionRest.getConnection().create(ServiceLibro.class);


        //Llamar métodos para llebar los Spinner al ejecutarse la app
        cargaPaises();
        cargaCategorias();

        txtTitulo = findViewById(R.id.txtRegLibTitulo);
        txtAnio = findViewById(R.id.txtRegLibAnio);
        txtSerie = findViewById(R.id.txtRegLibSerie);

        //Relacionar y dar función al botón
        btnRegistra = findViewById(R.id.btnRegLibEnviar);
        btnRegistra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = txtTitulo.getText().toString();
                String anio = txtAnio.getText().toString();
                String serie= txtSerie.getText().toString();
                String idPais = spnPais.getSelectedItem().toString().split(":")[0];
                String idCategoria = spnCategoria.getSelectedItem().toString().split(":")[0];

                Pais objPais = new Pais();
                objPais.setIdPais(Integer.parseInt(idPais.trim()));

                Categoria objCategoria = new Categoria();
                objCategoria.setIdCategoria(Integer.parseInt(idCategoria.trim()));

                Libro objLibro = new Libro();
                objLibro.setTitulo(titulo);
                objLibro.setAnio(Integer.parseInt(anio));
                objLibro.setSerie(serie);
                objLibro.setPais(objPais);
                objLibro.setCategoria(objCategoria);
                objLibro.setFechaRegistro(FunctionUtil.getFechaActualStringDateTime());
                objLibro.setEstado(1);

                registra(objLibro);
            }
        });
    }


    //Métodos para cargar los datos
    void cargaPaises(){
        Call<List<Pais>> call = servicePais.listaPais();
        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                if (response.isSuccessful()){
                    List<Pais> lst = response.body();
                    //para dar formato al dato mostrado
                    for(Pais obj: lst){
                        paises.add(obj.getIdPais() + " : " + obj.getNombre());
                    }
                    adaptadorPais.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {

            }
        });
    }

    void cargaCategorias(){
        Call<List<Categoria>> call = serviceCategoriaLibro.listaCategoria();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override //Cuando hay respuesta
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                if (response.isSuccessful()){
                    List<Categoria> lst = response.body();
                    //para dar formato al dato mostrado
                    for(Categoria obj: lst){
                        categorias.add(obj.getIdCategoria() + " : " + obj.getDescripcion());
                    }
                    adaptadorCategoria.notifyDataSetChanged();
                }
            }

            @Override //Cuando hay un error
            public void onFailure(Call<List<Categoria>> call, Throwable t) {

            }
        });
    }

    void registra(Libro objLibro){
        Call<Libro> call = serviceLibro.registrarLibro(objLibro);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
              if (response.isSuccessful()) {
                  Libro objSalida = response.body();
                  mensajeAlert(" Registro de Libro exitoso:  "
                          + " \n >>>> ID >> " + objSalida.getIdLibro()
                          + " \n >>> Título >>> " +  objSalida.getTitulo());
                }
            }

            @Override
            public void onFailure(Call<Libro> call, Throwable t) {

            }
        });
    }

    
    //Mensajes en Alert
    public void mensajeAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

    //Mensajes tipo Toast
    void mensajeToast(String mensaje){
        Toast toast1 =  Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG);
        toast1.show();
    }
}