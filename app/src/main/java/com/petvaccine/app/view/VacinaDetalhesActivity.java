package com.petvaccine.app.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.petvaccine.app.R;

public class VacinaDetalhesActivity extends ActionBarActivity implements VacinaDetalhesFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vacina_detalhes_activity);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(VacinaDetalhesFragment.ARG_VACINA_URI, getIntent().getData());
            bundle.putString(VacinaDetalhesFragment.ARG_PET_NOME, getIntent().getExtras().getString(VacinaDetalhesFragment.ARG_PET_NOME));

            Fragment fragment = new VacinaDetalhesFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onVacinaDoseSelecionada(Uri contentUri, String nomePet, String nomeVacina, int numDose) {
        // Esta classe só existirá quando a visão NÃO for composta por dois painéis (mTwoPane = false)
        Intent intent = new Intent(this, VacinaAplicacaoActivity.class).setData(contentUri);
        intent.putExtra(VacinaAplicacaoFragment.ARG_PET_NOME, nomePet);
        intent.putExtra(VacinaAplicacaoFragment.ARG_VACINA_NOME, nomeVacina);
        intent.putExtra(VacinaAplicacaoFragment.ARG_VACINA_NUM_DOSE, numDose);
        startActivity(intent);
    }

}
