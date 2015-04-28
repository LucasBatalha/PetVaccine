package com.petvaccine.app.view;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract;
import com.petvaccine.app.util.Utils;

import java.text.ParseException;
import java.util.Date;

public class VacinaAplicacaoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_PET_NOME = "pet_nome";
    public static final String ARG_VACINA_NOME = "vacina_nome";
    public static final String ARG_VACINA_NUM_DOSE = "vacina_num_dose";
    public static final String ARG_VACINA_PET_URI = "vacina_pet_uri";

    public static final String [] CURSOR_COLUNAS = {
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry._ID,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_PREVISAO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_DATA_APLICACAO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_LABORATORIO,
            PvContract.VacinaPetEntry.TABELA + "." + PvContract.VacinaPetEntry.COL_LOTE};
    public static final int CURSOR_COL_VACINAPET_ID = 0;
    public static final int CURSOR_COL_VACINAPET_DATA_PREVISAO = 1;
    public static final int CURSOR_COL_VACINAPET_DATA_APLICACAO = 2;
    public static final int CURSOR_COL_VACINAPET_LABORATORIO = 3;
    public static final int CURSOR_COL_VACINAPET_LOTE = 4;

    private static final int LOADER_ID = 0;

    private long mVacinaPetId;
    private Uri mVacinaUri;

    private Button mButtonOk;
    private CheckBox mCheckSeAplicou;
    private EditText mEditCampoDataAplicacao;
    private EditText mEditCampoLaboratorio;
    private EditText mEditCampoLote;
    private TextView mTextDataPrevisao;
    private TextView mTextNumDose;
    private TextView mTextTitulo;
    private View mViewSeAplicou;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.vacina_aplicacao_fragment, container, false);

        mButtonOk = (Button) rootView.findViewById(R.id.vacina_aplicacao_botao_ok);
        mCheckSeAplicou = (CheckBox) rootView.findViewById(R.id.vacina_aplicacao_check_se_aplicou);
        mEditCampoDataAplicacao = (EditText) rootView.findViewById(R.id.vacina_aplicacao_campo_data);
        mEditCampoLaboratorio = (EditText) rootView.findViewById(R.id.vacina_aplicacao_campo_laboratorio);
        mEditCampoLote = (EditText) rootView.findViewById(R.id.vacina_aplicacao_campo_lote);
        mTextDataPrevisao = (TextView) rootView.findViewById(R.id.vacina_aplicacao_text_data_previsao);
        mTextNumDose = (TextView) rootView.findViewById(R.id.vacina_aplicacao_text_numdose);
        mTextTitulo = (TextView) rootView.findViewById(R.id.vacina_aplicacao_titulo);
        mViewSeAplicou = rootView.findViewById(R.id.vacina_aplicacao_layout_se_aplicou_true);

        mVacinaUri = getArguments().getParcelable(VacinaAplicacaoFragment.ARG_VACINA_PET_URI);
        int vacinaNumDose = getArguments().getInt(VacinaAplicacaoFragment.ARG_VACINA_NUM_DOSE);
        String nomePet = getArguments().getString(VacinaAplicacaoFragment.ARG_PET_NOME);
        String nomeVacina = getArguments().getString(VacinaAplicacaoFragment.ARG_VACINA_NOME);

        mTextTitulo.setText(String.format(getString(R.string.vacina_detalhes_titulo), nomeVacina, nomePet));
        mTextNumDose.setText(String.format(getString(R.string.vacina_aplicacao_numdose), vacinaNumDose));

        mCheckSeAplicou.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    rootView.findViewById(R.id.vacina_aplicacao_layout_se_aplicou_true).setVisibility(View.VISIBLE);
                } else {
                    rootView.findViewById(R.id.vacina_aplicacao_layout_se_aplicou_true).setVisibility(View.GONE);
                }
            }
        });

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarAplicacaoVacina();
            }
        });

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mVacinaUri == null) {
            return null;
        }

        return new CursorLoader(getActivity(), mVacinaUri, CURSOR_COLUNAS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mVacinaPetId = data.getLong(CURSOR_COL_VACINAPET_ID);

            String dataPrevisao = null;
            try {
                long dataPrevisaoLong = data.getLong(CURSOR_COL_VACINAPET_DATA_PREVISAO);
                if (dataPrevisaoLong > 0) {
                    dataPrevisao = DateFormat.getDateFormat(getActivity()).format(new Date(dataPrevisaoLong));
                }
            } catch (Exception ex) {}

            String dataAplicacao = null;
            try {
                long dataAplicacaoLong = data.getLong(CURSOR_COL_VACINAPET_DATA_APLICACAO);
                if (dataAplicacaoLong > 0) {
                    dataAplicacao = DateFormat.getDateFormat(getActivity()).format(new Date(dataAplicacaoLong));
                }
            } catch (Exception ex) {}

            mTextDataPrevisao.setText(String.format(getString(R.string.vacina_aplicacao_data_previsao), dataPrevisao));

            if (dataAplicacao == null) {
                // Dose não foi aplicada
                mCheckSeAplicou.setChecked(false);
                mCheckSeAplicou.setEnabled(true);
                mButtonOk.setVisibility(View.VISIBLE);
                mViewSeAplicou.setVisibility(View.GONE);
            } else {
                // Dose já aplicada: exibir campos mas não permitir alterar
                mCheckSeAplicou.setChecked(true);
                mCheckSeAplicou.setEnabled(false);
                mButtonOk.setVisibility(View.GONE);
                mViewSeAplicou.setVisibility(View.VISIBLE);
                mEditCampoDataAplicacao.setText(dataAplicacao);
                mEditCampoLaboratorio.setText(data.getString(CURSOR_COL_VACINAPET_LABORATORIO));
                mEditCampoLote.setText(data.getString(CURSOR_COL_VACINAPET_LOTE));
                mEditCampoDataAplicacao.setEnabled(false);
                mEditCampoLaboratorio.setEnabled(false);
                mEditCampoLote.setEnabled(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

    /**
     * Exibe pergunta ao usuário e, caso a resposta seja positiva, executa UPDATE na linha
     * correspondente a esta aplicação (na tabela vacina_pet) e adiciona os itens restantes.
     */
    private void adicionarAplicacaoVacina() {
        long auxDataAplicacao;
        String nomeVacina = getArguments().getString(VacinaAplicacaoFragment.ARG_VACINA_NOME);
        String auxCampoDataAplicacao = mEditCampoDataAplicacao.getText().toString();

        final String laboratorio = mEditCampoLaboratorio.getText().toString();
        final String lote = mEditCampoLote.getText().toString();

        try {
            // Transformando data de nascimento em long
            auxDataAplicacao = DateFormat.getDateFormat(getActivity()).parse(auxCampoDataAplicacao).getTime();
        } catch (ParseException ex) {
            auxDataAplicacao = 0;
        }

        final long dataAplicacao = auxDataAplicacao;

        if (dataAplicacao <= 0 || dataAplicacao > System.currentTimeMillis()) {
            Utils.exibeMensagemAtencao(getString(R.string.erro_data_invalida), getActivity());
        } else {
            DialogInterface.OnClickListener dialogConfirmClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Executa UPDATE em vacina_pet, alterando os campos que o usuário acaba de informar
                    Uri uri = PvContract.VacinaPetEntry.buildUri(mVacinaPetId);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(PvContract.VacinaPetEntry.COL_DATA_APLICACAO, dataAplicacao);
                    contentValues.put(PvContract.VacinaPetEntry.COL_LABORATORIO, laboratorio);
                    contentValues.put(PvContract.VacinaPetEntry.COL_LOTE, lote);

                    getActivity().getContentResolver().update(uri, contentValues, null, null);
                }
            };

            String pergunta = String.format(getString(R.string.vacina_aplicacao_pergunta), nomeVacina, auxCampoDataAplicacao, laboratorio, lote);
            (new AlertDialog.Builder(getActivity())).setMessage(pergunta)
                    .setPositiveButton(getString(R.string.vacina_aplicacao_pergunta_sim), dialogConfirmClickListener)
                    .setNegativeButton(getString(R.string.vacina_aplicacao_pergunta_nao), null).show();
        }
    }

}