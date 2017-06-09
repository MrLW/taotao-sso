package com.taotao.sso.service.impl;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.dao.JedisClient;
import com.taotao.sso.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${REDIS_SESSION_KEY}")
	private String REDIS_SESSION_KEY ;
	
	@Value("${REDIS_SESSION_EXPIRE_SECOND}")
	private Integer REDIS_SESSION_EXPIRE_SECOND ;
	
	@Override
	public TaotaoResult login(String username, String password, HttpServletRequest request,
			HttpServletResponse response) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> userList = tbUserMapper.selectByExample(example );
		if(userList != null && userList.size() > 0 ){
			TbUser user = userList.get(0);
			if (user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) { // 登录成功
				// 生成token
				String token = UUID.randomUUID().toString();
				
				// 安全,将密码设置为null
				user.setPassword(null);
				
				// 写入redis ----> 其实相当于写入session
				jedisClient.set(REDIS_SESSION_KEY + ":" + token, JsonUtils.object2Json(user));
				// 设置过期时间
				jedisClient.expire(REDIS_SESSION_KEY + ":" + token, REDIS_SESSION_EXPIRE_SECOND) ;
				// 写入cookie
				CookieUtils.setCookie(request, response, "TT_TOKEN", token );
				
				return TaotaoResult.ok() ;
				
			}else {
				return TaotaoResult.build(400, "用户名或密码错误");
			}
		}else {
			return TaotaoResult.build(400, "用户名或密码错误");
		}
	}
	
	// 根据token查询用户
	@Override
	public TaotaoResult getUserByToken(String token) {
		String json = jedisClient.get(REDIS_SESSION_KEY + ":" + token ) ;
		//判断是否查询到结果
		if (StringUtils.isBlank(json)) {
			return TaotaoResult.build(400, "用户session已经过期");
		}
		TbUser user = JsonUtils.jsonToPojo(json, TbUser.class);
		// 注意：重新设置过期时间（其它模块）
		jedisClient.expire(REDIS_SESSION_KEY + ":" + token , REDIS_SESSION_EXPIRE_SECOND) ;
		
		return TaotaoResult.ok(user);
	}

}
