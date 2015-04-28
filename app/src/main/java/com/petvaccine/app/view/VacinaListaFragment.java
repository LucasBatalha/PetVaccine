package com.petvaccine.app.view;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract;

public class VacinaListaFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_PET_NOME = "pet_nome";
    public static final String ARG_PET_URI = "pet_uri";

    public static final String [] CURSOR_COLUNAS = {PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry._ID,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_PET_ID,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_PREVISAO,
            PvContract.VacinaEntry.TABELA + "." + PvContract.VacinaEntry.COL_NOME,
            PvContract.VacinaEntry.TABELA + "." + PvContract.VacinaEntry.COL_INFO_LINK,
            PvContract.VacinaEntry.TABELA + "." + PvContract.VacinaEntry._ID};
    public static final int CURSOR_COL_VACINAPET_ID = 0;
    public static final int CURSOR_COL_VACINAPET_PET_ID = 1;
    public static final int CURSOR_COL_VACINAPET_DATA_PREVISAO = 2;
    public static final int CURSOR_COL_VACINA_NOME = 3;
    public static final int CURSOR_COL_VACINA_INFO_LINK = 4;
    public static final int CURSOR_COL_VACINA_ID = 5;

    private static final int LOADER_ID = 0;
    private static final String POSICAO_LISTA = "posicao_lista";

    private int mPosicaoLista = ListView.INVALID_POSITION;
    private VacinaListaCursorAdapter mListaVacinasCursorAdapter;
    private Uri mPetUri;
    private ListView mListaVacinas;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vacina_lista_fragment, container, false);
        mListaVacinas = (ListView) rootView.findViewById(R.id.vacina_lista_lista);

        mPetUri = getArguments().getParcelable(VacinaListaFragment.ARG_PET_URI);

        mListaVacinasCursorAdapter = new VacinaListaCursorAdapter(getActivity(), null, 0);
        mListaVacinas.setAdapter(mListaVacinasCursorAdapter);

        mListaVacinas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mPosicaoLista = pos;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(pos);
                if (cursor != null) {
                    String vacinaNome = cursor.getString(CURSOR_COL_VACINA_NOME);
                    String vacinaLink = cursor.getString(CURSOR_COL_VACINA_INFO_LINK);
                    long vacinaId = cursor.getLong(CURSOR_COL_VACINA_ID);
                    long petId = cursor.getLong(CURSOR_COL_VACINAPET_PET_ID);
                    Uri uri = PvContract.VacinaEntry.buildUri(vacinaId, petId, vacinaNome, vacinaLink);
                    ((Callback) getActivity()).onVacinaSelecionada(uri);
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(POSICAO_LISTA)) {
            mPosicaoLista = savedInstanceState.getInt(POSICAO_LISTA);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosicaoLista != ListView.INVALID_POSITION) {
            outState.putInt(POSICAO_LISTA, mPosicaoLista);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (mPetUri == null) {
            return null;
        }

        long petId = PvContract.PetEntry.getIdFromUri(mPetUri);
        Uri uri = PvContract.VacinaPetEntry.buildUriWithPetIdRetornaVacinas(petId);

        String sortOrder = PvContract.VacinaPetEntry.COL_DATA_PREVISAO + " ASC";
        String selection = PvContract.VacinaPetEntry.COL_DATA_PREVISAO + " >= ? OR " + PvContract.VacinaPetEntry.COL_DATA_APLICACAO + " IS NULL ";
        String[] selectionArgs = {Long.toString(System.currentTimeMillis())};

        return new CursorLoader(getActivity(), uri, CURSOR_COLUNAS, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListaVacinasCursorAdapter.swapCursor(data);
        if (mPosicaoLista != ListView.INVALID_POSITION) {
            mListaVacinas.smoothScrollToPosition(mPosicaoLista);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListaVacinasCursorAdapter.swapCursor(null);
    }

    /**
     * Toda Activity que contém este Fragment deve implementar esta interface.
     */
    public interface Callback {
        /** Método chamado quando um item da lista é selecionado. */
        public void onVacinaSelecionada(Uri contentUri);
    }

}
