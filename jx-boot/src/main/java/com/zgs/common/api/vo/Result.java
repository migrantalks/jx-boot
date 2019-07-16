package com.zgs.common.api.vo;

import java.io.Serializable;


import com.zgs.common.constant.CommonConstant;
import lombok.Data;

/**
 *  接口返回数据格式
 * @author zgs
 */
@Data
public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 返回处理消息
	 */
	private String message;

	/**
	 * 返回代码
	 */
	private Integer code;
	
	/**
	 * 返回数据对象 data
	 */
	private T data;

	/**
	 * 时间戳
	 */
	private long timestamp = System.currentTimeMillis();


	
	public static Result<Object> fail(String msg) {
		return fail(ResultCode.INTERNAL_SERVER_ERROR.code(), msg);
	}
	
	public static Result<Object> fail(int code, String msg) {
		Result<Object> r = new Result<Object>();
		r.setCode(code);
		r.setMessage(msg);
		return r;
	}
	
	public static Result<Object> success(String msg) {
		Result<Object> r = new Result<Object>();
		r.setCode(ResultCode.SUCCESS.code());
		r.setMessage(msg);
		return r;
	}
	
	public static Result<Object> success(Object obj) {

		Result<Object> r = new Result<Object>();
		r.setCode(ResultCode.SUCCESS.code());
		r.setData(obj);
		return r;
	}
}
