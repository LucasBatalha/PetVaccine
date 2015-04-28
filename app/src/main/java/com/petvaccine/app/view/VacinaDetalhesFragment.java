package com.petvaccine.app.view;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract;
import com.petvaccine.app.service.VacinaInfoService;

public class VacinaDetalhesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_PET_NOME = "pet_nome";
    public static final String ARG_VACINA_URI = "vacina_uri";

    public static final String [] CURSOR_INFO_COLUNAS = {
        PvContract.VacinaEntry.TABELA + "." + PvContract.VacinaEntry._ID,
        PvContract.VacinaEntry.TABELA + "." + PvContract.VacinaEntry.COL_INFO};
    public static final int CURSOR_INFO_COL_VACINA_ID = 0;
    public static final int CURSOR_INFO_COL_VACINA_INFO = 1;

    public static final String [] CURSOR_LISTA_COLUNAS = {
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry._ID,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_PREVISAO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_APLICACAO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_LABORATORIO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_LOTE,
            PvContract.VacinaDoseEntry.TABELA + "." + PvContract.VacinaDoseEntry.COL_NUM_DOSE};
    public static final int CURSOR_LISTA_COL_VACINAPET_ID = 0;
    public static final int CURSOR_LISTA_COL_VACINAPET_DATA_PREVISAO = 1;
    public static final int CURSOR_LISTA_COL_VACINAPET_DATA_APLICACAO = 2;
    public static final int CURSOR_LISTA_COL_VACINAPET_LABORATORIO = 3;
    public static final int CURSOR_LISTA_COL_VACINAPET_LOTE = 4;
    public static final int CURSOR_LISTA_COL_VACINA_DOSE_NUM_DOSE = 5;

    private static final int LOADER_ID_INFO = 0;
    private static final int LOADER_ID_LISTA = 1;
    private static final String POSICAO_LISTA = "posicao_lista";

    private int mPosicaoLista = ListView.INVALID_POSITION;
    private String mNomePet;
    private String mVacinaNome;
    private VacinaDetalhesCursorAdapter mListaDosesCursorAdapter;
    private Uri mVacinaUri;
    private ListView mListaDoses;
    private ProgressBar mProgressBarInfo;
    private TextView mVacinaInfo;
    private TextView mTitulo;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID_LISTA, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vacina_detalhes_fragment, container, false);
        mListaDoses = (ListView) rootView.findViewById(R.id.vacina_detalhes_lista_doses);
        mProgressBarInfo = (ProgressBar) rootView.findViewById(R.id.vacina_detalhes_progress_bar);
        mTitulo = (TextView) rootView.findViewById(R.id.vacina_detalhes_titulo);
        mVacinaInfo = (TextView) rootView.findViewById(R.id.vacina_detalhes_info);
        mVacinaInfo.setMovementMethod(new ScrollingMovementMethod()); // Atribui movimento à barra de rolagem

        mVacinaUri = getArguments().getParcelable(VacinaDetalhesFragment.ARG_VACINA_URI);
        mNomePet = getArguments().getString(VacinaDetalhesFragment.ARG_PET_NOME);
        mVacinaNome = PvContract.VacinaEntry.getVacinaNomeFromUri(mVacinaUri);

        mListaDosesCursorAdapter = new VacinaDetalhesCursorAdapter(getActivity(), null, 0);
        mListaDoses.setAdapter(mListaDosesCursorAdapter);

        mListaDoses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mPosicaoLista = pos;
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(pos);
                if (cursor != null) {
                    Uri uri = PvContract.VacinaPetEntry.buildUri(cursor.getLong(CURSOR_LISTA_COL_VACINAPET_ID));
                    int numDose = cursor.getInt(CURSOR_LISTA_COL_VACINA_DOSE_NUM_DOSE);
                    ((Callback) getActivity()).onVacinaDoseSelecionada(uri, mNomePet, mVacinaNome, numDose);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID_LISTA) {
            if (mVacinaUri == null) {
                return null;
            }

            String sortOrder = PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_PREVISAO;
            return new CursorLoader(getActivity(), mVacinaUri, CURSOR_LISTA_COLUNAS, null, null, sortOrder);
        } else if (id == LOADER_ID_INFO) {
            return new CursorLoader(getActivity(), mVacinaUri, CURSOR_INFO_COLUNAS, null, null, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_LISTA) {
            if (data != null && data.moveToFirst()) {
                mListaDosesCursorAdapter.swapCursor(data); // Preenche lista de doses
                if (mPosicaoLista != ListView.INVALID_POSITION) {
                    mListaDoses.smoothScrollToPosition(mPosicaoLista);
                }

                mTitulo.setText(String.format(getString(R.string.vacina_detalhes_titulo), mVacinaNome, mNomePet));
                getLoaderManager().initLoader(LOADER_ID_INFO, null, this);
            }
        } else if (loader.getId() == LOADER_ID_INFO) {
            if (data != null && data.moveToFirst()) {
                String info = data.getString(CURSOR_INFO_COL_VACINA_INFO);
                if (info == null || "".equals(info)) {
                    mProgressBarInfo.setVisibility(View.VISIBLE);
                    // Inicia o Service responsável por baixar informações da vacina
                    Intent intent = new Intent(getActivity(), VacinaInfoService.class);
                    intent.putExtra(VacinaInfoService.INTENT_KEY_VACINA_URI, mVacinaUri);
                    getActivity().startService(intent);
                } else {
                    mProgressBarInfo.setVisibility(View.GONE);
                    mVacinaInfo.setText(Html.fromHtml(info));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ID_LISTA) {
            mListaDosesCursorAdapter.swapCursor(null);
        }
    }

    /**
     * Toda Activity que contém este Fragment deve implementar esta interface.
     */
    public interface Callback {
        /** Método chamado quando um item da lista é selecionado. */
        public void onVacinaDoseSelecionada(Uri contentUri, String nomePet, String nomeVacina, int numDose);
    }

}
