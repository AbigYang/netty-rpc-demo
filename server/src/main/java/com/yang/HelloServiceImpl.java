package com.yang;

import com.yang.vo.HelloVo;

public class HelloServiceImpl implements HelloService {

    @Override
    public HelloVo hello(HelloVo vo) {
        System.out.println("accept hello：" + vo.msg);
        HelloVo helloVo = new HelloVo();
        helloVo.msg = "666";
        return helloVo;
    }
}
