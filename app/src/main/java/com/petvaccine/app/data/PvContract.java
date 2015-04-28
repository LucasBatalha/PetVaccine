package com.petvaccine.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class PvContract {

    public static final String CONTENT_AUTHORITY = "com.petvaccine.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PET = "pet";
    public static final String PATH_VACINA = "vacina";
    public static final String PATH_VACINADOSE = "vacina_dose";
    public static final String PATH_VACINAPET = "vacina_pet";

    /** Define o conteúdo da tabela de pets. */
    public static final class PetEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PET).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PET;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PET;

        public static final String TABELA = "pet";

        public static final String COL_NOME = "nome";
        public static final String COL_NASCIMENTO = "nascimento";
        public static final String COL_RACA = "raca";

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
    }

    /** Define o conteúdo da tabela de vacinas. */
    public static final class VacinaEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACINA).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINA;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINA;

        public static final String TABELA = "vacina";

        public static final String COL_NOME = "nome";
        public static final String COL_INFO_LINK = "info_link";
        public static final String COL_INFO = "info";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildUri(long id, long petId, String vacinaNome, String vacinaInfoLink) {
            return CONTENT_URI.buildUpon().appendPath("" + petId).appendPath(vacinaNome).appendPath(vacinaInfoLink).appendPath("" + id).build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }

        public static long getPetIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static String getVacinaNomeFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getVacinaInfoLinkFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }

    /** Define o conteúdo da tabela de doses de vacinas. */
    public static final class VacinaDoseEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACINADOSE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINADOSE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINADOSE;

        public static final String TABELA = "vacina_dose";

        public static final String COL_VACINA_ID = "vacina_id";
        public static final String COL_NUM_DOSE = "num_dose";
        public static final String COL_INTERVALO_DIAS = "intervalo_dias"; //Intervalo entre esta dose e a anterior (em dias)

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /** Define o conteúdo da tabela do relacionamento entre Vacinas e Pets (da ordem de n para n). */
    public static final class VacinaPetEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VACINAPET).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINAPET;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VACINAPET;

        public static final String TABELA = "vacina_pet";

        public static final String COL_VACINA_DOSE_ID = "vacina_dose_id";
        public static final String COL_PET_ID = "pet_id";
        public static final String COL_DATA_PREVISAO = "data_previsao";
        public static final String COL_DATA_APLICACAO = "data_aplicacao"; // A data da aplicação é nula se a dose não tiver sido aplicada
        public static final String COL_LABORATORIO = "laboratorio";
        public static final String COL_LOTE = "lote";

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildUriWithPetIdRetornaVacinas(long petId) {
            return CONTENT_URI.buildUpon().appendPath("0").appendPath("" + petId).appendPath("0").build();
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getPetIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

}
