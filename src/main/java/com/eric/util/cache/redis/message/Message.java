package com.eric.util.cache.redis.message;

import java.io.Serializable;
import java.util.Date;

/**
 * description:消息对象
 * author:Eric
 * Date:16/9/9
 * Time:15:22
 * version 1.0.0
 */
public class Message implements Serializable{


    private Integer smsId;//消息记录ID

    private Date createDate;//消息创建时间

    private Date actualSendTime;//消息实际发送时间

    private Integer sendStatus;//消息发送状态 0 未发送 1 发送成功 2 发送失败

    private String content;//消息内容


    public Integer getSmsId() {
        return smsId;
    }

    public void setSmsId(Integer smsId) {
        this.smsId = smsId;
    }

    public Date getCreateDate() {
        return createDate==null?null:(Date) createDate.clone();
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate == null?null:createDate;
    }

    public Date getActualSendTime() {
        return actualSendTime==null?null:(Date) actualSendTime.clone();
    }

    public void setActualSendTime(Date actualSendTime) {

        this.actualSendTime = actualSendTime ==null?null:actualSendTime;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
