package com.example.practica_final;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.practica_final.Modelo.Busqueda;
import com.example.practica_final.Persistencia.DB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Clase donde se muestran los Host solicitados por el usuario en los distintos mapas.
 *
 * @author Eduardo Mora González
 */
public class MapaActivity extends AppCompatActivity implements SensorEventListener {

    //Elementos Activity
    private Button volver;
    private WebView webView;

    //Parametros relativos a las urls y obtencion de datos
    private JSONObject obj;
    private String url;
    private String urlFinal;

    //Coordenadas
    private String longitud;
    private String latitud;

    //Links de Mapas
    private String googleMaps = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    private String openStreet = "http://www.openstreetmap.org/index.html?mlat=%s&mlon=%s&zoom=10";

    //Variables relativas al sensor
    private boolean availableAccelerometer;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long ultimoCambio;
    private float last_x, last_y, last_z;
    private static final int UMBRAL = 100;

    //Variables relativas a la BBDD
    private Busqueda busqueda;

    /**
     * Metodo para cada vez que se crea la Activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Creamos las instancias y los permisos necesarios
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        GlobalInfo.cambioMapa=false;

        //Bundle para obtener los parametros de la Activity Principal
        Bundle parametros = this.getIntent().getExtras();

        //Obtenemos la lista de sensores disponibles
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (senAccelerometer != null){
            availableAccelerometer = true;
        }else{
            Toast.makeText(this, "No se detecta Acelerometro: Función cambio de Mapa no activa", Toast.LENGTH_SHORT).show();
        }
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        //Declaramos los elementos del Activity
        webView = (WebView) findViewById(R.id.webview);
        volver = (Button) findViewById(R.id.btnVolverMapa);

        //Comprobacion de los parametros, en el caso de que no, se establece una opción por defecto
        if(parametros !=null){
            url = parametros.getString("URL");
            GlobalInfo.url=url;
        } else{
            url =GlobalInfo.url;
        }

        //Obtenemos el contenido de la pagina
        try {

            //Hacemos la llamada a freegeoip con el host para obtener el hot
            URL link = new URL("https://freegeoip.app/json/"+url);
            HttpURLConnection con = (HttpURLConnection) link.openConnection();
            con.setRequestMethod("GET");

            //Leemos el contenido de la pagina y lo guardamos
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            //Creamos un Objeto para guardar el contenido JSON obtenido anteriormente
            try {
                obj = new JSONObject(content.toString());
            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON");

                //Avisamos del error y revargamos el sistema
                GlobalInfo.error=true;
                Intent volver = new Intent(MapaActivity.this, MainActivity.class);
                startActivity(volver);
            }

        } catch (Exception error) {
            Log.v("json error", error.toString());
            //Avisamos del error y revargamos el sistema
            GlobalInfo.error=true;
            Intent volver = new Intent(MapaActivity.this, MainActivity.class);
            startActivity(volver);
        }

        //Obtenemos las coordenadas del JSOn
        try {
            latitud=obj.getString("latitude");
            longitud=obj.getString("longitude");
        } catch (JSONException e) {
            e.printStackTrace();
            //Avisamos del error y revargamos el sistema
            GlobalInfo.error=true;
            Intent volver = new Intent(MapaActivity.this, MainActivity.class);
            startActivity(volver);
        }

        //Depende la opcion elegimos GoogleMap u openStreet
        if(GlobalInfo.activoMapaGoogle){
            urlFinal = String.format(googleMaps, latitud, longitud);
        }else{
            urlFinal = String.format(openStreet, latitud, longitud);
        }

        //Metemos en la BBDD
        if(!GlobalInfo.cambioMapa){
            meterBusquedaBBDD(url);
        }

        //Visualizamos la web
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(urlFinal);

        //Logica para cuando se pulse el boton
        volver.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent volver = new Intent(MapaActivity.this, MainActivity.class);
                startActivity(volver);
            }
        });
    }

    /**
     * Metodo para realizar las operaciones cuando se decteca que un sensor cambia.
     * Funcionalidad basada en los ejemplos del temario.
     *
     * @param sensorEvent
     * @see "http://code.tutsplus.com/tutorials/using-the-accelerometer-on-android--mobile-22125"
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //Declaramos el sensor que usaremos
        Sensor miSensor = sensorEvent.sensor;

        //Si no se esta cambiando de mapa en este momento
        if(!GlobalInfo.cambioMapa){

            //Si el evento es del sensor Acelerometro
            if (miSensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                //Obtenemos los valores
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                long tiempoActual = System.currentTimeMillis();

                //Si el ultimo cambio ha sido despues de 1 segundos
                if ((tiempoActual - ultimoCambio) > 1000) {

                    //Calculamos la diferencia de tiempos
                    long diferenciaTiempo = (tiempoActual - ultimoCambio);
                    ultimoCambio = tiempoActual;

                    //Calculamos la velocidad de movimiento
                    float velocidad = Math.abs(x+y+z - last_x - last_y - last_z)/ diferenciaTiempo * 10000;

                    //Si la velocidad es mayor que el umbral cambiamos de mapa
                    if (velocidad > UMBRAL) {
                        Toast.makeText(getApplicationContext(), "CAMBIAMOS DE MAPA", Toast.LENGTH_SHORT).show();
                        boolean actual = GlobalInfo.activoMapaGoogle;
                        GlobalInfo.activoMapaGoogle = !actual;
                        GlobalInfo.cambioMapa=true;
                        finish();
                        startActivity(getIntent());
                    }
                    //Asignamos los nuevos valores
                    last_x = x;
                    last_y = y;
                    last_z = z;
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    /**
     * Metodo cuando se reinicia la actividad
     */
    protected void onResume() {
        super.onResume();
        if (availableAccelerometer) {
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Metodo cuando se pausa la actividad
     */
    protected void onPause() {
        super.onPause();
        if (availableAccelerometer) {
            senSensorManager.unregisterListener(this);
        }
    }

    /**
     * Metodo para insertar la busqueda en la BBDD
     * @param url Host buscado
     */
    public void meterBusquedaBBDD(String url) {
        if (GlobalInfo.activoBBDD) {
            // Insert data from database.
            busqueda = new Busqueda(url, ObtenerFecha(), ObtenerHora());
            DB.getInstance().insertBusqueda(MapaActivity.this, busqueda);
        }
    }


    /**
     * Metodo para obtener la Fecha actual
     * @return fecha actual
     */
    public static Date ObtenerFecha() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = new Date();
        try {
            fechaDate = formato.parse(fechaDate.toString());
        }
        catch (ParseException ex)
        {
            System.out.println(ex);
        }
        return fechaDate;
    }

    /**
     * Metodo para obtener la Hora actual
     * @return hora actual
     */
    public static Date ObtenerHora()
    {
        SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
        Date fechaDate = new Date();
        try {
            fechaDate = formato.parse(fechaDate.toString());
        }
        catch (ParseException ex)
        {
            System.out.println(ex);
        }
        return fechaDate;
    }
}