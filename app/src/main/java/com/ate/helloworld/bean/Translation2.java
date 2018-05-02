package com.ate.helloworld.bean;

/**
 * 创建实体类
 */
public class Translation2 {
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

}
