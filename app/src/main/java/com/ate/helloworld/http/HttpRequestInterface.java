package com.ate.helloworld.http;

import com.ate.helloworld.bean.Translation;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface HttpRequestInterface {
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation> getCall();
}
