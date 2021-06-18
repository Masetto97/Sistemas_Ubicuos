package com.example.practica_final.Persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase para crear la BBDD. La clase está basada en los ejemplos del temario.
 *
 * @author Eduardo Mora González
 * @see "https://ubuvirtual.ubu.es/course/view.php?id=12689"
 */
public class DBSqliteHelper extends SQLiteOpenHelper {

    // DDL SQL statements
    public static final String sqlCreateBusquedas = "CREATE TABLE [Busquedas] ("
            + "[idBusqueda] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + "[link] VARCHAR(50)  NULL,"
            + "[FechaBusqueda] DATE  NULL,"
            + "[HoraBusqueda] TIME  NULL)";

    /**
     * Constructor
     *
     * @param context context
     * @param name nombre
     * @param factory factory
     * @param version version
     */
    public DBSqliteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * On create database.
     *
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Executing DDL with create tables
        db.execSQL(sqlCreateBusquedas);
    }

    /**
     * On upgrade database.
     *
     * @param db database
     * @param previousVersion previous version number
     * @param newVersion new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int previousVersion, int newVersion) {

        // NOTE: easy solution dropping tables and creating again
        // Remove table...
        db.execSQL("DROP TABLE IF EXISTS Busquedas");

        // Create new versions... (only create table again in this solution)
        db.execSQL(sqlCreateBusquedas);
    }
}
