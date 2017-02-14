package com.taotao.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.taotao.common.bean.EasyUIResult;
import com.taotao.common.httpclient.ApiService;
import com.taotao.manage.pojo.Content;

@Service
public class IndexService {

	@Autowired
	private ApiService apiService;
	
	private static final ObjectMapper MAPPER = new ObjectMapper();

	/**
	 *查询大广告
	 * @return
	 */
	public String queryAd1() {
		try {
			String json = this.apiService.doGet("http://manage.taotao.com/rest/api/content?categoryId=45&rows=6");
			
			EasyUIResult ui = EasyUIResult.formatToList(json, Content.class);
			
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
			
			List<Content> contents = (List<Content>) ui.getRows();
			
			for (Content content : contents) {
				Map<String, Object> map = new HashMap<>();
				map.put("srcB", content.getPic2());
				map.put("height", 240);
				map.put("alt","");
				map.put("width", 670);
				map.put("src", content.getPic());
				map.put("widthB", 550);
				map.put("href", content.getUrl());
				map.put("heightB", 240);
				result.add(map);
			}
			return MAPPER.writeValueAsString(result);//把list转换成json
		} catch (Exception e) {
			//给管理员发送邮件，查询地址是否，对方的服务器是否继续服务。
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询右上角小广告
	 * @return
	 */
	public String queryAd2() {
		try {
			String json = this.apiService.doGet("http://manage.taotao.com/rest/api/content?categoryId=46&rows=1");
			
			EasyUIResult ui = EasyUIResult.formatToList(json, Content.class);
			
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
			
			List<Content> contents = (List<Content>) ui.getRows();
			
			for (Content content : contents) {
				Map<String, Object> map = new HashMap<>();
				map.put("srcB", content.getPic2());
				map.put("height", 70);
				map.put("alt","");
				map.put("width", 310);
				map.put("src", content.getPic());
				map.put("widthB", 210);
				map.put("href", content.getUrl());
				map.put("heightB", 70);
				result.add(map);
			}
			return MAPPER.writeValueAsString(result);//把list转换成json
		} catch (Exception e) {
			//给管理员发送邮件，查询地址是否，对方的服务器是否继续服务。
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 淘淘快报
	 * @return
	 */
	public String queryAd3() {
		try {
			String json = this.apiService.doGet("http://manage.taotao.com/rest/api/content?categoryId=48&rows=8");
			if (StringUtils.isNotBlank(json)) {
				JsonNode total = MAPPER.readTree(json);	
				ArrayNode rows = (ArrayNode) total.get("rows");
				StringBuilder sb = new StringBuilder();
				sb.append("<ul>");
				for (int i = 0; i < rows.size(); i++) {
					if ((i & 1) != 0) {
						sb.append("<li class=\"odd\" clstag=\"homepage|keycount|home2013|11b1\">");
						sb.append("<a href=\"");
						sb.append(rows.get(i).get("url").asText());
						sb.append("\" target=\"_blank\" title=\"");
						sb.append(rows.get(i).get("title").asText());
						sb.append("\">");
						sb.append(rows.get(i).get("title").asText());
						sb.append("</a></li>");
					}else {
						sb.append("<li clstag=\"homepage|keycount|home2013|11b1\">");
						sb.append("<a href=\"");
						sb.append(rows.get(i).get("url").asText());
						sb.append("\" target=\"_blank\" title=\"");
						sb.append(rows.get(i).get("title").asText());
						sb.append("\">");
						sb.append(rows.get(i).get("title").asText());
						sb.append("</a></li>");
					}
				}
				sb.append("</ul>");
				return sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 查询中间小广告
	 * @return
	 */
	public String queryAd4() {
		try {
			String json = this.apiService.doGet("http://manage.taotao.com/rest/api/content?categoryId=47&rows=6");
			
			EasyUIResult ui = EasyUIResult.formatToList(json, Content.class);
			
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
			
			List<Content> contents = (List<Content>) ui.getRows();
			
			for (int i = 0; i < contents.size(); i++) {
				Map<String, Object> map = new HashMap<>();
				map.put("alt","");
				map.put("href", contents.get(i).getUrl());
				map.put("index", i);
				map.put("src", contents.get(i).getPic());
				map.put("ext", "");
				result.add(map);
			}
			
			return MAPPER.writeValueAsString(result);//把list转换成json
		} catch (Exception e) {
			//给管理员发送邮件，查询地址是否，对方的服务器是否继续服务。
			e.printStackTrace();
		}
		return null;
	}
	
	
}
