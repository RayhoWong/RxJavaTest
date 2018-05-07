package com.ate.helloworld.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.ate.helloworld.R;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * 过滤操作符
 * 原文链接:https://www.jianshu.com/p/90d53c791c42
 */
public class FifthActivity extends AppCompatActivity {

    private static final String TAG = "rxjava";

    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);
        mBtn = findViewById(R.id.btn);
//        rxjavaTest();
        gongNengFangDou();
    }

    /**
     * 功能防抖测试
     * 功能防抖:在规定的时间内，用户多次触发该功能,仅会触发第一次操作
     * throttleFirst的应用
     *
     */
    private void gongNengFangDou(){
      /*  1. 此处采用了RxBinding：RxView.clicks(button) = 对控件点击进行监听，需要引入依赖：compile 'com.jakewharton.rxbinding2:rxbinding:2.0.0'
          2. 传入Button控件，点击时，都会发送数据事件（但由于使用了throttleFirst（）操作符，所以只会发送该段时间内的第1次点击事件）*/
        RxView.clicks(mBtn)
                .throttleFirst(5,TimeUnit.SECONDS)// 才发送5s内第1次点击按钮的事件
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Object value) {
                        Log.d(TAG, "发送了网络请求" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应" + e.toString());
                        // 获取异常错误信息
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
    }

    private void rxjavaTest(){
//        filterTest();
//        ofTypeTest();
//        skipAndskipLastTest();
//        distinctAnddistinctUntilChangedTest();
//        takeTest();
//        takeLastTest();
//        throttleFirstAndthrottleLastTest();
//        firstElementAndlastElementTest();
//        elementAtTest();
//        elementAtOnErrorTest();
    }

    /**
     * 作用:过滤 特定条件的事件
       原理
     */
    private void filterTest(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                // 1. 发送5个事件
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onNext(4);
                e.onNext(5);
            }
            // 2. 采用filter（）变换操作符
        }).filter(new Predicate<Integer>() {
            // 根据test()的返回值 对被观察者发送的事件进行过滤 & 筛选
            // a. 返回true，则继续发送
            // b. 返回false，则不发送（即过滤）
            @Override
            public boolean test(Integer integer) throws Exception {
                // 本例子 = 过滤了整数≤3的事件
                return integer > 3;
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "过滤后得到的事件是："+ value  );
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "对Complete事件作出响应");
            }
        });
    }

    /**
     * 过滤特定的数据类型的数据
     */
    private void ofTypeTest(){
        Observable.just(1,"hoho",2,"hahah",5)
                .ofType(Integer.class)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"获取到的整型事件元素是： "+ integer);
                    }
                });
    }

    private void skipAndskipLastTest(){
        // 使用1：根据顺序跳过数据项
        Observable.just(1,2,3,4,5)
                .skip(1)// 跳过正序的前1项
                .skipLast(2)// 跳过正序的后2项
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"获取到的整型事件元素是： "+ integer);
                    }
                });

        // 使用2：根据时间跳过数据项
        // 发送事件特点：发送数据0-5，每隔1s发送一次，每次递增1；第1次发送延迟0s
        Observable.intervalRange(0, 5, 0, 1, TimeUnit.SECONDS)
                .skip(1, TimeUnit.SECONDS) // 跳过第1s发送的数据
                .skipLast(1, TimeUnit.SECONDS) // 跳过最后1s发送的数据
                .subscribe(new Consumer<Long>() {

                    @Override
                    public void accept( Long along ) throws Exception {
                        Log.d(TAG,"获取到的整型事件元素是： "+ along);
                    }
                });
    }

    /**
     * 作用:过滤事件序列中重复的事件 / 连续重复的事件
     */
    private void distinctAnddistinctUntilChangedTest(){
        // 使用1：过滤事件序列中重复的事件
        Observable.just(1,2,1,2,3)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d(TAG,"不重复的整型事件元素是： "+ integer);
                    }
                });

        // 使用2：过滤事件序列中 连续重复的事件
        // 下面序列中，连续重复的事件 = 3、4
        Observable.just(1,2,3,1,2,3,3,4,4 )
                .distinctUntilChanged()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept( Integer integer) throws Exception {
                        Log.d(TAG,"不连续重复的整型事件元素是： "+ integer);
                    }
                });
    }

    /**
     * 根据指定事件数量过滤事件
       需求场景:通过设置指定的事件数量，仅发送特定数量的事件
       对应操作符类型:take（） & takeLast（）
       对应操作符使用:take（）
       作用:指定观察者最多能接收到的事件数量
     */
    private void takeTest(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                // 1. 发送5个事件
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onNext(5);
            }

            // 采用take（）变换操作符
            // 指定了观察者只能接收2个事件
        }).take(2)
                .subscribe(new Observer<Integer>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "过滤后得到的事件是："+ value  );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
        // 实际上，可理解为：被观察者还是发送了5个事件，只是因为操作符的存在拦截了3个事件，最终观察者接收到的是2个事件
    }


    /**
     * takeLast（）
       作用:指定观察者只能接收到被观察者发送的最后几个事件
     */
    private void takeLastTest(){
        Observable.just(1, 2, 3, 4, 5)
                .takeLast(3) //指定观察者只能接受被观察者发送的3个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "过滤后得到的事件是："+ value  );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }
                });
    }

    /**
     * 需求场景:通过设置指定的时间，仅发送在该时间内的事件
     * 作用:在某段时间内，只发送该段时间内第1次事件 / 最后1次事件
     *      如，1段时间内连续点击按钮，但只执行第1次的点击操作
     */
    private void throttleFirstAndthrottleLastTest(){
//        <<- 在某段时间内，只发送该段时间内第1次事件 ->>
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        // 隔段事件发送时间
                        e.onNext(1);
                        Thread.sleep(500);

                        e.onNext(2);
                        Thread.sleep(400);

                        e.onNext(3);
                        Thread.sleep(300);

                        e.onNext(4);
                        Thread.sleep(300);

                        e.onNext(5);
                        Thread.sleep(300);

                        e.onNext(6);
                        Thread.sleep(400);

                        e.onNext(7);
                        Thread.sleep(300);
                        e.onNext(8);

                        Thread.sleep(300);
                        e.onNext(9);

                        Thread.sleep(300);
                        e.onComplete();
                    }
                }).throttleFirst(1, TimeUnit.SECONDS)//每1秒中采用数据
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "开始采用subscribe连接");
                            }

                            @Override
                            public void onNext(Integer value) {
                                Log.d(TAG, "接收到了事件"+ value  );
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "对Error事件作出响应");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "对Complete事件作出响应");
                            }
                        });

                //在某段时间内，只发送该段时间内最后1次事件
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                        // 隔段事件发送时间
                        e.onNext(1);
                        Thread.sleep(500);

                        e.onNext(2);
                        Thread.sleep(400);

                        e.onNext(3);
                        Thread.sleep(300);

                        e.onNext(4);
                        Thread.sleep(300);

                        e.onNext(5);
                        Thread.sleep(300);

                        e.onNext(6);
                        Thread.sleep(400);

                        e.onNext(7);
                        Thread.sleep(300);
                        e.onNext(8);

                        Thread.sleep(300);
                        e.onNext(9);

                        Thread.sleep(300);
                        e.onComplete();
                    }
                }).throttleLast(1, TimeUnit.SECONDS)//每1秒中采用数据
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                Log.d(TAG, "开始采用subscribe连接");
                            }

                            @Override
                            public void onNext(Integer value) {
                                Log.d(TAG, "接收到了事件"+ value  );
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "对Error事件作出响应");
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "对Complete事件作出响应");
                            }
                        });
    }

   /* Sample（）
    作用:在某段时间内，只发送该段时间内最新（最后）1次事件 与 throttleLast（） 操作符类似
    具体使用:仅需要把上文的 throttleLast（） 改成Sample（）操作符即可，此处不作过多描述*/

    /**
     * throttleWithTimeout （） / debounce（）
     * 作用:发送数据事件时，若2次发送事件的间隔＜指定时间，就会丢弃前一次的数据，直到指定时间内都没有新数据发射时才会发送后一次的数据
     */
   private void throttleWithTimeoutAndDebounceTest(){
       Observable.create(new ObservableOnSubscribe<Integer>() {
           @Override
           public void subscribe(ObservableEmitter<Integer> e) throws Exception {
               // 隔段事件发送时间
               e.onNext(1);
               Thread.sleep(500);
               e.onNext(2); // 1和2之间的间隔小于指定时间1s，所以前1次数据（1）会被抛弃，2会被保留
               Thread.sleep(1500);  // 因为2和3之间的间隔大于指定时间1s，所以之前被保留的2事件将发出
               e.onNext(3);
               Thread.sleep(1500);  // 因为3和4之间的间隔大于指定时间1s，所以3事件将发出
               e.onNext(4);
               Thread.sleep(500); // 因为4和5之间的间隔小于指定时间1s，所以前1次数据（4）会被抛弃，5会被保留
               e.onNext(5);
               Thread.sleep(500); // 因为5和6之间的间隔小于指定时间1s，所以前1次数据（5）会被抛弃，6会被保留
               e.onNext(6);
               Thread.sleep(1500); // 因为6和Complete实践之间的间隔大于指定时间1s，所以之前被保留的6事件将发出

               e.onComplete();
           }
       }).throttleWithTimeout(1, TimeUnit.SECONDS)//每1秒中采用数据
               .subscribe(new Observer<Integer>() {
                   @Override
                   public void onSubscribe(Disposable d) {

                   }

                   @Override
                   public void onNext(Integer value) {
                       Log.d(TAG, "接收到了事件"+ value  );
                   }

                   @Override
                   public void onError(Throwable e) {
                       Log.d(TAG, "对Error事件作出响应");
                   }

                   @Override
                   public void onComplete() {
                       Log.d(TAG, "对Complete事件作出响应");
                   }
               });
   }

    /**
     *根据 指定事件位置 过滤事件
      需求场景:通过设置指定的位置，过滤在该位置的事件
      firstElement（） / lastElement（）
      作用:仅选取第1个元素 / 最后一个元素
     */
   private void firstElementAndlastElementTest(){
       // 获取第1个元素
       Observable.just(1, 2, 3, 4, 5)
               .firstElement()
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept( Integer integer) throws Exception {
                       Log.d(TAG,"获取到的第一个事件是： "+ integer);
                   }
               });

       // 获取最后1个元素
       Observable.just(1, 2, 3, 4, 5)
               .lastElement()
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept( Integer integer) throws Exception {
                       Log.d(TAG,"获取到的最后1个事件是： "+ integer);
                   }
               });
   }

    /**
     *作用:指定接收某个元素（通过 索引值 确定）
      注：允许越界，即获取的位置索引 ＞ 发送事件序列长度
     */
   private void elementAtTest(){
       // 使用1：获取位置索引 = 2的 元素
       // 位置索引从0开始
       Observable.just(1, 2, 3, 4, 5)
               .elementAt(2)
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept( Integer integer) throws Exception {
                       Log.d(TAG,"获取到的事件元素是： "+ integer);
                   }
               });

       // 使用2：获取的位置索引 ＞ 发送事件序列长度时，设置默认参数
       Observable.just(1, 2, 3, 4, 5)
               .elementAt(6,10)
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept( Integer integer) throws Exception {
                       Log.d(TAG,"获取到的事件元素是： "+ integer);
                   }
               });
   }

    /**
     * elementAtOrError（）
     *作用:在elementAt（）的基础上，当出现越界情况（即获取的位置索引 ＞ 发送事件序列长度）时，即抛出异常
     */
   private void elementAtOnErrorTest(){
       Observable.just(1, 2, 3, 4, 5)
               .elementAtOrError(6)
               .subscribe(new Consumer<Integer>() {
                   @Override
                   public void accept( Integer integer) throws Exception {
                       Log.d(TAG,"获取到的事件元素是： "+ integer);
                   }
               });
   }


}
