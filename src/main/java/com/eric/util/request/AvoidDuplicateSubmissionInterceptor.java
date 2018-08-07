package com.eric.util.request;

import com.eric.util.model.JsonResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * 防止请求重复提交拦截器
 */
public class AvoidDuplicateSubmissionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger LOGGER = Logger.getLogger(AvoidDuplicateSubmissionInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //可扩展 过滤指定的请求 判断用户是否登录等
        if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 取得当前方法名
                Method method = handlerMethod.getMethod();

                // 取得方法中AvoidDuplicateSubmission的注释
                AvoidDuplicateSubmission annotation = method.getAnnotation(AvoidDuplicateSubmission.class);
                if (annotation != null) {
                    String tokenName = getTokenName(request, handlerMethod);
                    LOGGER.info("=====" + tokenName);
                    //如果存在表示前一个请求还没结束
                    Object tokenObject = request.getSession(false).getAttribute(tokenName);
                    if (tokenObject != null && StringUtils.isNotBlank(tokenObject.toString())) {
                        // 请求提交失败
                        LOGGER.error("please don't repeat submit,["+", url:" + request.getServletPath() + ",tokenName:" + tokenName + "]");
                        writeResponse(response);
                        return false;
                    }
                    // 添加请求对应的token
                    request.getSession(false).setAttribute(tokenName, UUID.randomUUID().toString());
                }
        }
        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 取得方法中AvoidDuplicateSubmission的注解
            AvoidDuplicateSubmission annotation = method.getAnnotation(AvoidDuplicateSubmission.class);
            if (annotation != null) {
                String tokenName = getTokenName(request, handlerMethod);
                LOGGER.info("----" + tokenName);
                request.getSession(false).removeAttribute(tokenName);
            }
        }
    }

    /**
     * 获取请求方法指定的tokenName
     * @param request
     * @param handlerMethod
     * @return
     */
    private String getTokenName(HttpServletRequest request, HandlerMethod handlerMethod) {
        String className = handlerMethod.getBeanType().getName();
        String classMethod = handlerMethod.getMethod().getName();
        String params = Json.toJson(request.getParameterMap());
        String requestUrl = request.getRequestURI();
        return className + "." + classMethod + "." + requestUrl + "." + params;
    }

    private void writeResponse(HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(new JsonResult(JsonResult.STATUS.FAIL).put("type", "DuplicateSubmission").put("message", "数据处理中，请勿重复提交").toJson());
        }
    }

}