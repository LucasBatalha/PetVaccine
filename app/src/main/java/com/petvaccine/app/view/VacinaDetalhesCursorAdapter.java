package com.petvaccine.app.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.petvaccine.app.R;

import java.util.Date;

public class VacinaDetalhesCursorAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView text;
        public final ImageView button;

        public ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.vacina_detalhes_item_text);
            button = (ImageView) view.findViewById(R.id.vacina_detalhes_item_button);
        }
    }

    public VacinaDetalhesCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.vacina_detalhes_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int doseNum = cursor.getInt(VacinaDetalhesFragment.CURSOR_LISTA_COL_VACINA_DOSE_NUM_DOSE);
        long doseDataPrevisaoLong = cursor.getLong(VacinaDetalhesFragment.CURSOR_LISTA_COL_VACINAPET_DATA_PREVISAO);
        String doseDataPrevisao = DateFormat.getDateFormat(context).format(new Date(doseDataPrevisaoLong));
        String doseDataAplicacao;

        try {
            long doseDataAplicacaoLong = cursor.getLong(VacinaDetalhesFragment.CURSOR_LISTA_COL_VACINAPET_DATA_APLICACAO);
            doseDataAplicacao = (doseDataAplicacaoLong <= 0) ? null : DateFormat.getDateFormat(context).format(new Date(doseDataAplicacaoLong));
        } catch (Exception ex) {
            doseDataAplicacao = null;
        }

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (doseDataAplicacao == null) {
            if (doseDataPrevisaoLong > System.currentTimeMillis()) { // Data de previsão para os dias de amanhã em diante
                viewHolder.text.setText(String.format(context.getString(R.string.vacina_detalhes_item_text), doseNum, doseDataPrevisao));
                viewHolder.text.setTextColor(context.getResources().getColor(R.color.app_preto));
                viewHolder.button.setVisibility(View.INVISIBLE);
            } else { // Esta dose da vacina está atrasada!
                viewHolder.text.setText(String.format(context.getString(R.string.vacina_detalhes_item_text), doseNum, doseDataPrevisao));
                viewHolder.text.setTextColor(context.getResources().getColor(R.color.app_vermelho));
                viewHolder.button.setVisibility(View.VISIBLE);
            }
        } else { // Dose já aplicada.
            viewHolder.text.setText(String.format(context.getString(R.string.vacina_detalhes_item_text), doseNum, doseDataAplicacao));
            viewHolder.text.setTextColor(context.getResources().getColor(R.color.app_verde));
            viewHolder.button.setVisibility(View.INVISIBLE);
        }
    }

}
