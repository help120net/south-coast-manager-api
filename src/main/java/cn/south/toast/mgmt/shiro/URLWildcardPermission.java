package cn.south.toast.mgmt.shiro;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.Permission;
import org.springframework.util.AntPathMatcher;

/**
 * 
 * @author huangbh
 *
 */
public class URLWildcardPermission implements Permission, Serializable {

	private static final long serialVersionUID = 1L;

	public static final AntPathMatcher pathMatcher = new AntPathMatcher();
	
	private String wildcardString;
	
	public URLWildcardPermission(String wildcardString){
		this.wildcardString = wildcardString;
	}

	public boolean implies(Permission permission) {
		if(StringUtils.isBlank(wildcardString)){
			return Boolean.FALSE;
		}
		return pathMatcher.match(wildcardString, permission.toString());
	}
	
	@Override
	public String toString() {
		return wildcardString;
	}

}
