package com.ate.helloworld.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ate.helloworld.R;
import com.leon.channel.helper.ChannelReaderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * rxjava的创建操作符测试
 * 原文链接:https://www.jianshu.com/p/e19f8ed863b1
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;
    private String channel;//渠道信息
    private String TAG = "Rxjava";
    private Integer i = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(this);

        channel = ChannelReaderUtil.getChannel(getApplicationContext());
        if(channel == null){
            channel = "空";
        }

        rxjavaTest();
    }

    /**
     * 并没有什么卵用
     * @param v
     */
    @Override
    public void onClick(View v) {
        Toast.makeText(this,channel,Toast.LENGTH_LONG).show();
    }

    /**
     * rxjava的创建操作符测试
     */
    private void rxjavaTest(){
//        createTest();
//        justTest();
//        fromArrayTest();
        fromIterableTest();
//        deferTest();
//        timerTest();
//        intervalTest();
//        intervalRangeTest();
//        range();
        rangeLongTest();
    }


    /**
     * 需求场景:完整的创建被观察者对象
       对应操作符类型:create（）
       作用:完整创建1个被观察者对象（Observable）
        RxJava 中创建被观察者对象最基本的操作符
     */
    private void createTest(){
        // 1. 通过creat（）创建被观察者对象
        Observable.create(new ObservableOnSubscribe<Integer>() {
            // 2. 在复写的subscribe（）里定义需要发送的事件
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }// 至此，一个被观察者对象（Observable）就创建完毕
        }).subscribe(new Observer<Integer>() {
            // 以下步骤仅为展示一个完整demo，可以忽略
            // 3. 通过通过订阅（subscribe）连接观察者和被观察者
            // 4. 创建观察者 & 定义响应事件的行为
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext: "+"接受了事件"+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+"对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
            }
        });
    }

    /**
     * 需求场景:快速的创建被观察者对象
     对应操作符类型:just（）
     作用:
        1.快速创建1个被观察者对象（Observable）
        2.发送事件的特点：直接发送 传入的事件
     注：最多只能发送10个参数
     应用场景 快速创建 被观察者对象（Observable） & 发送10个以下事件
     */
    private void justTest(){
        // 1. 创建时传入整型1、2、3、4
        // 在创建后就会发送这些对象，相当于执行了onNext(1)、onNext(2)、onNext(3)、onNext(4)
        Observable.just(1,2,3,4,5,6,7,8,9)
                // 至此，一个Observable对象创建完毕，以下步骤仅为展示一个完整demo，可以忽略
                // 2. 通过通过订阅（subscribe）连接观察者和被观察者
                // 3. 创建观察者 & 定义响应事件的行为
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "onNext: "+"接受了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                    }
                });
    }


    /**
     * 作用:
     *     1.快速创建1个被观察者对象（Observable）
     2.发送事件的特点：直接发送 传入的数组数据
     会将数组中的数据转换为Observable对象
     应用场景:
     1.快速创建 被观察者对象（Observable） & 发送10个以上事件（数组形式）
     2.数组元素遍历
     */
    private void fromArrayTest(){
        Integer[] array = {0,1,2,3,4,5};
        Observable.fromArray(array)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"数组遍历");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "onNext: "+"数组中的元素"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"遍历结束");
                    }
                });
    }


    /**
     *作用
         1.快速创建1个被观察者对象（Observable）
         2.发送事件的特点：直接发送 传入的集合List数据
     会将数组中的数据转换为Observable对象
     应用场景
         1.快速创建 被观察者对象（Observable） & 发送10个以上事件（集合形式）
         2.集合元素遍历
     *
     */
    private void fromIterableTest(){
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<12;i++){
            list.add(i);
        }
        Observable.fromIterable(list)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"集合遍历");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "onNext: "+"集合中的元素"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"遍历结束");
                    }
                });
    }


    /**
     * 延迟创建
     需求场景
            1.定时操作：在经过了x秒后，需要自动执行y操作
            2.周期性操作：每隔x秒后，需要自动执行y操作
     defer（）

     作用
        直到有观察者（Observer ）订阅时，才动态创建被观察者对象（Observable） & 发送事件
        1.通过 Observable工厂方法创建被观察者对象（Observable）
        2.每次订阅后，都会得到一个刚创建的最新的Observable对象，这可以确保Observable对象里的数据是最新的
     应用场景
       动态创建被观察者对象（Observable） & 获取最新的Observable对象数据
     */
    private void deferTest(){
        // 2. 通过defer 定义被观察者对象
        // 注：此时被观察者对象还没创建
        Observable observable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });
        //第二次对i赋值
        i = 20;
        //观察者开始订阅
        //注：此时，才会调用defer（）创建被观察者对象（Observable）
        observable.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext: "+"接受了事件"+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: "+"对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
            }
        });
    }

    /**
     * 作用
          1.快速创建1个被观察者对象（Observable）
          2.发送事件的特点：延迟指定时间后，发送1个数值0（Long类型）
          本质 = 延迟指定时间后，调用一次 onNext(0)
     应用场景
          延迟指定事件，发送一个0，一般用于检测
     */
    private void timerTest(){
        final int seconds = 2;
        // 该例子 = 延迟2s后，发送一个long类型数值
        Observable.timer(seconds, TimeUnit.SECONDS)
                  .subscribe(new Observer<Long>() {
                      @Override
                      public void onSubscribe(Disposable d) {
                          Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                      }

                      @Override
                      public void onNext(Long value) {
                          Log.d(TAG, "onNext: "+seconds+"秒后接受了事件"+value);
                      }

                      @Override
                      public void onError(Throwable e) {
                          Log.d(TAG, "onError: "+"对Error事件作出响应");
                      }

                      @Override
                      public void onComplete() {
                          Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                      }
                  });
    }

    /**
     * 作用
          1.快速创建1个被观察者对象（Observable）
          2.发送事件的特点：每隔指定时间 就发送事件
          发送的事件序列 = 从0开始、无限递增1的的整数序列(无限产生事件)
     */
    private void intervalTest(){
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
        Observable.interval(3,1,TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "onNext: "+"接受了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                    }
                });
    }

    /**
     * 作用
          1.快速创建1个被观察者对象（Observable）
          2.发送事件的特点：每隔指定时间 就发送 事件，可指定发送的数据的数量
          a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
          b. 作用类似于interval（），但可指定发送的数据的数量
     */
    private void intervalRangeTest(){
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 参数3 = 第1次事件延迟发送时间；
        // 参数4 = 间隔时间数字；
        // 参数5 = 时间单位
        Observable.intervalRange(3,5,2,1,TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：
                // 1. 从3开始，一共发送10个事件；
                // 2. 第1次延迟2s发送，之后每隔2秒产生1个数字（从0开始递增1，无限个）
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "onNext: "+"接受了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                    }
                });
    }

    /**
     *作用
         1.快速创建1个被观察者对象（Observable）
         2.发送事件的特点：连续发送 1个事件序列，可指定范围
         a. 发送的事件序列 = 从0开始、无限递增1的的整数序列
         b. 作用类似于intervalRange（），但区别在于：无延迟发送事件
     */
    private void range(){
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.range(5,12)
                //该例子发送的事件序列特点：从3开始发送，每次发送事件递增1，一共发送10个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "onNext: "+"接受了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                    }
                });
    }

    /**
     * 作用：类似于range（），区别在于该方法支持数据类型 = Long
             具体使用
             与range（）类似，此处不作过多描述
     */
    private void rangeLongTest(){
        Observable.rangeLong(3,10)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: "+"开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "onNext: "+"接受了事件"+value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+"对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: "+"对Complete事件作出响应");
                    }
                });
    }

}
