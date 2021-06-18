package com.example.practica_final;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practica_final.Persistencia.DB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase para implementar una Actividad encargada de gestionar todas las opciones de la Aplicación.
 * Algunas funcionalidades extraidas y basadas en la urls mencionadas.
 *
 * @author Eduardo Mora González
 * @see "https://cursokotlin.com"
 * @see "https://code.tutsplus.com/es/tutorials/8-regular-expressions-you-should-know--net-6149"
 * @see "https://academiaandroid.com/sqlite-android-creacion-acceso-base-datos-insercion/"
 */
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    //Elementos Activity
    private Button btnC1;
    private Button btnC2;
    private ImageButton btnAcercaDe;
    private ImageButton btnConfiguracion;
    private ImageButton btnHistory;
    private ListView listaHost;
    private ArrayAdapter mAdapter;
    private Calendar c;
    private Bundle extras = new Bundle();

    //Expresiones Regulares para sacar los Host
    private String hrefRegex ="href\\s*=\\s*(?:\"([^\"]*)\"|(\\S+))";
    private String urlRegex = "^(https:\\/\\/)?[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9_\\-]+\\.[a-zA-Z0-9_\\-]+$";

    //Lista de Host encontrados
    private List<String> hosts;

    private static final String TAG = MainActivity.class.getName();

    /**
     * Metodo para cada vez que se crea la Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Configuracion Inicial del Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Definición de las Notificaciones emergente
        Toast toast =Toast.makeText(getApplicationContext(),"HOST NO VALIDO", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,Gravity.TOP, Gravity.TOP);

        //Instancia del calendario
        c = Calendar.getInstance();

        //Elementos del Activity
        btnC1 = (Button) findViewById(R.id.button);
        btnC2 = (Button) findViewById(R.id.button2);
        btnAcercaDe = (ImageButton) findViewById(R.id.btnAcercaDe);
        btnConfiguracion = (ImageButton) findViewById(R.id.btnConfiguracion);
        btnHistory = (ImageButton) findViewById(R.id.imageButtonHistory);
        listaHost = (ListView) findViewById(R.id.listHost);
        listaHost.setOnItemClickListener(this);

        //Funcionalidad de cambiar de Activity cuando se pulsa el boton del Caso 1
        btnC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Obtenemos la dirección escrita
                EditText urlName = (EditText)findViewById(R.id.editT1);
                //Creamos un intent para cambiar de Activity
                Intent intent = new Intent(MainActivity.this, MapaActivity.class);

                //Añadimos la dirección escrita
                extras.putString("URL",urlName.getText().toString());
                intent.putExtras(extras);

                //Iniciamos Activity
                startActivity(intent);
            }
        });

        //Funcionalidad de cambiar de Activity cuando se pulsa el boton del Caso 2
        btnC2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creamos una Nueva Lista
                hosts  = new ArrayList<>();

                //Creamos una Lista auxiliar
                List<String> auxHosts = new ArrayList<>();

                //Obtenemos la dirección escrita
                EditText txtNombre = (EditText)findViewById(R.id.editT1);

                //Añadimos el direccion escrita
                hosts.add(txtNombre.getText().toString());

                //Iniciamos la obtención de la informarcion de la pagina
                try {

                    //Conectamos a la URL
                    URL url = new URL("https://" + txtNombre.getText().toString());
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");

                    //Creamos un Buffer para guardar la información
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    //Configuramos el pattern de href
                    Pattern hrefPattern = Pattern.compile(hrefRegex, Pattern.CASE_INSENSITIVE);

                    //Recorremos todas las lineas obtenidas
                    while ((inputLine = in.readLine()) != null) {

                        //Buscamos coincidencias
                        Matcher matcher = hrefPattern.matcher(inputLine);
                        boolean matchFound = matcher.find();

                        //Si existen coincicendias las añadimos a la lista
                        if(matchFound) {
                            auxHosts.add(matcher.group());
                        }
                    }
                    in.close();
                } catch (Exception error) {
                    toast.show();
                    Log.v("html error", error.toString());
                }

                //Configuramos el pattern de url
                Pattern urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);

                //Recorremos la Lista auxiliar para limpiar los datos
                for(int i = 0; i< auxHosts.size();i++){
                    String aux = auxHosts.get(i);

                    aux=aux.replace("href=\"","");
                    aux=aux.replace("\"","");
                    aux= aux.substring(0, aux.length()-1);

                    //Buscamos coincidencias
                    Matcher matcher = urlPattern.matcher(aux);
                    boolean matchFound = matcher.find();

                    //Si existen coincicendias arreglamos y añadimos a la lista
                    if(matchFound) {
                        aux = aux.replace("https://","");
                        hosts.add(aux);
                    }
                }

                //Quitamos los repetidos
                Set<String> hostsLimpios = new HashSet<String>(hosts);

                //Mostramos la lista de host en la ListView de la aplicación
                mAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, hostsLimpios.toArray());
                listaHost.setAdapter(mAdapter);
            }
        });

        //Funcionalidad de cambiar de Activity cuando se pulsa el boton AcerdaDe
        btnAcercaDe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AcerdaDe.class);
                startActivity(intent);
            }
        });

        //Funcionalidad de cambiar de Activity cuando se pulsa el boton Configuracion
        btnConfiguracion.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intent);
            }
        });

        //Funcionalidad de cambiar de Activity cuando se pulsa el boton Historial
        btnHistory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                //Creamos una Nueva Lista
                hosts  = DB.getInstance().selectLinks(MainActivity.this);

                //Quitamos los repetidos
                Set<String> hostsLimpios = new HashSet<String>(hosts);

                //Mostramos la lista de host en la ListView de la aplicación
                mAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, hostsLimpios.toArray());
                listaHost.setAdapter(mAdapter);
            }
        });

        //Notificamos si existe un error en la URL
        if(GlobalInfo.error==true){
            toast.show();
            GlobalInfo.error=false;
        }
    }

    /**
     * Metodo para obtener informacion del parametro de la lista seleccionado/pulsado
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Creamos un intent para cambiar de Activity
        Intent intent = new Intent(MainActivity.this, MapaActivity.class);

        //Añadimos la dirección escrita
        extras.putString("URL", hosts.get(i));
        intent.putExtras(extras);

        //Iniciamos Activity
        startActivity(intent);
    }
}