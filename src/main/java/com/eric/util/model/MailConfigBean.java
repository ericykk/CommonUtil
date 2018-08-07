package com.eric.util.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description: 读取指定配置文件属性Bean
 * author: Eric
 * Date: 18/3/15
 */
@Component
public class MailConfigBean {
    @Value("${mail.trace.protocol}")
    private String protocol;

    @Value("${mail.trace.host}")
    private String host;

    @Value("${mail.trace.port}")
    private String port;

    @Value("${mail.trace.mail}")
    private String mail;

    @Value("${mail.trace.password}")
    private String password;

    @Value("${mail.trace.need.trace}")
    private boolean needTrace;


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public boolean isNeedTrace() {
        return needTrace;
    }

    public void setNeedTrace(boolean needTrace) {
        this.needTrace = needTrace;
    }
}
