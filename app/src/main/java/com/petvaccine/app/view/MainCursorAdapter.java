package com.petvaccine.app.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.petvaccine.app.R;

import java.io.FileNotFoundException;

public class MainCursorAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView text;
        public final ImageView image;

        public ViewHolder(View view) {
            text = (TextView) view.findViewById(R.id.main_list_item_text);
            image = (ImageView) view.findViewById(R.id.main_list_item_imagem);
        }
    }

    public MainCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layoutId = R.layout.main_item;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String nome = cursor.getString(MainFragment.CURSOR_COL_NOME);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.text.setText(nome);

        try {
            viewHolder.image.setImageBitmap(BitmapFactory.decodeStream(context.openFileInput(nome)));
        } catch (FileNotFoundException ex) {
            viewHolder.image.setImageResource(R.mipmap.image_pet_no_photo);
        }
    }

}
