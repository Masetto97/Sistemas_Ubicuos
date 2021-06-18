package com.example.practica_final;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase para implementar una Actividad encargada de la configuración y preferencias del usuario.
 *
 * @author Eduardo Mora González
 */
public class ConfiguracionActivity extends AppCompatActivity {

    //Elementos del Activity
    private Switch switchOpcionBBDD;
    private Switch switchOpcionGoogle;
    private Switch switchOpcionStreet;
    private Button volver;

    /**
     * Metodo para cada vez que se cree una instancia de esta Actividad
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Elementos de la Activity
        switchOpcionBBDD = (Switch) findViewById(R.id.switchBBDD);
        switchOpcionGoogle  = (Switch) findViewById(R.id.switchGoogle);
        switchOpcionStreet = (Switch) findViewById(R.id.switchStreetMap);
        volver = (Button) findViewById(R.id.btnVolverConfig);

        //Inizializacion de configuraciones
        switchOpcionBBDD.setChecked(GlobalInfo.activoBBDD);
        switchOpcionGoogle.setChecked(GlobalInfo.activoMapaGoogle);
        switchOpcionStreet.setChecked(!GlobalInfo.activoMapaGoogle);

        //Logica para boton volver y guardar
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(ConfiguracionActivity.this, MainActivity.class);
                startActivity(volver);
            }
        });
    }

    /**
     * Metodo para detectar el cambio en el swtich y actualizar el valor global
     * @param view
     */
    public void onClick(View view) {

        //Swtich Relativo a la BBDD
        if(view.getId()==R.id.switchBBDD) {
            if (switchOpcionBBDD.isChecked()) {
                GlobalInfo.activoBBDD = true;
            } else {
                GlobalInfo.activoBBDD = false;
            }
        }

        //Swtich Relativo a  StreetMap
        if(view.getId()==R.id.switchStreetMap ){
            if(switchOpcionStreet.isChecked()){
                GlobalInfo.activoMapaGoogle=false;
            }else{
                GlobalInfo.activoMapaGoogle=true;
            }
        }

        //Swtich Relativo a Google Map
        if(view.getId()==R.id.switchGoogle){
            if(switchOpcionGoogle.isChecked()){
                GlobalInfo.activoMapaGoogle=true;
            }else{
                GlobalInfo.activoMapaGoogle=false;
            }
        }

        //Recargamos la pagina para ver los cambios
        Intent recargar = new Intent(ConfiguracionActivity.this, ConfiguracionActivity.class);
        startActivity(recargar);
    }
}