package cn.south.toast.mgmt.shiro;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.south.toast.base.dto.UserLoginOutDTO;
import cn.south.toast.base.enums.UserStatusEnum;
import cn.south.toast.base.service.ResourceService;
import cn.south.toast.base.service.RoleService;
import cn.south.toast.base.service.UserService;

/**
 * 
 * @author huangbh
 *
 */

public class ShiroDBRealm extends AuthorizingRealm {
	private static final Logger logger = LoggerFactory.getLogger(ShiroDBRealm.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private ResourceService resourceService;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		UserLoginOutDTO user = (UserLoginOutDTO) principals.getPrimaryPrincipal();
		String userId = user.getId();
		SimpleAuthorizationInfo info =  new SimpleAuthorizationInfo();
		//获取用户角色
		List<String> roleList = roleService.listRoleByUserId(userId);
		info.setRoles(new HashSet<String>(roleList));
		//获取用户资源
		List<String> resourceList = resourceService.listResourceByUserId(userId);
		
		//类型转换
		Set<Permission> permissionSet = resourceList.stream().map(tmp -> {
			Permission permission = new URLWildcardPermission(tmp);
			return permission;
		}).collect(Collectors.toSet());
		
		info.setObjectPermissions(permissionSet);
		return info;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken)authcToken;
        
		String username = token.getUsername();
        if(StringUtils.isEmpty(username)){
        	return null;
        }
        
        long startTime = System.currentTimeMillis();
		logger.debug("---------------------获取资源菜单开始");
        //查询用户及用户的菜单资源
        UserLoginOutDTO userLoginOutDTO = userService.getUserLoginOutDTO(username);
        
    	long endTime = System.currentTimeMillis();
		long consume = endTime - startTime;
		logger.debug("---------------------获取资源菜单结束，耗时"+ consume);
        
        if(userLoginOutDTO == null){
        	throw new UnknownAccountException();
        }
        
//    	if(userLoginOutDTO.getLocked()){
//    		throw new LockedAccountException();
//    	}
    	
        //帐号被停用
    	if(userLoginOutDTO.getStatus() == null || userLoginOutDTO.getStatus() == UserStatusEnum.ACCOUNT_DISTABLE.getCode()){
    		throw new DisabledAccountException();
    	}
    	
    	startTime = System.currentTimeMillis();
  		logger.debug("---------------------shiro开始");
    	AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(userLoginOutDTO, userLoginOutDTO.getPassword(), getName());
    	
    	endTime = System.currentTimeMillis();
		consume = endTime - startTime;
		logger.debug("---------------------shiro结束，耗时"+ consume);
    	return authcInfo;
	}
}
