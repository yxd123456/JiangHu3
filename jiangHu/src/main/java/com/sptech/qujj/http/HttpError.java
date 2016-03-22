/**  
 * Copyright © 2015 蓝色互动. All rights reserved.
 *
 * @Title HttpError.java
 * @Prject JinZaiTong
 * @Package com.bm.jinzaitong.http
 * @Description TODO
 * @author liwb  
 * @date 2015-4-1 下午5:03:17
 * @version V1.0  
 */
package com.sptech.qujj.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright © 2015 蓝色互动. All rights reserved.
 * 
 * @Title HttpError.java
 * @Prject JinZaiTong
 * @Package com.bm.jinzaitong.http
 * @Description TODO
 * @author liwenbo
 * @date 2015-4-1 下午5:03:17
 * @version V1.0
 */
public class HttpError {
	public static Map<String, String> map = new HashMap<String, String>() {
		{

			put("api_phone_err", "手机号错误");			
			put("api_type_err", "用户类型错误");
			put("api_number_err", "类型错误");
			put("api_login_empty", "帐号不存在");
			put("api_login_pwd_err", "密码错误");
			put("api_login_re_pwd_err", "确认密码错误");
			put("api_socre_err",  "规则分出错");
			put("api_score_err","积分不足");		
			put("api_code_err", "验证码错误");
			put("api_id_err", "id错误");
			put("api_third_err", "第三方接口错误");
			put("api_u_id_err", "用户id错误");
			put("api_o_type_err", "产品类型错误");
			put("api_content_err",  "反馈的内容错误");
			
			put("check_type_error", "文件类型错误");
			put("check_size_error", "文件大小错误");

			put("Exception", "程序异常");
			put("exception", "服务器错误");
			put("empty", "用户在云端认证失败");
			put("data_empty", "没有数据了");
			put("third_err", "第三方接口错误");
			put("exists", "你今天已经领取,请明天再来领取");
			put("atten_exists", "你已经关注过");
			put("mobi_err", "参数错误");
			

		}
	};

	public static String getError(String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		} else {
			return "未知错误！";
		}
	}
}
