package com.cnwalking.twochat.utils;


import com.cnwalking.twochat.common.Response;

public class ResponseUtils {
    public static <T> Response<T> returnSuccess(T data) {
        Response<T> response = new Response<>();
        response.setMessage("成功");
        response.setData(data);
        response.setCode(200);
        return response;
    }

    public static Response returnDefaultSuccess() {
        Response response = new Response();
        response.setMessage("操作成功");
        response.setCode(200);
        return response;
    }


    public static <T> Response<T> returnInfo(int code, String message,T t) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(t);
        return response;
    }

    public static Response returnError(int code, String message) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public static Response returnDefaultError() {
        Response response = new Response();
        response.setCode(400);
        response.setMessage("系统内部异常");
        return response;
    }

    public static <T> Response<T> returnError(int code, String message, T data) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
