package com.petvaccine.app.view;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.petvaccine.app.R;

public class PetDetalhesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pet_detalhes_activity);
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PetDetalhesFragment.ARG_PET_URI, getIntent().getData());

            Fragment fragment = new PetDetalhesFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

}
