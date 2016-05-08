package com.example.rodrigo.customcontentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rodrigo.customcontentprovider.ContentProvider.CustomContentProvider;

import java.util.Arrays;

//
public class MainActivity extends AppCompatActivity {

    private EditText edtName, edtNickName;
    private Button btnAddNew,btnDeleteAll,btnShowAll;


    //Al cargar la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtName = (EditText)findViewById(R.id.edtName);
        edtNickName = (EditText)findViewById(R.id.edtNickName);

        btnAddNew = (Button)findViewById(R.id.btnAddNew);
        btnShowAll = (Button)findViewById(R.id.btnShowAll);
        btnDeleteAll = (Button)findViewById(R.id.btnDeleteAll);

        //Inserta un nuevo registro
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                if(!edtName.getText().toString().isEmpty() && !edtNickName.getText().toString().isEmpty()){

                    contentValues.put(CustomContentProvider.NAME,edtName.getText().toString());
                    contentValues.put(CustomContentProvider.NICK_NAME,edtNickName.getText().toString());

                    Uri uri = getContentResolver().insert(CustomContentProvider.CONTENT_URI,contentValues);
                    Toast.makeText(MainActivity.this, "Record inserted", Toast.LENGTH_SHORT).show();

                    edtName.setText("");
                    edtNickName.setText("");
                }else{
                    Toast.makeText(MainActivity.this, "Please enter the records first", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Consultamos los registros ordenados por nombre
                String URL = "content://com.example.rodrigo.customcontentprovider.contentprovider/nicknames";
                Uri friends = Uri.parse(URL);
                Cursor cursor = getContentResolver().query(friends,null,null,null,"name");

                String result = "Content providers results: ";

                if(!cursor.moveToFirst()){
                    Toast.makeText(MainActivity.this, "No content yet", Toast.LENGTH_SHORT).show();
                }else{
                    do {
                        result = result + "\n"
                                 + cursor.getString(cursor.getColumnIndex(CustomContentProvider.NAME))
                                 + " has nickname: "
                                 + cursor.getString(cursor.getColumnIndex(CustomContentProvider.NICK_NAME));
                    }while (cursor.moveToNext());
                    if(!result.isEmpty()){
                        Toast.makeText(MainActivity.this, "" + result, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(MainActivity.this, "No records present", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
