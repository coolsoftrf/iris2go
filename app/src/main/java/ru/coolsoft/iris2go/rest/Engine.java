package ru.coolsoft.iris2go.rest;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.*;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Response;
import retrofit2.Retrofit;

public class Engine {
    private static final Api mApi = new Retrofit.Builder()
            .baseUrl(Config.SERVER)
            .build()
            .create(Api.class);

    public static Spanned translate(@NonNull String word, int lang, @Nullable Boolean byTrn, boolean showTrn, boolean nikud){
        RequestDto request = new RequestDto(word, lang, byTrn, showTrn, nikud);

        try {
            Response<ResponseDto> response = mApi.translate(request).execute();
            ResponseDto dto = response.body();
//            return Html.fromHtml(dto.xjx.cmd[1].cdata);
            return SpannedString.valueOf(Objects.requireNonNull(dto).toString());
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return SpannedString.valueOf("error");
    }
}
