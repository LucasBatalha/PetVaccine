package com.petvaccine.app.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.petvaccine.app.R;
import com.petvaccine.app.data.PvContract;
import com.petvaccine.app.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

public class PetAdicionaFragment extends Fragment {

    private static final int CODIGO_INTENT_GALERIA = 1;

    private Button mBotaoAdicionar;
    private EditText mCampoNascimento;
    private EditText mCampoNome;
    private Spinner mCampoRaca;
    private ImageView mImagem;
    private TextView mTextAlteraImagem;

    private String mPathImagemEscolhida = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pet_adiciona_fragment, container, false);
        mBotaoAdicionar = (Button) rootView.findViewById(R.id.adiciona_pet_botao);
        mCampoNascimento = (EditText) rootView.findViewById(R.id.adiciona_pet_nascimento);
        mCampoNome = (EditText) rootView.findViewById(R.id.adiciona_pet_nome);
        mCampoRaca = (Spinner) rootView.findViewById(R.id.adiciona_pet_raca);
        mImagem = (ImageView) rootView.findViewById(R.id.adiciona_pet_imagem);
        mTextAlteraImagem = (TextView) rootView.findViewById(R.id.adiciona_pet_text_alterar_imagem);

        View.OnClickListener onClickAbreGaleria = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loadPicture = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(loadPicture, CODIGO_INTENT_GALERIA);
            }
        };

        mImagem.setOnClickListener(onClickAbreGaleria);
        mTextAlteraImagem.setOnClickListener(onClickAbreGaleria);

        mBotaoAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarPet();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODIGO_INTENT_GALERIA && resultCode == Activity.RESULT_OK) {
            Uri imagemUri = data.getData();
            mImagem.setImageURI(imagemUri);

            Cursor cursor = getActivity().getContentResolver().query(imagemUri, null, null, null, null);
            cursor.moveToFirst();
            mPathImagemEscolhida = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            cursor.close();
        }
    }

    private void adicionarPet() {
        String nome = mCampoNome.getText().toString();
        String raca = (String) mCampoRaca.getSelectedItem();
        long nascimento = 0;

        try {
            // Transformando data de nascimento em long
            nascimento = DateFormat.getDateFormat(getActivity()).parse(mCampoNascimento.getText().toString()).getTime();
        } catch (ParseException ex) {
            nascimento = 0;
        }

        if (nascimento <= 0 || nascimento >= System.currentTimeMillis()) {
            Utils.exibeMensagemAtencao(getString(R.string.erro_data_nascimento_invalida), getActivity());
        } else if (nome == null || "".equals(nome) || raca == null || "".equals(raca)) {
            Utils.exibeMensagemAtencao(getString(R.string.erro_campos_mal_preenchidos), getActivity());
        } else {
            // Salvando a imagem...
            if (mPathImagemEscolhida != null) {
                try {
                    copiaImagem(mPathImagemEscolhida, nome);
                } catch (Exception ex) {
                    Utils.exibeMensagemErro(getString(R.string.pet_adiciona_erro_imagem), getActivity());
                }
            }

            // Salvando os dados...
            ContentValues petValues = new ContentValues();
            petValues.put(PvContract.PetEntry.COL_NOME, nome);
            petValues.put(PvContract.PetEntry.COL_NASCIMENTO, nascimento);
            petValues.put(PvContract.PetEntry.COL_RACA, raca);

            getActivity().getContentResolver().insert(PvContract.PetEntry.buildUri(), petValues);

            Toast.makeText(getActivity(), getString(R.string.pet_adiciona_adicionado), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    /**
     * Copia a imagem para um arquivo no diretório privado do aplicativo e retorna o caminho
     * para o novo arquivo - nomeado com o mesmo nome do pet.
     *
     * @param path Caminho da imagem a ser copiada.
     * @throws IOException
     */
    private void copiaImagem(String path, String nomePet) throws IOException {
        InputStream originalInputStream = null;
        OutputStream novaOutputStream = null;

        try {
            File originalFile = new File(path);
            originalInputStream = new FileInputStream(originalFile);
            novaOutputStream = getActivity().openFileOutput(nomePet, Context.MODE_PRIVATE);byte[] buffer = new byte[32768]; // Buffer de 32 KB

            int bytesRead = 0;
            while(bytesRead != -1){
                bytesRead = originalInputStream.read(buffer);
                if(bytesRead > 0){
                    novaOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } finally {
            try {
                novaOutputStream.close();
            } catch (Exception ex) {}

            try {
                originalInputStream.close();
            } catch (Exception ex) {}
        }
    }

}
