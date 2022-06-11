package ru.coolsoft.iris2go.rest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import retrofit2.*;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static ru.coolsoft.iris2go.rest.Config.*;

public class Engine {
    private static final String EQ = "=";
    private static final String CONCAT = "&";

    private static final Api mApi = new Retrofit.Builder()
            .baseUrl(Config.SERVER)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
            .create(Api.class);

    public static void translate(@NonNull String word, int lang, @Nullable Boolean byTrn, boolean showTrn, boolean nikud,
                                 Callback<ResponseDto> callback) {
        String args = createRequestArgs(word, lang, byTrn, showTrn, nikud);
        Call<ResponseDto> call = mApi.translate(Config.AJAX_TRANSLATE
                , System.currentTimeMillis()
                , args);
        try {
            call.enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();

            callback.onFailure(call, e);
        }
    }


    private static String createRequestArgs(@NonNull String word, int lang, @Nullable Boolean byTrn, boolean showTrn, boolean nikud) {
        StringBuilder args = new StringBuilder("<xjxquery><q>")
                .append(QUERY_WORD + EQ)
                .append(word)
                .append(CONCAT)

                .append(QUERY_WORD_ENC + EQ)
                .append(CONCAT)

                .append(QUERY_LANG + EQ)
                .append(lang)
                .append(CONCAT);

        if (byTrn != null) {
            args.append(QUERY_BY_TRN + EQ)
                    .append(byTrn ? 1 : 0)
                    .append(CONCAT);
        }

        args.append(QUERY_NIKUD + EQ)
                .append(nikud ? 1 : 0)
                .append(CONCAT)

                .append(QUERY_TRANSCRIPT + EQ)
                .append(showTrn ? 1 : 0)

                .append("</q></xjxquery>");

        return args.toString();
    }
}
