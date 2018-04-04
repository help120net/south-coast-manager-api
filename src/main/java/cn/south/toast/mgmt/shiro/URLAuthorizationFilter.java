package cn.south.toast.mgmt.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
/**
 * 
 * @author huangbh
 *
 */
public class URLAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		HttpServletRequest httpRequest = ((HttpServletRequest)request);
		//获取URI
		String uri = httpRequest.getRequestURI();
		Permission permission = new URLWildcardPermission(uri);
		
		Subject subject = getSubject(request, response);
		
		if(subject.isPermitted(permission)){
			return true;
		}
		return false;
	}
	
}
