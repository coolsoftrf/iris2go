package ru.coolsoft.iris2go.rest;

import retrofit2.Call;
import retrofit2.http.*;

import static ru.coolsoft.iris2go.rest.Config.ENDPOINT_TRANSLATE;

public interface Api {

    @FormUrlEncoded
    @POST(ENDPOINT_TRANSLATE)
    Call<ResponseDto> translate(@Body RequestDto request);
}
