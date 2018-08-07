package com.eric.util.version;

import com.eric.util.encryption.MD5FileUtils;
import sun.misc.Launcher;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 自动添加js和cs版本号工具类
 * @version 1.0.0
 * @author yangkang
 */
public class AddJsAndCssVersionUtils {

    /**文件路径
     * 默认页面jsp
     */
    private static String path = "/WEB-INF/jsp";

    /**
     * 默认js文件路径
     */
    private static String jsPath = "/front";

    /**
     * 初始化
     * @Version:
     */
    public static void execute() {
        //获取当前类的路径
        String realPath = AddJsAndCssVersionUtils.class.getResource("/").getPath();
        //页面路径
        String finalViewPath = realPath.replace("/WEB-INF/classes/",path);
        //js路径
        String finalJsPath = realPath.replace("/WEB-INF/classes/",jsPath);
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        String version = df.format(date);
        Map<String, String> map = getJsMap(finalJsPath);
        addVersionToVm(finalViewPath, version,map);
    }

    /**
     * 获取js与css版本map
     * @Version:
     * @param path
     * @return
     */
    public static Map<String, String> getJsMap(String path) {
        Map<String, String> map = new HashMap<String, String>();
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null)
            return map;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getVersionMap(files[i].getAbsolutePath(), map);
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                // 如果是符合条件的文件，则添加版本信息
                if (strFileName.endsWith(".js")) {
                    try {
                        String md5 = MD5FileUtils.getFileMD5String(files[i]);
                        map.put(files[i].getName(), md5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return map;
    }

    /**
     * 遍历web目录中的vm文件，给js和css的引用加上版本号
     * @param path 路径
     * @param version 默 认版本
     * @param map 版本map
     */
    private static void addVersionToVm(String path,String version,Map<String,String> map){
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null){
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addVersionToVm(files[i].getAbsolutePath(), version,map);
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                // 如果是符合条件的文件，则添加版本信息
                if (strFileName.endsWith(".vm")
                        || strFileName.endsWith(".html")
                        || strFileName.endsWith(".jsp")) {

                    InputStream is = null;
                    OutputStream os = null;
                    List<String> contentList = new ArrayList<String>();

                    // 读文件
                    try {
                        is = new FileInputStream(files[i]);
                        Reader r = new InputStreamReader(is,"UTF-8");
                        BufferedReader br = new BufferedReader(r);
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            String modLine = getModLine(line, version,map);
                            if (modLine != null) {
                                line = modLine;
                            }
                            line = line + "\r\n";
                            contentList.add(line);
                        }
                        // 关闭流
                        br.close();
                        r.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != is) {
                            try {
                                is.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    // 写文件
                    try {
                        os = new FileOutputStream(files[i]);
                        Writer w = new OutputStreamWriter(os,"UTF-8");
                        BufferedWriter bw = new BufferedWriter(w);
                        for (Iterator<String> it = contentList.iterator();it.hasNext();) {
                            String line = it.next();
                            bw.write(line);
                        }
                        // 更新到文件
                        bw.flush();
                        // 关闭流
                        bw.close();
                        w.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (null != os) {
                            try {
                                os.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取js 与css map key 文件名   value md5值
     * @Version:
     * @param path 路径
     * @param map map
     */
    public static void getVersionMap(String path, Map<String, String> map) {
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null){
            return;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                getVersionMap(files[i].getAbsolutePath(), map);
            } else {
                String strFileName = files[i].getAbsolutePath().toLowerCase();
                // 如果是符合条件的文件，则添加版本信息
                if (strFileName.endsWith(".js") || strFileName.endsWith(".css")) {
                    try {
                        String md5 = MD5FileUtils.getFileMD5String(files[i]);
                        map.put(strFileName, md5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     *
     * 查找文件需要的版本号的js 与css 行
     * @param line 行字符
     * @param version 默认版本
     * @param map md5 map
     * @return
     */
    private static String getModLine(String line, String version, Map<String, String> map) {
        // 增加js版本
        line = line.trim();
        if (line.startsWith("<script") && line.endsWith("</script>")) {
            int pos = line.indexOf(".js");
            // 默认加时间为版本号
            String modLine = line.substring(0, pos) + ".js?1=" + version+ "\"></script>";
            String[] nameStr = line.split("/");
            for(int i=0;i<nameStr.length;i++){
                String jsname = nameStr[i];
                if(jsname.indexOf(".js")!=-1){
                    int jsindex = jsname.indexOf(".js") +3;
                    String  key = jsname.substring(0, jsindex);
                    String md5=map.get(key);
                    if(md5!=null){
                        modLine = line.substring(0, pos) + ".js?version=" + md5
                                + "\"></script>";
                    }
                }
            }
            return modLine;
        } else if (line.startsWith("<link type=\"text/css\" rel=\"stylesheet\"")) {
            int pos = line.indexOf(".css");
            String modLine = line.substring(0, pos) + ".css?version=" + version+ "\"/>";
            String[] nameStr = line.split("/");
            for(int i=0;i<nameStr.length;i++){
                String jsname = nameStr[i];
                if(jsname.indexOf(".css")!=-1){
                    int jsindex = jsname.indexOf(".js") +4;
                    String  key = jsname.substring(0, jsindex);
                    String md5=map.get(key);
                    if(md5!=null){
                        modLine = line.substring(0, pos) + ".css?version=" + md5+ "\"/>";
                    }
                }
            }
            return modLine;
        } else {
            return null;
        }
    }


    public static void main(String[] args) {
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i].toExternalForm());
        }
        System.out.println(System.getProperty("sun.boot.class.path"));
    }
}
