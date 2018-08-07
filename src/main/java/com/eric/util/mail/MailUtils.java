package com.eric.util.mail;

import com.eric.util.model.MailConfigBean;
import com.eric.util.model.MailPO;
import com.sun.mail.pop3.POP3Folder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * description:邮件工具类  使用apache commons-email
 * author:yangkang
 * Date:16/8/24
 * Time:13:52
 * version 1.0.0
 */
@Slf4j
public class MailUtils {

    @Resource
    private MailConfigBean mailConfigBean;


    public List<MailPO> readMail() {

        Store store = null;
        Folder folder = null;
        List<MailPO> mailPOList = new ArrayList<>();
        try {
            // 准备连接服务器的会话信息
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", mailConfigBean.getProtocol());
            props.setProperty("mail.pop3.host", mailConfigBean.getHost());
            props.setProperty("mail.pop3.port", mailConfigBean.getPort());

            // 创建Session实例对象
            Session session = Session.getInstance(props);
            // 创建IMAP协议的Store对象
            store = session.getStore(mailConfigBean.getProtocol());

            // 连接邮件服务器 password为授权码
            store.connect(mailConfigBean.getMail(), mailConfigBean.getPassword());

            // 获得收件箱
            folder = store.getFolder("INBOX");
            // 以读写模式打开收件箱
            folder.open(Folder.READ_WRITE);
            //获取邮件
            Message[] messages = folder.getMessages();
            //解析邮件
            POP3Folder inbox = (POP3Folder) folder;
            if (messages != null && messages.length > 0) {
                mailPOList = parseMessage(inbox, messages);
                //删除已经读取邮件
                deleteMessage(messages);
            }
        } catch (Exception e) {
            log.error("读取邮件失败：" + e.getMessage(), e);
        } finally {
            //释放资源
            try {
                if (folder != null) {
                    // 刷新邮件
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return mailPOList;
    }


    /**
     * 解析邮件
     *
     * @param messages 要解析的邮件列表
     */
    private List<MailPO> parseMessage(POP3Folder inbox, Message... messages) throws MessagingException, IOException {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        List<MailPO> mailPOList = new ArrayList<>();
        // 解析邮件
        for (Message message : messages) {
            try {
                MimeMessage msg = (MimeMessage) message;
                msg.setFlag(Flags.Flag.SEEN, true);
                MailPO mailPO = new MailPO();
                //ID
                mailPO.setMessageId(msg.getMessageID().replace("$", ""));
                //UID
                mailPO.setUid(inbox.getUID(message));
                InternetAddress address = getFrom(msg);
                //发件人
                mailPO.setSender(address.getAddress());
                //发件时间
                mailPO.setSentDate(msg.getSentDate());
                //标题
                String subject = decodeText(msg.getSubject()).trim().replace("$", "");
                if (StringUtils.isBlank(subject)) {
                    continue;
                }
                mailPO.setSubject(subject);
                //收件人
                mailPO.setReceiver(getReceiveAddress(msg, null));
                //收件日期
                mailPO.setReceivedDate(msg.getReceivedDate());
                //邮件文本内容
                StringBuffer content = new StringBuffer();
                handleMailTextContent(message, content);
                mailPO.setMailContent(content.toString().replace("$", ""));
                //邮件类型  外部邮件  or 内部邮件
                if (mailPO.getSender().contains("ximalaya.com")) {
                    mailPO.setMailType((byte) 1);
                } else {
                    mailPO.setMailType((byte) 0);
                }
                mailPOList.add(mailPO);
            } catch (Exception e) {
                log.error("邮件解析失败：" + e.getMessage(), e);
            }
        }
        return mailPOList;
    }


    /**
     * 获得邮件发件人
     *
     * @param msg 邮件内容
     * @return 地址
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    private static InternetAddress getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        Address[] froms = msg.getFrom();
        if (froms.length < 1) {
            throw new MessagingException("邮件" + decodeText(msg.getSubject()) + "没有发件人!");
        }
        return (InternetAddress) froms[0];
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     * <p>Message.RecipientType.TO  收件人</p>
     * <p>Message.RecipientType.CC  抄送</p>
     * <p>Message.RecipientType.BCC 密送</p>
     *
     * @param msg  邮件内容
     * @param type 收件人类型
     * @return 收件人1 <邮件地址1>, 收件人2 <邮件地址2>, ...
     * @throws MessagingException
     */
    private static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException, UnsupportedEncodingException {
        StringBuilder receiveAddress = new StringBuilder();
        Address[] addresses;
        if (type == null) {
            addresses = msg.getAllRecipients();
        } else {
            addresses = msg.getRecipients(type);
        }

        if (addresses == null || addresses.length < 1) {
            throw new MessagingException("邮件" + decodeText(msg.getSubject()) + "没有收件人!");
        }
        for (Address address : addresses) {
            InternetAddress internetAddress = (InternetAddress) address;
            receiveAddress.append(internetAddress.toUnicodeString()).append(",");
        }
        //删除最后一个逗号
        receiveAddress.deleteCharAt(receiveAddress.length() - 1);

        return receiveAddress.toString();
    }

    /**
     * 判断邮件中是否包含附件
     *
     * @return 邮件中存在附件返回true，不存在返回false
     * @throws MessagingException
     * @throws IOException
     */
    private static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                if (disposition != null && (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.contains("application") || contentType.contains("name")) {
                        flag = true;
                    }
                }
                if (flag) {
                    break;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 文本解码
     *
     * @param encodeText 解码MimeUtility.encodeText(String text)方法编码后的文本
     * @return 解码后的文本
     * @throws UnsupportedEncodingException
     */
    private static String decodeText(String encodeText) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(encodeText)) {
            return "";
        } else {
            return MimeUtility.decodeText(encodeText);
        }
    }


    /**
     * 删除邮件
     *
     * @param messages 要解析的邮件列表
     */
    public static void deleteMessage(Message... messages) throws MessagingException, IOException {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }
        // 邮件删除
        for (Message message : messages) {
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    /**
     * 封装邮件文本内容
     * <p>
     * 这里只考虑邮件文本内容  邮件附件大或者多会严重影响解析速度  这里不做处理
     * multipart/mixed：附件 不过滤
     * <p>
     * multipart/related：内嵌资源
     * <p>
     * multipart/alternative：纯文本与超文本共存
     *
     * @param part      邮件体
     * @param contentSb 存储邮件文本内容的字符串
     * @throws MessagingException
     * @throws IOException
     */
    public static void handleMailTextContent(Part part, StringBuffer contentSb) throws MessagingException, IOException {
        //如果是文本类型的附件，这里直接跳过不做处理
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        //只处理纯文本  contentType为text/html则不处理
        if (part.isMimeType("text/plain") && !isContainTextAttach) {
            contentSb.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            handleMailTextContent((Part) part.getContent(), contentSb);
        } else if (part.isMimeType("multipart/related") || part.isMimeType("multipart/alternative")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                handleMailTextContent(bodyPart, contentSb);
            }
        }

    }
}
