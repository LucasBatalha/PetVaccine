package com.petvaccine.app.view;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.petvaccine.app.R;

public class PetAdicionaActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_adiciona_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PetAdicionaFragment()).commit();
        }
    }

}