package com.petvaccine.app.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract.*;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String [] CURSOR_COLUNAS = {PetEntry.TABELA + "." + PetEntry._ID, PetEntry.COL_NOME};
    public static final int CURSOR_COL_PET_ID = 0;
    public static final int CURSOR_COL_NOME = 1;

    private static final int LOADER_ID = 0;
    private static final String POSICAO_LISTA = "posicao_lista";

    private int mPosicaoLista = ListView.INVALID_POSITION;
    private MainCursorAdapter mMainCursorAdapter;
    private ListView mListaPets;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        mListaPets = (ListView) rootView.findViewById(R.id.lista_pets);

        View textListaVazia = rootView.findViewById(R.id.lista_pets_vazia);
        mListaPets.setEmptyView(textListaVazia);

        mMainCursorAdapter = new MainCursorAdapter(getActivity(), null, 0);
        mListaPets.setAdapter(mMainCursorAdapter);

        textListaVazia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PetAdicionaActivity.class));
            }
        });

        mListaPets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mPosicaoLista = pos;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(pos);
                if (cursor != null) {
                    ((Callback) getActivity()).onPetSelecionado(PetEntry.buildUri(cursor.getLong(CURSOR_COL_PET_ID)));
                }
            }
        });

        mListaPets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // Exibir diálogo: "Excluir pet? Sim/Não"
                final Cursor cursor = (Cursor) adapterView.getItemAtPosition(pos);
                if (cursor != null) {
                    final String nomePet = cursor.getString(CURSOR_COL_NOME);

                    DialogInterface.OnClickListener dialogConfirmClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Activity activity = getActivity();
                            activity.getContentResolver().delete(PetEntry.buildUri(cursor.getLong(CURSOR_COL_PET_ID)), PetEntry.TABELA + "." + PetEntry._ID + " = ?", null);
                            activity.deleteFile(nomePet);
                            ((Callback) activity).onPetExcluido();
                        }
                    };

                    (new AlertDialog.Builder(getActivity())).setMessage(String.format(getString(R.string.main_pergunta_excluir_pet), nomePet))
                            .setPositiveButton(getString(R.string.main_excluir_sim), dialogConfirmClickListener).setNegativeButton(getString(R.string.main_excluir_nao), null).show();
                }

                return true;
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
        String sortOrder = PetEntry.COL_NOME + " ASC";
        Uri uri = PetEntry.buildUri();

        return new CursorLoader(getActivity(), uri, CURSOR_COLUNAS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMainCursorAdapter.swapCursor(data);
        if (mPosicaoLista != ListView.INVALID_POSITION) {
            mListaPets.smoothScrollToPosition(mPosicaoLista);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMainCursorAdapter.swapCursor(null);
    }

    /**
     * Toda Activity que contém este Fragment deve implementar esta interface.
     */
    public interface Callback {
        /** Método chamado quando um pet é selecionado na lista. */
        public void onPetSelecionado(Uri contentUri);

        /** Método chamado quando um pet é excluído pelo usuário. */
        public void onPetExcluido();
    }

}
