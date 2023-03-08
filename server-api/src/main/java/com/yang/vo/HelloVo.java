package com.yang.vo;

import java.io.Serializable;

public class HelloVo implements Serializable {
    public String msg;

    @Override
    public String toString() {
        return "HelloVo{" +
                "msg='" + msg + '\'' +
                '}';
    }
}
