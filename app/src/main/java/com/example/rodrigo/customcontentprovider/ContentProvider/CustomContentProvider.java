package com.example.rodrigo.customcontentprovider.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Content provider
 */
public class CustomContentProvider extends ContentProvider {

    //Campos para el contentProvider
    static final String PROVIDER_NAME = "com.rodrigovazquez.provider";
    static final String URL = "content://" + PROVIDER_NAME + "/nicknames";
    static final Uri CONTENT_URI = Uri.parse(URL);

    //Valores utilizados por el content Uri
    static final int NICKNAME = 1;
    static final int NICKNAME_ID = 2;

    //Map para el query
    private static HashMap<String, String> nickNamesMap;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames", NICKNAME);
        uriMatcher.addURI(PROVIDER_NAME, "nicknames/#", NICKNAME_ID);
    }


    DBHelper dbHelper;


    //region DATABASE
    static final String ID = "id";
    static final String NAME = "name";
    static final String NiCK_NAME = "nickname";

    private SQLiteDatabase database;
    static final String DATABASE_NAME = "NicknamesDirectory";
    static final String TABLE_NAME = "Nicknames";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE = "CREATE TABLE" + TABLE_NAME +
            "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL," +
            "nickname TEXT NOT NULL);";

    //Crea y maneja la base de datos
    private static class DBHelper extends SQLiteOpenHelper {

        /**
         * @param context
         */
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        /**
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
            onCreate(db);
        }
    }

    //endregion

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        //Habilitamos para lectura y escritura
        database = dbHelper.getWritableDatabase();
        if (database == null) {
            return false;
        } else {
            return true;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case NICKNAME:
                queryBuilder.setProjectionMap(nickNamesMap);
                break;
            case NICKNAME_ID:
                queryBuilder.appendWhere(ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (sortOrder == null || sortOrder == "") {
            sortOrder = NAME;
        }

        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        //registramos el uri
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    //Patron vnd.android.cursor.dir | vnd.android.cursor.item
    @Nullable
    @Override
    public String getType(Uri uri) {
        //retornamos el tipo de datos que retorna el content provider
        switch(uriMatcher.match(uri)){
            //Listado de registros
            case NICKNAME:
                return "vnd.android.cursor.dir/vnd.example.nicknames";
            //Registro unico
            case NICKNAME_ID:
                return "vnd.android.cursor.item/vnd.example.nicknames";
            default:
                throw new IllegalArgumentException("Unsupported URI :" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);
        //Si el registro se guardo exitosamente
        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case NICKNAME:
                //Borramos todos los elementos de la tabla
                count = database.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case NICKNAME_ID:
                String id = uri.getLastPathSegment();
                count = database.delete(TABLE_NAME,ID +" = " + id +(!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : ""),selectionArgs);
                break;

            default:
                throw  new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case NICKNAME:
                //Borramos todos los elementos de la tabla
                count = database.update(TABLE_NAME,values,selection,selectionArgs);
                break;
            case NICKNAME_ID:
                String id = uri.getLastPathSegment();
                count = database.update(TABLE_NAME, values,ID +" = " + id +(!TextUtils.isEmpty(selection) ? "AND (" + selection + ")" : ""),selectionArgs);
                break;

            default:
                throw  new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
}
