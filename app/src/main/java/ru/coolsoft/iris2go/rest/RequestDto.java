package ru.coolsoft.iris2go.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import static ru.coolsoft.iris2go.rest.Config.*;

class RequestDto {
    private static final String EQ = "=";
    private static final String CONCAT = "&";
    @SerializedName(Config.ARG_XAJAX)
    private String xajax;

    @SerializedName(Config.ARG_XAJAXR)
    private long xajaxr;

    @SerializedName(Config.ARG_ARGS)
    private String args;

    RequestDto(@NonNull String word, int lang, @Nullable Boolean byTrn, boolean showTrn, boolean nikud) {
        this.xajax = Config.AJAX_TRANSLATE;
        this.xajaxr = System.currentTimeMillis();

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

                .append("</xjxquery></q>");

        this.args = args.toString();
    }
}
