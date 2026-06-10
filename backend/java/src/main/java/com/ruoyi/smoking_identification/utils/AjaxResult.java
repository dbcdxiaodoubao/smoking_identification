package com.ruoyi.smoking_identification.utils;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("返回体对象")
public class AjaxResult<T> {

    @ApiModelProperty("操作代码")
    private Integer code;
    @ApiModelProperty("操作信息")
    private String msg;
    @ApiModelProperty("数据")
    private T data;



    /**
     * 成功的返回结果
     *
     * @param msg  成功信息
     * @param data 返回的值
     * @param <T>  返回值的泛型
     * @return 返回AjaxResult对象
     */
    public static <T> AjaxResult<T> success(String msg, T data) {
        return new AjaxResult<T>(200, msg, data);
    }


    /**
     * 成功的返回结果
     *
     * @param data 返回的值
     * @param <T>  返回值的泛型
     * @return 返回AjaxResult对象
     */
    public static <T> AjaxResult<T> success(T data) {
        return new AjaxResult<T>(200, "操作成功", data);
    }

    public static <T> AjaxResult<T> success() {
        return new AjaxResult<T>(200, "操作成功",null);
    }

    /**
     * 失败的返回结果
     *
     * @param msg  成功信息
     * @param data 返回的值
     * @param <T>  返回值的泛型
     * @return 返回AjaxResult对象
     */
    public static <T> AjaxResult<T> error(String msg, T data) {
        return new AjaxResult<T>(500, msg, data);
    }

    public static <T> AjaxResult<T> error() {
        return new AjaxResult<T>(500, "操作失败", null);
    }

    /**
     * 失败的返回结果
     *
     * @param data 返回的值
     * @param <T>  返回值的泛型
     * @return 返回AjaxResult对象
     */
    public static <T> AjaxResult<T> error(T data) {
        return new AjaxResult<T>(500, "操作失败", data);
    }

    /**
     * 认证失败的返回结果
     *
     * @param msg  成功信息
     * @param data 返回的值
     * @param <T>  返回值的泛型
     * @return 返回AjaxResult对象
     */
    public static <T> AjaxResult<T> unAuth(String msg, T data) {
        return new AjaxResult<T>(401, msg, data);
    }
}