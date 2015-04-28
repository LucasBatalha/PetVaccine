package com.petvaccine.app.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.petvaccine.app.R;

public class VacinaListaActivity extends ActionBarActivity implements VacinaListaFragment.Callback, VacinaDetalhesFragment.Callback {

    private static final String KEY_EXIBINDO_VACINA_APLICACAO_FRAGMENT = "evaf";
    private static final String KEY_PET_NOME = "ptnm";
    private static final String KEY_URI_VACINA_DETALHES_FRAGMENT = "uvdf";

    private boolean mTwoPane;
    private String mPetNome;

    private boolean mExibindoVacinaAplicacaoFragment = false;
    private Uri mUltimoUriVacinaDetalhesFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vacina_lista_activity);

        mTwoPane = (findViewById(R.id.vacina_lista_activity_second_fragment_container) != null);
        mPetNome = getIntent().getExtras().getString(VacinaListaFragment.ARG_PET_NOME);

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(VacinaListaFragment.ARG_PET_URI, getIntent().getData());
            bundle.putString(VacinaListaFragment.ARG_PET_NOME, mPetNome);

            Fragment fragment = new VacinaListaFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            mExibindoVacinaAplicacaoFragment = false;
        } else if (savedInstanceState.containsKey(KEY_EXIBINDO_VACINA_APLICACAO_FRAGMENT)) {
            mExibindoVacinaAplicacaoFragment = savedInstanceState.getBoolean(KEY_EXIBINDO_VACINA_APLICACAO_FRAGMENT);
            mUltimoUriVacinaDetalhesFragment = savedInstanceState.getParcelable(KEY_URI_VACINA_DETALHES_FRAGMENT);
            mPetNome = savedInstanceState.getString(KEY_PET_NOME);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(KEY_EXIBINDO_VACINA_APLICACAO_FRAGMENT, mExibindoVacinaAplicacaoFragment);
        outState.putParcelable(KEY_URI_VACINA_DETALHES_FRAGMENT, mUltimoUriVacinaDetalhesFragment);
        outState.putString(KEY_PET_NOME, mPetNome);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mExibindoVacinaAplicacaoFragment) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(VacinaDetalhesFragment.ARG_VACINA_URI, mUltimoUriVacinaDetalhesFragment);
            bundle.putString(VacinaDetalhesFragment.ARG_PET_NOME, mPetNome);

            Fragment fragment = new VacinaDetalhesFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.vacina_lista_activity_second_fragment_container, fragment).commit();
            mExibindoVacinaAplicacaoFragment = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onVacinaSelecionada(Uri contentUri) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(VacinaDetalhesFragment.ARG_VACINA_URI, contentUri);
            bundle.putString(VacinaDetalhesFragment.ARG_PET_NOME, mPetNome);

            Fragment fragment = new VacinaDetalhesFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.vacina_lista_activity_second_fragment_container, fragment).commit();
            mExibindoVacinaAplicacaoFragment = false;
            mUltimoUriVacinaDetalhesFragment = contentUri;
        } else {
            Intent intent = new Intent(this, VacinaDetalhesActivity.class).setData(contentUri);
            intent.putExtra(VacinaDetalhesFragment.ARG_PET_NOME, mPetNome);
            startActivity(intent);
        }
    }

    @Override
    public void onVacinaDoseSelecionada(Uri contentUri, String nomePet, String nomeVacina, int numDose) {
        // Este método só será chamado pelo VacinaDetalhesFragment se mTwoPane=true
        Bundle bundle = new Bundle();
        bundle.putParcelable(VacinaAplicacaoFragment.ARG_VACINA_PET_URI, contentUri);
        bundle.putString(VacinaAplicacaoFragment.ARG_PET_NOME, nomePet);
        bundle.putString(VacinaAplicacaoFragment.ARG_VACINA_NOME, nomeVacina);
        bundle.putInt(VacinaAplicacaoFragment.ARG_VACINA_NUM_DOSE, numDose);

        Fragment fragment = new VacinaAplicacaoFragment();
        fragment.setArguments(bundle);

        // OBS: Não foi possível usar fragmentManagar.findFragmentByTag(tag), sempre retornava 'null'
        getSupportFragmentManager().beginTransaction().replace(R.id.vacina_lista_activity_second_fragment_container, fragment).commit();
        mExibindoVacinaAplicacaoFragment = true;
    }

}
