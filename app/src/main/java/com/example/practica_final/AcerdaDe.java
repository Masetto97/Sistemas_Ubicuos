package com.example.practica_final;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Clase donde se muestra lo relativo a la Actividad AcercaDe
 *
 * @author Eduardo Mora González
 */
public class AcerdaDe extends AppCompatActivity {

    //Elementos del Activity
    private Button volver;

    /**
     * Metodo para cada vez que se cree una instancia de esta Actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Definimos los parametros y configuracion iniciales
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerda_de);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Asignamos los elemetos del Activity
        volver = (Button) findViewById(R.id.btnVoler);

        //Logica para cuando se presione el Botón
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(AcerdaDe.this, MainActivity.class);
                startActivity(volver);
            }
        });
    }
}