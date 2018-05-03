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
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * rxjava的嵌套网络请求(flatmap变换操作符的使用场景)
 * 原文链接:https://www.jianshu.com/p/5f5d61f04f96
 */
public class RxJavaNestedRequestActivity extends AppCompatActivity {

    private static final String TAG = "Rxjava";

    private Observable<Translation1> observable1;//注册
    private Observable<Translation2> observable2;//登录
    private GetRequest_Interface request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_java_nested_request);
        nestedRequest();
    }

    /**
     * 模拟注册成功后，再进行登录操作
     */
    private void nestedRequest() {
        //创建retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.icia.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        //创建网络请求接口实例
        request = retrofit.create(GetRequest_Interface.class);
        //实例化被观察者
        observable1 = request.getCall_1();
        observable2 = request.getCall_2();

        observable1.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                /**
                 * do系列操作符的作用
                 *    1.使用doOnNext()来调试
                      2.在flatMap()里使用doOnError()作为错误处理。
                      3.使用doOnNext()去保存/缓存网络结果
                 */
                .doOnNext(new Consumer<Translation1>() {
                    @Override
                    public void accept(Translation1 result) throws Exception {
                        Log.d(TAG, "第1次网络请求成功"+result.show());
                        // 对第1次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                })
                //用作网络请求失败处理
//                .doOnError(new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.d(TAG, "注册失败!");
//                    }
//                })
                .observeOn(Schedulers.io())
                        //（新被观察者，同时也是新观察者）切换到IO线程去发起登录请求
                        //特别注意：因为flatMap是对初始被观察者作变换，所以对于旧被观察者，它是新观察者，所以通过observeOn切换线程
                        //但对于初始观察者，它则是新的被观察者
                .flatMap(new Function<Translation1, ObservableSource<Translation2>>() {
                    // 作变换，即作嵌套网络请求
                    @Override
                    public ObservableSource<Translation2> apply(Translation1 translation1) throws Exception {
                        return observable2;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Translation2>() {
                    @Override
                    public void accept(Translation2 result) throws Exception {
                        Log.d(TAG, "第2次网络请求成功"+result.show());
                        // 对第2次网络请求返回的结果进行操作 = 显示翻译结果
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG,"登录失败");
                    }
                });
    }
}
