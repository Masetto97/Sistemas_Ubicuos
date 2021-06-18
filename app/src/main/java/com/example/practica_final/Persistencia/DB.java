package com.example.practica_final.Persistencia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.practica_final.Modelo.Busqueda;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Clase para configuracion y ejecuccion de la BBDD. La clase está basada en los ejemplos del temario.
 *
 * @author Eduardo Mora González
 * @see "https://ubuvirtual.ubu.es/course/view.php?id=12689"
 */
public class DB {
    private static final String TAG = DB.class.getName();

    /** Nombre Base de Datos */
    public static final String DB_EXAMPLE = "DBExercise";

    /**
     * Instancia Base de Datos
     */
    private static final DB db = new DB();

    /**
     * Constructor.
     */
    private DB() {
    }

    /**
     * Get Instancia.
     *
     * @return Instancia Base de Datos
     */
    public static DB getInstance() {
        return db;
    }

    /**
     * Metodo para insertar una Busqueda en la BBDD
     * @param context Contexto Actual
     * @param data Datos a meter en la BBDD
     */
    public void insertBusqueda( Context context, Busqueda data) {
        DBSqliteHelper dbSqlite = null;
        SQLiteDatabase dbHelper = null;
        try {

            //Configuración Inicial BBDD
            dbSqlite = new DBSqliteHelper((Context) context, DB_EXAMPLE, null, 1);
            dbHelper = dbSqlite.getReadableDatabase();
            dbHelper.beginTransaction();

            //Añadimos los parametros a un Content
            ContentValues values = new ContentValues();
            values.put("link", data.getLink());
            values.put("FechaBusqueda", data.getFechaBusqueda().toString());
            values.put("HoraBusqueda", data.getHoraBusqueda().toString());

            //Ejecutar QUERY
            long newRowId =  dbHelper.insert("Busquedas",null,values);
            dbHelper.setTransactionSuccessful();

            Log.e(TAG, "Datos insertados en la BBDD: " + data.toString());

        } catch (Exception ex) {
            Log.e(TAG, "Error in database access: " + ex.getLocalizedMessage());
        } finally {
            dbHelper.endTransaction();
            dbHelper.close();
            dbSqlite.close();
        }
    }

    /**
     * Metodo para obtener todas las Busquedas guardadas en la BBDD
     * @param context
     */
    public List<String> selectLinks(Context context) {

        DBSqliteHelper dbSqlite = null;
        SQLiteDatabase dbHelper = null;
        Cursor cursor = null;

        //Lista con los resultados
        List<String> host = new ArrayList<>();;

        try {
            dbSqlite = new DBSqliteHelper(context, DB_EXAMPLE,
                    null, 1);
            // read mode
            dbHelper = dbSqlite.getReadableDatabase();
            dbHelper.beginTransaction();

            // Ejecutamos QUERY
            cursor = dbHelper.rawQuery("SELECT * FROM Busquedas", null);

            // Cargamos los datos recorriendo el cursor
            if (cursor.moveToFirst()) {
                do {
                    String link = cursor.getString(1);
                    host.add(link);
                } while (cursor.moveToNext());
            }

            dbHelper.setTransactionSuccessful();

        } catch (Exception ex) {
            Log.e(TAG, "Error in database access: " + ex.getLocalizedMessage());
        } finally {
            dbHelper.endTransaction();
            cursor.close();
            dbHelper.close();
            dbSqlite.close();
        }

        return host;
    }
}
