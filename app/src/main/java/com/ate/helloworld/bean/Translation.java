package com.ate.helloworld.bean;

import android.util.Log;

/**
 * 创建实体类
 */
public class Translation {
    private int status;

    private Content content;
    private static class Content {
        private String from;
        private String to;
        private String vendor;
        private String out;
        private int errNo;
    }

    //定义 输出返回数据 的方法
    public String show() {
        return content.out;
    }

    public void show2(){
        Log.d("RxJava", content.out );
    }
}
