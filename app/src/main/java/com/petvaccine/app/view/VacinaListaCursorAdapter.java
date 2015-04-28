package com.petvaccine.app.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.petvaccine.app.R;

import java.util.Date;

public class VacinaListaCursorAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView text;

        public ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.vacina_lista_item_text);
        }
    }

    public VacinaListaCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.vacina_lista_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long vacinaDataPrevisaoLong = cursor.getLong(VacinaListaFragment.CURSOR_COL_VACINAPET_DATA_PREVISAO);
        String vacinaDataPrevisao = DateFormat.getDateFormat(context).format(new Date(vacinaDataPrevisaoLong));
        String vacinaNome = cursor.getString(VacinaListaFragment.CURSOR_COL_VACINA_NOME);

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.text.setText(String.format(context.getString(R.string.vacina_lista_item), vacinaDataPrevisao, vacinaNome));

        if (vacinaDataPrevisaoLong < System.currentTimeMillis()) {
            // Esta dose da vacina está atrasada!
            viewHolder.text.setTextColor(context.getResources().getColor(R.color.app_vermelho));
        } else {
            viewHolder.text.setTextColor(context.getResources().getColor(R.color.app_preto));
        }
    }

}
