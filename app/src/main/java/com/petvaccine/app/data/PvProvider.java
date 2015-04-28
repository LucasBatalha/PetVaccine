package com.petvaccine.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.petvaccine.app.data.PvContract.*;
import com.petvaccine.app.util.Utils;

public class PvProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private PvDbHelper mOpenHelper;

    private static final int MATCH_PET = 100;
    private static final int MATCH_PET_COM_ID = 110;
    private static final int MATCH_VACINA_COM_ID_PETID_E_VACINA_NOME_E_VACINA_INFO_LINK = 200;
    private static final int MATCH_VACINA_DOSE = 300;
    private static final int MATCH_VACINA_DOSE_COM_ID = 310;
    private static final int MATCH_VACINA_PET = 400;
    private static final int MATCH_VACINA_PET_COM_ID = 410;
    private static final int MATCH_VACINA_PET_COM_PETID = 430;
    private static final int MATCH_VACINA_PET_COM_PETID_RETORNA_VACINAS = 440;

    private static final SQLiteQueryBuilder sVacinaPetJoinVacinaDoseJoinVacinaQB;
    private static final SQLiteQueryBuilder sVacinaJoinVacinaDoseJoinVacinaPetQB;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PvContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PvContract.PATH_PET, MATCH_PET);
        matcher.addURI(authority, PvContract.PATH_PET + "/#", MATCH_PET_COM_ID);
        matcher.addURI(authority, PvContract.PATH_VACINA + "/#/*/*/#", MATCH_VACINA_COM_ID_PETID_E_VACINA_NOME_E_VACINA_INFO_LINK);
        matcher.addURI(authority, PvContract.PATH_VACINADOSE, MATCH_VACINA_DOSE);
        matcher.addURI(authority, PvContract.PATH_VACINADOSE + "/#", MATCH_VACINA_DOSE_COM_ID);
        matcher.addURI(authority, PvContract.PATH_VACINAPET, MATCH_VACINA_PET);
        matcher.addURI(authority, PvContract.PATH_VACINAPET + "/#", MATCH_VACINA_PET_COM_ID);
        matcher.addURI(authority, PvContract.PATH_VACINAPET + "/#/#", MATCH_VACINA_PET_COM_PETID);
        matcher.addURI(authority, PvContract.PATH_VACINAPET + "/#/#/#", MATCH_VACINA_PET_COM_PETID_RETORNA_VACINAS);
        return matcher;
    }

    static {
        sVacinaPetJoinVacinaDoseJoinVacinaQB = new SQLiteQueryBuilder();

        // Join entre as tabelas vacina_pet, vacina_dose e vacina para buscar nome da vacina...
        // vacina_pet INNER JOIN vacina_dose ON vacina_pet.vacina_dose_id = vacina_dose._id
        // INNER JOIN vacina ON vacina_dose.vacina_id = vacina._id
        sVacinaPetJoinVacinaDoseJoinVacinaQB.setTables(
                VacinaPetEntry.TABELA + " INNER JOIN " + VacinaDoseEntry.TABELA + " ON " + VacinaPetEntry.TABELA + "."
                        + VacinaPetEntry.COL_VACINA_DOSE_ID + " = " + VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID
                        + " INNER JOIN " + VacinaEntry.TABELA + " ON " + VacinaDoseEntry.TABELA + "."
                        + VacinaDoseEntry.COL_VACINA_ID + " = " + VacinaEntry.TABELA + "." + VacinaEntry._ID);
    }

    static {
        sVacinaJoinVacinaDoseJoinVacinaPetQB = new SQLiteQueryBuilder();

        // Join entre as tabelas vacina_pet e vacina_dose...
        // vacina INNER JOIN vacina_dose ON vacina_dose.vacina_id = vacina._id
        // INNER JOIN vacina_pet ON vacina_pet.vacina_dose_id = vacina_dose._id
        sVacinaJoinVacinaDoseJoinVacinaPetQB.setTables(
                VacinaEntry.TABELA + " INNER JOIN " + VacinaDoseEntry.TABELA + " ON " + VacinaDoseEntry.TABELA
                        + "." + VacinaDoseEntry.COL_VACINA_ID + " = " + VacinaEntry.TABELA + "." + VacinaEntry._ID
                        + " INNER JOIN " + VacinaPetEntry.TABELA + " ON " + VacinaPetEntry.TABELA + "."
                        + VacinaPetEntry.COL_VACINA_DOSE_ID + " = " + VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new PvDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MATCH_PET:
                return PetEntry.CONTENT_TYPE;
            case MATCH_PET_COM_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            case MATCH_VACINA_COM_ID_PETID_E_VACINA_NOME_E_VACINA_INFO_LINK:
                return VacinaDoseEntry.CONTENT_TYPE;
            case MATCH_VACINA_DOSE:
                return VacinaDoseEntry.CONTENT_TYPE;
            case MATCH_VACINA_DOSE_COM_ID:
                return VacinaDoseEntry.CONTENT_ITEM_TYPE;
            case MATCH_VACINA_PET:
                return VacinaPetEntry.CONTENT_TYPE;
            case MATCH_VACINA_PET_COM_ID:
                return VacinaPetEntry.CONTENT_ITEM_TYPE;
            case MATCH_VACINA_PET_COM_PETID:
                return VacinaPetEntry.CONTENT_TYPE;
            case MATCH_VACINA_PET_COM_PETID_RETORNA_VACINAS:
                return VacinaPetEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("URI desconhecido: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MATCH_PET:
                retCursor = mOpenHelper.getReadableDatabase().query(PetEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_PET_COM_ID:
                selection = PetEntry.TABELA + "." + PetEntry._ID + " = ? ";
                selectionArgs = new String[]{"" + PetEntry.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(PetEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_COM_ID_PETID_E_VACINA_NOME_E_VACINA_INFO_LINK:
                selection = VacinaEntry.TABELA + "." + VacinaEntry._ID + " = ? AND " + VacinaPetEntry.TABELA + "." + VacinaPetEntry.COL_PET_ID + " = ? ";
                selectionArgs = new String[]{"" + VacinaEntry.getIdFromUri(uri), "" + VacinaEntry.getPetIdFromUri(uri)};
                retCursor = sVacinaJoinVacinaDoseJoinVacinaPetQB.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_DOSE:
                retCursor = mOpenHelper.getReadableDatabase().query(VacinaDoseEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_DOSE_COM_ID:
                selection = VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID + " = ? ";
                selectionArgs = new String[]{"" + VacinaDoseEntry.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(VacinaDoseEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_PET:
                retCursor = mOpenHelper.getReadableDatabase().query(VacinaPetEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_PET_COM_ID:
                selection = VacinaPetEntry.TABELA + "." + VacinaPetEntry._ID + " = ? ";
                selectionArgs = new String[]{"" + VacinaPetEntry.getIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(VacinaPetEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_PET_COM_PETID:
                selection = VacinaPetEntry.TABELA + "." + VacinaPetEntry.COL_PET_ID + " = ? ";
                selectionArgs = new String[]{"" + VacinaPetEntry.getPetIdFromUri(uri)};
                retCursor = mOpenHelper.getReadableDatabase().query(VacinaPetEntry.TABELA, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MATCH_VACINA_PET_COM_PETID_RETORNA_VACINAS:
                selection = VacinaPetEntry.TABELA + "." + VacinaPetEntry.COL_PET_ID + " = ? ";
                selectionArgs = new String[]{"" + VacinaPetEntry.getPetIdFromUri(uri)};
                retCursor = sVacinaPetJoinVacinaDoseJoinVacinaQB.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("URI desconhecido: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case MATCH_PET:
            case MATCH_PET_COM_ID:
                _id = db.insert(PetEntry.TABELA, null, values);
                if (_id > 0) {
                    returnUri = PetEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }

                criarVacinasParaNovoPet(_id, values.getAsLong(PetEntry.COL_NASCIMENTO));

                break;
            case MATCH_VACINA_DOSE:
            case MATCH_VACINA_DOSE_COM_ID:
                _id = db.insert(VacinaDoseEntry.TABELA, null, values);
                if (_id > 0) {
                    returnUri = VacinaDoseEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            case MATCH_VACINA_PET:
            case MATCH_VACINA_PET_COM_ID:
            case MATCH_VACINA_PET_COM_PETID:
                _id = db.insert(VacinaPetEntry.TABELA, null, values);
                if (_id > 0) {
                    returnUri = VacinaPetEntry.buildUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("URI desconhecido: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = " 1 ";
        switch (match) {
            case MATCH_PET:
                rowsDeleted = db.delete(PetEntry.TABELA, selection, selectionArgs);
                break;
            case MATCH_PET_COM_ID:
                rowsDeleted = db.delete(PetEntry.TABELA, PetEntry.TABELA + "." + PetEntry._ID + " = ? ", new String[] {"" + PetEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_DOSE:
                rowsDeleted = db.delete(VacinaDoseEntry.TABELA, selection, selectionArgs);
                break;
            case MATCH_VACINA_DOSE_COM_ID:
                rowsDeleted = db.delete(VacinaDoseEntry.TABELA, VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID + " = ? ", new String[] {"" + VacinaDoseEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_PET:
                rowsDeleted = db.delete(VacinaPetEntry.TABELA, selection, selectionArgs);
                break;
            case MATCH_VACINA_PET_COM_ID:
                rowsDeleted = db.delete(VacinaPetEntry.TABELA, VacinaPetEntry.TABELA + "." + VacinaPetEntry._ID + " = ? ", new String[] {"" + VacinaPetEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_PET_COM_PETID:
                rowsDeleted = db.delete(VacinaPetEntry.TABELA, VacinaPetEntry.TABELA + "." + VacinaPetEntry.COL_PET_ID + " = ? ", new String[] {"" + VacinaPetEntry.getPetIdFromUri(uri)});
                break;
            default:
                throw new UnsupportedOperationException("URI desconhecido: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MATCH_PET:
                rowsUpdated = db.update(PetEntry.TABELA, values, selection, selectionArgs);
                break;
            case MATCH_PET_COM_ID:
                rowsUpdated = db.update(PetEntry.TABELA, values, PetEntry.TABELA + "." + PetEntry._ID + " = ? ", new String[] {"" + PetEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_COM_ID_PETID_E_VACINA_NOME_E_VACINA_INFO_LINK:
                rowsUpdated = db.update(VacinaEntry.TABELA, values, VacinaEntry.TABELA + "." + VacinaEntry._ID + " = ? ", new String[] {"" + VacinaEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_DOSE:
                rowsUpdated = db.update(VacinaDoseEntry.TABELA, values, selection, selectionArgs);
                break;
            case MATCH_VACINA_DOSE_COM_ID:
                rowsUpdated = db.update(VacinaDoseEntry.TABELA, values, VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID + " = ? ", new String[] {"" + VacinaDoseEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_PET:
                rowsUpdated = db.update(VacinaPetEntry.TABELA, values, selection, selectionArgs);
                break;
            case MATCH_VACINA_PET_COM_ID:
                rowsUpdated = db.update(VacinaPetEntry.TABELA, values, VacinaPetEntry.TABELA + "." + VacinaPetEntry._ID + " = ? ", new String[] {"" + VacinaPetEntry.getIdFromUri(uri)});
                break;
            case MATCH_VACINA_PET_COM_PETID:
                rowsUpdated = db.update(VacinaPetEntry.TABELA, values, VacinaPetEntry.TABELA + "." + VacinaPetEntry.COL_PET_ID + " = ? ", new String[] {"" + VacinaPetEntry.getPetIdFromUri(uri)});
                break;
            default:
                throw new UnsupportedOperationException("URI desconhecido: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int returnCount = 0;
        String nomeTabela;

        switch (match) {
            case MATCH_VACINA_DOSE:
            case MATCH_VACINA_DOSE_COM_ID:
                nomeTabela = VacinaDoseEntry.TABELA;
                break;
            case MATCH_VACINA_PET:
                nomeTabela = VacinaPetEntry.TABELA;
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        db.beginTransaction();

        try {
            for (ContentValues value : values) {
                long _id = db.insert(nomeTabela, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    /**
     * Preenche o "cartão de vacinação" de um pet recém-adicionado. Isto é, insere na tabela
     * vacina_dose todas as doses das vacinas com datas previstas de acordo com a data de
     * nascimento do pet.
     *
     * @param petId
     * @param petNascimento
     */
    private void criarVacinasParaNovoPet(long petId, long petNascimento) {
        final String sortOrderDoses = VacinaDoseEntry.COL_VACINA_ID + " ASC , " + VacinaDoseEntry.COL_NUM_DOSE + " ASC ";
        final String[] colunasDoses = {VacinaDoseEntry.TABELA + "." + VacinaDoseEntry._ID, VacinaDoseEntry.COL_VACINA_ID, VacinaDoseEntry.COL_INTERVALO_DIAS};
        final int colunaDose_Id = 0;
        final int colunaDose_VacinaId = 1;
        final int colunaDose_IntervaloDias = 2;

        Cursor cursorDoses = this.query(VacinaDoseEntry.buildUri(), colunasDoses, null, null, sortOrderDoses);

        if (cursorDoses.moveToFirst()) {
            ContentValues vetorContentValues[] = new ContentValues[cursorDoses.getCount()];
            int auxIndiceVetor = 0;
            long ultimoVacinaId = -1;
            long ultimoDataPrevisao = -1;

            do {
                long vacinaId = cursorDoses.getInt(colunaDose_VacinaId);
                long doseId = cursorDoses.getInt(colunaDose_Id);
                long doseIntervaloDias = cursorDoses.getInt(colunaDose_IntervaloDias);
                long dataPrevisao;

                if (vacinaId == ultimoVacinaId) {
                    // Próxima dose da mesma vacina: acrescentar última dataPrevisao calculada
                    dataPrevisao = ultimoDataPrevisao + (doseIntervaloDias * Utils.DIA_MILISSEGUNDOS);
                } else {
                    // Passou pra outra vacina
                    dataPrevisao = petNascimento + (doseIntervaloDias * Utils.DIA_MILISSEGUNDOS);
                }

                ultimoDataPrevisao = dataPrevisao;
                ultimoVacinaId = vacinaId;

                ContentValues auxContentValues = new ContentValues();
                auxContentValues.put(VacinaPetEntry.COL_VACINA_DOSE_ID, doseId);
                auxContentValues.put(VacinaPetEntry.COL_PET_ID, petId);
                auxContentValues.put(VacinaPetEntry.COL_DATA_PREVISAO, dataPrevisao);

                vetorContentValues[auxIndiceVetor] = auxContentValues;
                auxIndiceVetor++;
            } while (cursorDoses.moveToNext());

            this.bulkInsert(VacinaPetEntry.buildUri(), vetorContentValues);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}