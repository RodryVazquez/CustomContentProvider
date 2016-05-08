package com.example.rodrigo.customcontentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rodrigo.customcontentprovider.ContentProvider.CustomContentProvider;

import java.util.Arrays;

//
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText edtName, edtNickName;
    private Button btnAddNew, btnDeleteAll, btnShowAll;


    //Al cargar la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Referenciamos los elementos de la vista
        edtName = (EditText) findViewById(R.id.edtName);
        edtNickName = (EditText) findViewById(R.id.edtNickName);
        btnAddNew = (Button) findViewById(R.id.btnAddNew);
        btnShowAll = (Button) findViewById(R.id.btnShowAll);
        btnDeleteAll = (Button) findViewById(R.id.btnDeleteAll);

        //Inserta un nuevo registro
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                if (!edtName.getText().toString().isEmpty() && !edtNickName.getText().toString().isEmpty()) {

                    contentValues.put(CustomContentProvider.NAME, edtName.getText().toString());
                    contentValues.put(CustomContentProvider.NICK_NAME, edtNickName.getText().toString());
                    try {
                        Uri uri = getContentResolver().insert(CustomContentProvider.CONTENT_URI, contentValues);
                        Toast.makeText(MainActivity.this, "Record inserted", Toast.LENGTH_SHORT).show();

                        edtName.setText("");
                        edtNickName.setText("");
                    } catch (SQLException ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter the records first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Muestra los registros existentes
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Consultamos los registros ordenados por nombre
                String URL = "content://com.example.rodrigo.customcontentprovider.contentprovider/nicknames";
                Uri friends = Uri.parse(URL);

                try {
                    Cursor cursor = getContentResolver().query(friends, null, null, null, "name");
                    String result = "Content providers results: ";

                    if (!cursor.moveToFirst()) {
                        Toast.makeText(MainActivity.this, "No content yet", Toast.LENGTH_SHORT).show();
                    } else {
                        do {
                            result = result + "\n"
                                    + cursor.getString(cursor.getColumnIndex(CustomContentProvider.NAME))
                                    + " has nickname: "
                                    + cursor.getString(cursor.getColumnIndex(CustomContentProvider.NICK_NAME));
                        } while (cursor.moveToNext());
                        if (!result.isEmpty()) {
                            Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "No records present", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });

        //Borra los registros existentes
        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String URL = "content://com.example.rodrigo.customcontentprovider.contentprovider/nicknames";
                Uri friends = Uri.parse(URL);
                try {
                    int count = getContentResolver().delete(friends, null, null);
                    String countNum = count + " records are deleted ";
                    Toast.makeText(MainActivity.this, "" + countNum, Toast.LENGTH_SHORT).show();
                } catch (IllegalArgumentException ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
        });
    }
}
