package com.eric.util.encryption;


import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 描述: 文本相似度计算
 *
 * @author eric
 * @create 2018-03-18 下午2:38
 */
public class SimilarityUtil {

    /**
     * 余弦定理计算两个字符串的相似性
     *
     * @param doc1
     * @param doc2
     * @return
     */
    public static double getSimilarity(String doc1, String doc2) {
        if (StringUtils.isNotBlank(doc1) && StringUtils.isNotBlank(doc2)) {
            Map<Integer, int[]> algorithmMap = new HashMap<>();
            //将两个字符串中的中文字符以及出现的总数封装到，AlgorithmMap中
            for (int i = 0; i < doc1.length(); i++) {
                char d1 = doc1.charAt(i);
                //标点和数字不处理
                if (isHanZi(d1)) {
                    //保存字符对应的GB2312编码
                    int charIndex = getGB2312Id(d1);
                    if (charIndex != -1) {
                        int[] fq = algorithmMap.get(charIndex);
                        if (fq != null && fq.length == 2) {
                            //已有该字符，加1
                            fq[0]++;
                        } else {
                            fq = new int[2];
                            fq[0] = 1;
                            fq[1] = 0;
                            //新增字符入map
                            algorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }

            for (int i = 0; i < doc2.length(); i++) {
                char d2 = doc2.charAt(i);
                if (isHanZi(d2)) {
                    int charIndex = getGB2312Id(d2);
                    if (charIndex != -1) {
                        int[] fq = algorithmMap.get(charIndex);
                        if (fq != null && fq.length == 2) {
                            fq[1]++;
                        } else {
                            fq = new int[2];
                            fq[0] = 0;
                            fq[1] = 1;
                            algorithmMap.put(charIndex, fq);
                        }
                    }
                }
            }

            Iterator<Integer> iterator = algorithmMap.keySet().iterator();
            double sqDoc1 = 0;
            double sqDoc2 = 0;
            double denominator = 0;
            while (iterator.hasNext()) {
                int[] c = algorithmMap.get(iterator.next());
                denominator += c[0] * c[1];
                sqDoc1 += c[0] * c[0];
                sqDoc2 += c[1] * c[1];
            }
            //余弦计算
            return denominator / Math.sqrt(sqDoc1 * sqDoc2);
        } else {
            throw new NullPointerException(" the Document is null or have not chars!!");
        }
    }

    private static boolean isHanZi(char ch) {
        // 判断是否汉字
        return (ch >= 0x4E00 && ch <= 0x9FA5);
    }

    /**
     * 根据输入的Unicode字符，获取它的GB2312编码或者ascii编码，
     *
     * @param ch 输入的GB2312中文字符或者ASCII字符(128个)
     * @return ch在GB2312中的位置，-1表示该字符不认识
     */
    private static short getGB2312Id(char ch) {
        try {
            byte[] buffer = Character.toString(ch).getBytes("GB2312");
            if (buffer.length != 2) {
                // 正常情况下buffer应该是两个字节，否则说明ch不属于GB2312编码，故返回'?'，此时说明不认识该字符
                return -1;
            }
            // 编码从A1开始，因此减去0xA1=161
            int b0 = (buffer[0] & 0x0FF) - 161;
            int b1 = (buffer[1] & 0x0FF) - 161;
            // 第一个字符和最后一个字符没有汉字，因此每个区只收16*6-2=94个汉字
            return (short) (b0 * 94 + b1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 使用Jaccard相似系数计算两个字符数组的相似性
     *
     * @param arrayX 字符数组X
     * @param arrayY 字符数组Y
     * @return
     */
    public static double getJaccardSimilarity(String[] arrayX, String[] arrayY) {
        double sim;
        if (arrayX != null && arrayY != null && arrayX.length > 0 && arrayY.length > 0) {
            sim = jaccardSimilarity(Arrays.asList(arrayX), Arrays.asList(arrayY));
        } else {
            throw new IllegalArgumentException("The arguments x and y must be not NULL and either x or y must be non-empty.");
        }
        return sim;
    }

    /**
     * 计算Jaccard相似系数 即两个集合的交集除以两个集合的并集
     *
     * @param listX
     * @param listY
     * @return
     */
    private static double jaccardSimilarity(List<String> listX, List<String> listY) {

        if (listX.size() == 0 || listY.size() == 0) {
            return 0.0;
        }
        // 计算集合x和集合y的并集
        Set<String> unionXY = new HashSet<>(listX);
        unionXY.addAll(listY);
        // 计算集合x和集合y的交集
        Set<String> intersectionXY = new HashSet<>(listX);
        intersectionXY.retainAll(listY);

        return (double) intersectionXY.size() / (double) unionXY.size();
    }
}
