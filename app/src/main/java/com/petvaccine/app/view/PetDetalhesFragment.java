package com.petvaccine.app.view;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract;

import java.io.FileNotFoundException;
import java.util.Date;

public class PetDetalhesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    public static final String ARG_PET_URI = "pet_uri";

    public static final String [] CURSOR_COLUNAS = {PvContract.PetEntry.TABELA + "." + PvContract.PetEntry._ID, PvContract.PetEntry.COL_NOME, PvContract.PetEntry.COL_NASCIMENTO, PvContract.PetEntry.COL_RACA};
    public static final int CURSOR_COL_PET_ID = 0;
    public static final int CURSOR_COL_NOME = 1;
    public static final int CURSOR_COL_NASCIMENTO = 2;
    public static final int CURSOR_COL_RACA = 3;

    private static final int LOADER_ID = 0;

    private String mPetNome = null;
    private Uri mPetUri;
    private Button mBotaoVacinas;
    private EditText mCampoNascimento;
    private EditText mCampoNome;
    private EditText mCampoRaca;
    private ImageView mImagem;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pet_detalhes_fragment, container, false);
        mBotaoVacinas = (Button) rootView.findViewById(R.id.pet_detalhes_botao_vacinas_proximas);
        mCampoNascimento = (EditText) rootView.findViewById(R.id.pet_detalhes_nascimento);
        mCampoNome = (EditText) rootView.findViewById(R.id.pet_detalhes_nome);
        mCampoRaca = (EditText) rootView.findViewById(R.id.pet_detalhes_raca);
        mImagem = (ImageView) rootView.findViewById(R.id.pet_detalhes_imagem);

        mPetUri = getArguments().getParcelable(PetDetalhesFragment.ARG_PET_URI);

        mBotaoVacinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPetNome != null) {
                    Intent intent = new Intent(getActivity(), VacinaListaActivity.class);
                    intent.setData(mPetUri);
                    intent.putExtra(VacinaListaFragment.ARG_PET_NOME, mPetNome);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mPetUri != null) {
            return new CursorLoader(getActivity(), mPetUri, CURSOR_COLUNAS, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mPetNome = data.getString(CURSOR_COL_NOME);

            mCampoNome.setText(mPetNome);
            mCampoRaca.setText(data.getString(CURSOR_COL_RACA));
            mCampoNascimento.setText(DateFormat.getDateFormat(getActivity()).format(new Date(data.getLong(CURSOR_COL_NASCIMENTO))));

            try {
                mImagem.setImageBitmap(BitmapFactory.decodeStream(getActivity().openFileInput(mPetNome)));
            } catch (FileNotFoundException ex) {
                mImagem.setImageResource(R.mipmap.image_pet_no_photo);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
