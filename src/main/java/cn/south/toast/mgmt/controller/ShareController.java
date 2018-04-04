package cn.south.toast.mgmt.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctc.wstx.util.StringUtil;

import cn.south.toast.base.dto.TestInDTO;
import cn.south.toast.base.module.Share;
import cn.south.toast.base.module.ShareExample;
import cn.south.toast.base.module.Share_;
import cn.south.toast.base.service.ShareService;
import cn.south.toast.common.dto.AppResultObj;
import cn.south.toast.common.mybatis.base.PageParameter;
import cn.south.toast.common.mybatis.query.Query;

/**
 * 
 * @author huangbh
 *
 */
@RestController
@RequestMapping("/api")
public class ShareController {

	@Autowired
	private ShareService shareService;
	
	@RequestMapping("/getshare")
	public Object addShare(@RequestBody TestInDTO inDTO, HttpServletRequest request) {
		String id = inDTO.getId();
		Share share = null;
		if (StringUtils.isNotBlank(id)) {
			share = shareService.selectByPrimaryKey(id);
		}
	
		return share;
	
	}
	
	@RequestMapping("/getShareByQuery")
	public Object getShareByQuery(@RequestBody TestInDTO inDTO) {
		String id = inDTO.getId();
		String name= inDTO.getName();
		Share share = null;
		PageParameter pageParameter =null;
		if (inDTO.getCount() != null && inDTO.getPage() != null) {
			 pageParameter = new PageParameter();
			pageParameter.setCurrentPage(inDTO.getPage());
			pageParameter.setPageSize(inDTO.getCount());
		}

		if (StringUtils.isNotBlank(name)) {
			List<Share> list = shareService.query(Query.newQuery().where(Share_.name.like("%"+name+"%")), pageParameter);
			return AppResultObj.success(list);
		}
		return AppResultObj.success();

	}

	@RequestMapping("/getShareByExample")
	public Object getShareByExample(@RequestBody TestInDTO inDTO) {
		String id = inDTO.getId();
		Share share = null;

		if (StringUtils.isNotBlank(id)) {
			ShareExample shareExample = new ShareExample();
		
			List<Share> list = shareService.selectByExample(shareExample);
			return AppResultObj.success(list);
		}

		return AppResultObj.success();

	}
}
