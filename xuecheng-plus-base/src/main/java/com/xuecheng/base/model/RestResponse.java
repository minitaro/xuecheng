package com.xuecheng.base.model;

import lombok.Data;
import lombok.ToString;

/**
* @description 通用响应dto
* @author TMC
* @date 2023/2/11 22:01
* @version 1.0
*/
@Data
@ToString
public class RestResponse<T> {

    /**
     * 响应编码,0为正常,-1为错误
     */
    private int code;

    /**
     * 响应提示信息
     */
    private String msg;

    /**
     * 响应内容
     */
    private T result;

    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
    * @description 添加错误响应信息
    * @param msg 响应信息
    * @return com.xuecheng.base.model.RestResponse<T>
    * @author TMC
    * @date 2023/2/11 22:10
    */
    public static <T> RestResponse<T> validfail(String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        return response;
    }
    public static <T> RestResponse<T> validfail(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setMsg(msg);
        response.setResult(result);
        return response;
    }

    /**
    * @description 添加正常响应数据
    * @param result 响应内容
    * @return com.xuecheng.base.model.RestResponse<T>
    * @author TMC
    * @date 2023/2/11 22:13
    */
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }
    public static <T> RestResponse<T> success(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }
    public static<T> RestResponse<T> success() {
        return new RestResponse<T>();
    }

    /**
    * @description 判断响应是否正常
    * @return java.lang.Boolean
    * @author TMC
    * @date 2023/2/11 22:17
    */
    public Boolean isSuccessful() {
        return this.code == 0;
    }
}
