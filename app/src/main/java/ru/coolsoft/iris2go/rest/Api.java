package ru.coolsoft.iris2go.rest;

import retrofit2.Call;
import retrofit2.http.*;

import static ru.coolsoft.iris2go.rest.Config.ENDPOINT_TRANSLATE;

public interface Api {

    @FormUrlEncoded
    @POST(ENDPOINT_TRANSLATE)
    Call<ResponseDto> translate(
            @Field(value = Config.ARG_XAJAX) String xajax
            , @Field(value = Config.ARG_XAJAXR) long xajaxr
            , @Field(value = Config.ARG_ARGS) String args
    );
}
