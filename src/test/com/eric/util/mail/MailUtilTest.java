package com.eric.util.mail;

import com.eric.util.BaseTest;
import org.apache.commons.mail.EmailAttachment;
import org.junit.Test;

import javax.mail.internet.MimeUtility;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description:邮件工具测试类
 * author:yangkang
 * Date:16/8/24
 * Time:15:41
 * version 1.0.0
 */
public class MailUtilTest extends BaseTest{

    @Test
    public void testSendMail(){
        Map<String,String> mailMap = new HashMap<String, String>();
        mailMap.put("mail_hostname","smtp.163.com");
        mailMap.put("mail_from","eric_ykk@163.com");
        mailMap.put("mail_password","ericauth163com");
        List<String> userCodeMail = new ArrayList<String>();
        userCodeMail.add("363784482@qq.com");
        List<String> copyUserCodeMail = new ArrayList<String>();
        copyUserCodeMail.add("363784482@qq.com");
        String content  = "hello world!";
        String subject  = "hello mailUtil";
        String realName = "Eric";
        try {
            //获取classes目录路径
            String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            File file = new File(classPath+"/mail.txt");
            FileOutputStream outputStream = new FileOutputStream(file);
            //获取输出通道
            FileChannel outChannel = outputStream.getChannel();
            String mailFileText = "hello attachFile!";
            //创建缓冲区
            ByteBuffer buffer =  ByteBuffer.wrap(mailFileText.getBytes("UTF-8"));
            //flip方法让缓冲区可以将新读入的数据写入另一个通道
            buffer.flip();
            //从输出通道中将数据写入缓冲区
            outChannel.write(buffer);

            List<EmailAttachment> emailAttachmentList = new ArrayList<EmailAttachment>();
            EmailAttachment emailAttachment = new EmailAttachment();
            emailAttachment.setPath(file.getPath());
            emailAttachment.setDescription("hello attachFile");
            //设置附件名 处理中文乱码
            try {
                emailAttachment.setName(MimeUtility.encodeText("hello attachFile","UTF-8",null));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            emailAttachmentList.add(emailAttachment);
            //发送邮件
            MailUtils.sendMail(mailMap,emailAttachmentList,userCodeMail,copyUserCodeMail,content,subject,realName);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
