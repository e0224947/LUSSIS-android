package com.sa45team7.lussis.utils;

import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.LUSSISError;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by nhatton on 1/21/18.
 * Util class to convert response from server to get error message
 */

public class ErrorUtil {

    public static LUSSISError parseError(Response<?> response) {
        Converter<ResponseBody, LUSSISError> converter =
                LUSSISClient.getRetrofitInstance()
                        .responseBodyConverter(LUSSISError.class, new Annotation[0]);

        LUSSISError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new LUSSISError();
        }

        return error;
    }
}

