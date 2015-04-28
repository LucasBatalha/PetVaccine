package com.petvaccine.app.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.petvaccine.app.R;

public class MainActivity extends ActionBarActivity implements MainFragment.Callback {

    private boolean mTwoPane;
    private Fragment showingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mTwoPane = (findViewById(R.id.main_activity_second_fragment_container) != null);
        if (!mTwoPane) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_adicionar_pet:
                startActivity(new Intent(this, PetAdicionaActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPetSelecionado(Uri contentUri) {
        if (mTwoPane) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(PetDetalhesFragment.ARG_PET_URI, contentUri);

            showingFragment = new PetDetalhesFragment();
            showingFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_second_fragment_container, showingFragment).commit();
        } else {
            Intent intent = new Intent(this, PetDetalhesActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onPetExcluido() {
        if (mTwoPane) {
            // Remover o fragment de detalhes
            getSupportFragmentManager().beginTransaction().detach(showingFragment).commit();
        }
    }

}
