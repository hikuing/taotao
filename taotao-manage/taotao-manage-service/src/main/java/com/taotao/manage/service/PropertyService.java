package com.taotao.manage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 读取资源文件中的内容
 * @author zhang
 *
 */
@Service
public class PropertyService {

	//@Value注解为spring提供，可以获取资源文件中的内容
	@Value("${image.REPOSITORY_PATH}")
	public String REPOSITORY_PATH;
	
	@Value("${image.IMAGE_BASE_URL}")
	public String IMAGE_BASE_URL;
}
