package com.eric.util.model;

import lombok.Data;

import java.util.Date;

@Data
public class MailPO {
    private Long id;

    private String messageId;

    private Date sentDate;

    private String subject;

    private Date receivedDate;

    private Date createAt;

    private String sender;

    private String receiver;

    private String uid;

    private String mailContent;

    private byte mailType;

    private byte status;
}