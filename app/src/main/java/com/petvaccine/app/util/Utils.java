package com.petvaccine.app.util;

import android.app.AlertDialog;
import android.content.Context;

import com.petvaccine.app.R;

public abstract class Utils {

    public static final long DIA_MILISSEGUNDOS = 24*60*60*1000; // Quantidade de milissegundos em um dia

    /**
     * Exibe mensagem ao usuario.
     *
     * @param titulo Titulo da mensagem a ser exibida.
     * @param mensagem Mensagem a ser exibida.
     */
    private static void exibeMensagem(String titulo, String mensagem, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo);
        builder.setMessage(mensagem);
        builder.setNeutralButton(context.getResources().getString(R.string.mensagem_botao), null);
        builder.show();
    }

    /**
     * Exibe mensagem de pedido de atencao ao usuario.
     *
     * @param mensagem Mensagem a ser exibida.
     */
    public static void exibeMensagemAtencao(String mensagem, Context context) {
        exibeMensagem(context.getString(R.string.mensagem_titulo_atencao), mensagem, context);
    }

    /**
     * Exibe mensagem de erro ao usuario.
     *
     * @param mensagem Mensagem a ser exibida.
     */
    public static void exibeMensagemErro(String mensagem, Context context) {
        exibeMensagem(context.getString(R.string.mensagem_titulo_erro), mensagem, context);
    }

    /**
     * Exibe mensagem de informação ao usuario.
     *
     * @param mensagem Mensagem a ser exibida.
     */
    public static void exibeMensagemInfo(String mensagem, Context context) {
        exibeMensagem(context.getString(R.string.mensagem_titulo_info), mensagem, context);
    }

}
