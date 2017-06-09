package com.taotao.sso.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbUserMapper;
import com.taotao.pojo.TbUser;
import com.taotao.pojo.TbUserExample;
import com.taotao.pojo.TbUserExample.Criteria;
import com.taotao.sso.service.RegisterService;
@Service
public class RegisterServiceImpl implements RegisterService{

	@Autowired
	private TbUserMapper userMapper ;
	
	// 注册校验
	@Override
	public TaotaoResult checkData(String param, int type) {
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		//1、2、3分别代表username、phone、email
		if(1 == type ){
			criteria.andUsernameEqualTo(param);
		}else if(2 == type ){
			criteria.andPhoneEqualTo(param);
		}else if(3 == type ){
			criteria.andEmailEqualTo(param);
		}
		List<TbUser> list = userMapper.selectByExample(example);
		if( list == null || list.isEmpty() ){
			return TaotaoResult.ok(true); // 数据库中没有数据，可以注册
		}
		return TaotaoResult.ok(false);
	}
	// 注册
	@Override
	public TaotaoResult register(TbUser user) {
		// 校验用户名、密码
		if(StringUtils.isBlank(user.getUsername()) || 
				StringUtils.isBlank(user.getPassword())){
			return TaotaoResult.build(400, "用户名或密码不正确");
		}
		// 校验用户名重复问题
		TaotaoResult result = checkData(user.getUsername(), 1);
		if(!(boolean)result.getData()){
			return TaotaoResult.build(400, "用户名重复");
		}
		//校验手机号
		if (user.getPhone() != null) {
			result = checkData(user.getPhone(), 2);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "手机号重复");
			}
		}
		//校验邮箱
		if (user.getEmail() != null) {
			result = checkData(user.getEmail(), 3);
			if (!(boolean) result.getData()) {
				return TaotaoResult.build(400, "邮箱重复");
			}
		}
		
		// 对密码进行加密md5
		user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
		//插入数据
		user.setCreated(new Date());
		user.setUpdated(new Date());
		
		
		
		userMapper.insert(user);
		
		return TaotaoResult.ok();
	}
}
