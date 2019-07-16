package com.zgs.modules.shiro.authc.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import com.zgs.common.exception.ServiceException;
import com.zgs.common.util.oConvertUtils;

/**
 * JWT工具类
 * @author zgs
 **/
public class JwtUtil {

	// 过期时间30分钟
	public static final long EXPIRE_TIME = 30 * 60 * 1000;

	/**
	 * 校验token是否正确
	 *
	 * @param token  密钥
	 * @param secret 用户的密码
	 * @return 是否正确
	 */
	public static boolean verify(String token, String userId, String secret) {
		try {
			// 根据密码生成JWT效验器
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withClaim("userId", userId).build();
			// 效验TOKEN
			DecodedJWT jwt = verifier.verify(token);
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	/**
	 * 获得token中的信息无需secret解密也能获得
	 *
	 * @return token中包含的用户名
	 */
	public static String getTokenInfo(String token, String key) {
		try {
			DecodedJWT jwt = JWT.decode(token);
			return jwt.getClaim(key).asString();
		} catch (JWTDecodeException e) {
			return null;
		}
	}

	/**
	 * 生成签名,5min后过期
	 *
	 * @param userId 用户名
	 * @param secret   用户的密码
	 * @return 加密的token
	 */
	public static String sign(String userId, String username, String secret) {
		Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
		Algorithm algorithm = Algorithm.HMAC256(secret);
		// 附带username信息
		return JWT.create().withClaim("userId", userId).withClaim("username", username).withExpiresAt(date).sign(algorithm);

	}

	/**
	 * 根据request中的token获取用户账号
	 * 
	 * @param request
	 * @return
	 * @throws ServiceException
	 */
	public static String getUserToken(HttpServletRequest request, String key) throws ServiceException {
		String accessToken = request.getHeader("X-Access-Token");
		String username = getTokenInfo(accessToken, key);
		if (oConvertUtils.isEmpty(username)) {
			throw new ServiceException("未获取到用户");
		}
		return username;
	}
}
