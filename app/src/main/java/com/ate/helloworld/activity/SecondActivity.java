package com.ate.helloworld.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ate.helloworld.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 原文链接https://www.jianshu.com/p/904c14d253ba
 * rxjava的变换操作符的测试
 */
public class SecondActivity extends AppCompatActivity {

    private String TAG = "Rxjava";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        rxjavaTest();
    }

    private void rxjavaTest(){
//        mapTest();
        flatmapTest();
//        concatmapTest();
//        bufferTest();
    }


    /*
       Map（）
       作用: 对被观察者发送的每1个事件都通过 指定的函数 处理，从而变换成另外一种事件
             即将被观察者发送的事件转换为任意的类型事件。
       应用场景:数据类型转换
     */
    private void mapTest(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        })
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return "使用 Map变换操作符 将事件" + integer +"的参数从 整型"+integer + " 变换成 字符串类型" + integer ;
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.d(TAG, s);
                    }
                });
    }

    /**
     * FlatMap（）
     作用：将被观察者发送的事件序列进行 拆分 & 单独转换，再合并成一个新的事件序列，最后再进行发送
     原理
        1.为事件序列中每个事件都创建一个Observable对象；
        2.将对每个原始事件转换后的新事件 都放入到对应 Observable对象；
        3.将新建的每个Observable 都合并到一个 新建的、总的Observable 对象；
        4.新建的、总的Observable对象将新合并的事件序列发送给观察者（Observer）
     应用场景:无序的将被观察者发送的整个事件序列进行变换
     注意：新合并生成的事件序列顺序是无序的，即 与旧序列发送事件的顺序无关
     */
    private void flatmapTest(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for(int i=0;i<3;i++){
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过flatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    /**
     * ConcatMap（）
     *  作用：类似FlatMap（）操作符
        与FlatMap（）的 区别在于：拆分 & 重新合并生成的事件序列的顺序 = 被观察者旧序列生产的顺序
        应用场景:有序的将被观察者发送的整个事件序列进行变换
        注：新合并生成的事件序列顺序是有序的，即 严格按照旧序列发送事件的顺序
     */
    private void concatmapTest() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).concatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("我是事件 " + integer + "拆分后的子事件" + i);
                    // 通过concatMap中将被观察者生产的事件序列先进行拆分，再将每个事件转换为一个新的发送三个String事件
                    // 最终合并，再发送给被观察者
                }
                return Observable.fromIterable(list);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    /**
     * 作用:定期从 被观察者（Obervable）需要发送的事件中 获取一定数量的事件 & 放到缓存区中,最终发送
     * 应用场景:缓存被观察者发送的事件
     */
    private void bufferTest() {
        Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 1)
                // 设置缓存区大小 & 步长
                // 缓存区大小 = 每次从被观察者中获取的事件数量
                // 步长 = 每次获取新事件的数量
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Integer> list) {
                        Log.d(TAG, "缓存区的事件数量=" + list.size());
                        for (Integer integer : list) {
                            Log.d(TAG, "事件=" + integer);
                        }
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
}
