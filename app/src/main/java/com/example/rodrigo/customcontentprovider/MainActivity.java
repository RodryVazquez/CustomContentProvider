package com.example.rodrigo.customcontentprovider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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

    }
}
