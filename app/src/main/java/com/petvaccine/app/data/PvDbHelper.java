package com.petvaccine.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.petvaccine.app.data.PvContract.*;

public class PvDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "petvaccine.db";

    public PvDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.beginTransaction();

///////////////////// CRIANDO TABELAS //////////////////////////////////////////////////////////////

            String SQL_CREATE_TABLE_PET = "CREATE TABLE " + PetEntry.TABELA + " ("
                    + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PetEntry.COL_NOME + " TEXT UNIQUE NOT NULL, "
                    + PetEntry.COL_NASCIMENTO + " INTEGER NOT NULL, "
                    + PetEntry.COL_RACA + " TEXT NOT NULL);";

            String SQL_CREATE_TABLE_VACINA = "CREATE TABLE " + VacinaEntry.TABELA + " ("
                    + VacinaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + VacinaEntry.COL_NOME + " TEXT UNIQUE NOT NULL, "
                    + VacinaEntry.COL_INFO_LINK + " TEXT UNIQUE NOT NULL, "
                    + VacinaEntry.COL_INFO + " TEXT);";

            String SQL_CREATE_TABLE_VACINA_DOSE = "CREATE TABLE " + VacinaDoseEntry.TABELA + " ("
                    + VacinaDoseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + VacinaDoseEntry.COL_VACINA_ID + " INTEGER NOT NULL REFERENCES " + VacinaEntry.TABELA + "(" + VacinaEntry._ID + ") ON DELETE CASCADE, "
                    + VacinaDoseEntry.COL_NUM_DOSE + " INTEGER NOT NULL, "
                    + VacinaDoseEntry.COL_INTERVALO_DIAS + " INTEGER NOT NULL);";

            String SQL_CREATE_TABLE_VACINA_PET = "CREATE TABLE " + VacinaPetEntry.TABELA + " ("
                    + VacinaPetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + VacinaPetEntry.COL_VACINA_DOSE_ID + " INTEGER NOT NULL REFERENCES " + VacinaDoseEntry.TABELA + "(" + VacinaDoseEntry._ID + ") ON DELETE CASCADE, "
                    + VacinaPetEntry.COL_PET_ID + " INTEGER NOT NULL REFERENCES " + PetEntry.TABELA + "(" + PetEntry._ID + ") ON DELETE CASCADE, "
                    + VacinaPetEntry.COL_DATA_PREVISAO + " INTEGER NOT NULL, "
                    + VacinaPetEntry.COL_DATA_APLICACAO + " INTEGER, "
                    + VacinaPetEntry.COL_LABORATORIO + " TEXT, "
                    + VacinaPetEntry.COL_LOTE + " TEXT);";

            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_PET);
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_VACINA);
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_VACINA_DOSE);
            sqLiteDatabase.execSQL(SQL_CREATE_TABLE_VACINA_PET);

///////////////////// INSERINDO VACINAS ////////////////////////////////////////////////////////////
            /*
                Estas doses de vacinas são pré-adicionadas ao banco e não serão alteradas durante a execução do app.
                Quando um pet é adicionado pelo usuário, um script cria seu "cartão de vacinação" - i.e. preenche a tabela 'vacina_pet' com estas doses.
                Estas doses aqui inseridas são informações reais sobre as vacinas básicas para cachorros.
                No entanto são necessárias mais pesquisas e futuras alterações para melhor usabilidade.
             */

            int id = 0;
            int dose;

            id++; dose = 0;
            sqLiteDatabase.insert(VacinaEntry.TABELA, null, criaContentValuesVacina(id, "Vermífugo", null, "info-vermifugo"));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 30));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 365));
            for (int i = 0; i < 40; i++) sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 90));

            id++; dose = 0;
            sqLiteDatabase.insert(VacinaEntry.TABELA, null, criaContentValuesVacina(id, "Antipulgas", null, "info-antipulgas"));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 42));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 365));
            for (int i = 0; i < 120; i++) sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 30));

            id++; dose = 0;
            sqLiteDatabase.insert(VacinaEntry.TABELA, null, criaContentValuesVacina(id, "Múltipla", null, "info-multipla"));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 42));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 21));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 21));
            for (int i = 0; i < 10; i++) sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 365));

            id++; dose = 0;
            sqLiteDatabase.insert(VacinaEntry.TABELA, null, criaContentValuesVacina(id, "Tosse", null, "info-tosse"));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 63));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 21));
            for (int i = 0; i < 10; i++) sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 365));

            id++; dose = 0;
            sqLiteDatabase.insert(VacinaEntry.TABELA, null, criaContentValuesVacina(id, "Antirrábica", null, "info-antirrabica"));
            sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 120));
            for (int i = 0; i < 10; i++) sqLiteDatabase.insert(VacinaDoseEntry.TABELA, null, criaContentValuesVacinaDose(id, ++dose, 365));

            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Ainda não precisa ser implementado.
    }

    private ContentValues criaContentValuesVacina(int id, String nome, String info, String infoLink) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VacinaEntry._ID, id);
        contentValues.put(VacinaEntry.COL_NOME, nome);
        contentValues.put(VacinaEntry.COL_INFO, info);
        contentValues.put(VacinaEntry.COL_INFO_LINK, infoLink);
        return contentValues;
    }

    private ContentValues criaContentValuesVacinaDose(int id, int numDose, int intervaloDias) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VacinaDoseEntry.COL_VACINA_ID, id);
        contentValues.put(VacinaDoseEntry.COL_NUM_DOSE, numDose);
        contentValues.put(VacinaDoseEntry.COL_INTERVALO_DIAS, intervaloDias);
        return contentValues;
    }

}
