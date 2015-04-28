package com.petvaccine.app.view;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.petvaccine.app.R;

public class VacinaAplicacaoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vacina_aplicacao_activity);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(VacinaAplicacaoFragment.ARG_VACINA_PET_URI, getIntent().getData());
            bundle.putString(VacinaAplicacaoFragment.ARG_PET_NOME, getIntent().getExtras().getString(VacinaAplicacaoFragment.ARG_PET_NOME));
            bundle.putString(VacinaAplicacaoFragment.ARG_VACINA_NOME, getIntent().getExtras().getString(VacinaAplicacaoFragment.ARG_VACINA_NOME));
            bundle.putInt(VacinaAplicacaoFragment.ARG_VACINA_NUM_DOSE, getIntent().getExtras().getInt(VacinaAplicacaoFragment.ARG_VACINA_NUM_DOSE));

            Fragment fragment = new VacinaAplicacaoFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

}
