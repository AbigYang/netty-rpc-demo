package com.yang.vo;

import java.net.URI;

public class NameServerVo {
    private Integer port;
    private String address;
    private String serviceName;

    public NameServerVo(String serviceName, URI uri) {
        this.port = uri.getPort();
        this.address = uri.getHost();
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "NameServerVo{" +
                "port=" + port +
                ", address='" + address + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }

    public NameServerVo() {

    }

    public NameServerVo(String serviceName, String address, Integer port) {
        this.port = port;
        this.address = address;
        this.serviceName = serviceName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
