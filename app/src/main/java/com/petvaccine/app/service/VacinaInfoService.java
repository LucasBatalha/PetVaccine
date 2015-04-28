package com.petvaccine.app.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.petvaccine.app.data.PvContract;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VacinaInfoService extends IntentService {

    public static final String INTENT_KEY_VACINA_URI = "vacinaUri";

    private static final String LOG_TAG = VacinaInfoService.class.getSimpleName();
    private static final String URL_PREFIX = "http://inf.ufg.br/~cc107736/vacinas/";

    public VacinaInfoService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        Uri vacinaUri = intent.getExtras().getParcelable(INTENT_KEY_VACINA_URI);
        String vacinaInfoLink = PvContract.VacinaEntry.getVacinaInfoLinkFromUri(vacinaUri);
        String urlString = URL_PREFIX + vacinaInfoLink;

        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() >= 300) {
                // Código HTTP maior que 300 significa erro!
                Log.e(LOG_TAG, "Erro ao buscar informação de vacina pelo link: '" + urlString + "' - CÓDIGO HTTP: " + urlConnection.getResponseCode());
                return;
            }

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.e(LOG_TAG, "Erro ao buscar informação de vacina pelo link: '" + urlString + "' - INPUT STREAM VAZIO");
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String aux;
            while ((aux = reader.readLine()) != null) {
                buffer.append(aux);
            }

            if (buffer.length() == 0) {
                Log.e(LOG_TAG, "Erro ao buscar informação de vacina pelo link: '" + urlString + "' - BUFFER VAZIO");
                return;
            }

            addVacinaInfo(vacinaUri, buffer.toString());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Erro ao buscar informação de vacina pelo link: '" + urlString + "'", ex);
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception ex) {}

            try {
                reader.close();
            } catch (Exception ex) {}
        }
    }

    /**
     * Adiciona ao Banco de Dados a informação da vacina que acaba de ser baixada.
     *
     * @param vacinaUri Uri com ID da tupla na tabela vacina.
     * @param vacinaInfo Nova informação da respectiva vacina.
     */
    private void addVacinaInfo(Uri vacinaUri, String vacinaInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PvContract.VacinaEntry.COL_INFO, vacinaInfo);

        this.getContentResolver().update(vacinaUri, contentValues, null, null);
    }

}