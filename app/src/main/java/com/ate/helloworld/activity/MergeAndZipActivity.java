package com.ate.helloworld.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ate.helloworld.R;
import com.ate.helloworld.bean.Translation1;
import com.ate.helloworld.bean.Translation2;
import com.ate.helloworld.http.GetRequest_Interface;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * merge和zip操作符应用(合并数据源和合并2个网络请求)
 */
public class MergeAndZipActivity extends AppCompatActivity {
    private static final String TAG = "rxjava";
    private String result = "数据源来自 = ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_and_zip);
//        mergeTest();
        zipTest();
    }

    // 用于存放最终展示的数据
    private void mergeTest() {
        /*
         * 设置第1个Observable：通过网络获取数据
         * 此处仅作网络请求的模拟
         **/
        Observable<String> network = Observable.just("网络");

        /*
         * 设置第2个Observable：通过本地文件获取数据
         * 此处仅作本地文件请求的模拟
         **/
        Observable<String> file = Observable.just("本地文件");


        /*
         * 通过merge（）合并事件 & 同时发送事件
         **/
        Observable.merge(network, file)
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "数据源有： " + value);
                        result += value + "+";
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    // 接收合并事件后，统一展示
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "获取数据完成");
                        Log.d(TAG, result);
                    }
                });
    }


    /**
     * 功能说明
       在该例中，我将结合结合 Retrofit 与RxJava，实现：
          1.从不同数据源（2个服务器）获取数据，即 合并网络请求的发送
          2.统一显示结果
     */
    private void zipTest() {
        // 定义Observable接口类型的网络请求对象
        Observable<Translation1> observable1;
        Observable<Translation2> observable2;

        //1.创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        //2.创建网络请求接口的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //3.采用Observable<...>形式 对 2个网络请求 进行封装
        observable1 = request.getCall_3().subscribeOn(Schedulers.io());
        observable2 = request.getCall_4().subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Translation1, Translation2, String>() {
            @Override
            public String apply(Translation1 translation1, Translation2 translation2) throws Exception {
                return translation1.show() + " & " + translation2.show();
            }
        }).observeOn(AndroidSchedulers.mainThread())// 在主线程接收 & 处理数据
                .subscribe(new Consumer<String>() {
                    // 成功返回数据时调用
                    @Override
                    public void accept(String s) throws Exception {
                        // 结合显示2个网络请求的数据结果
                        Log.d(TAG, "最终接收到的数据是：" + s);
                    }
                }, new Consumer<Throwable>() {
                    // 网络请求错误时调用
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        System.out.println("登录失败");
                    }
                });
    }
}
