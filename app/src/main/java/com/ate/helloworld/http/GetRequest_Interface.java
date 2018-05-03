package com.ate.helloworld.http;

import com.ate.helloworld.bean.Translation;
import com.ate.helloworld.bean.Translation1;
import com.ate.helloworld.bean.Translation2;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 进行网络请求的Retrofit的接口(返回一个Observable对象)
 */
public interface GetRequest_Interface {
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation> getCall();
    // 注解里传入 网络请求 的部分URL地址
    // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
    // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
    // 采用Observable<...>接口
    // getCall()是接受网络请求数据的方法


    // 网络请求1(注册)
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20register")
    Observable<Translation1> getCall_1();

    // 网络请求2(登录)
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20login")
    Observable<Translation2> getCall_2();
    // 注解里传入 网络请求 的部分URL地址
    // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
    // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
    // 采用Observable<...>接口
    // getCall()是接受网络请求数据的方法

    // 网络请求3
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20world")
    Observable<Translation1> getCall_3();

    // 网络请求4
    @GET("ajax.php?a=fy&f=auto&t=auto&w=hi%20china")
    Observable<Translation2> getCall_4();

}
