package com.taotao.search.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.search.bean.Item;
import com.taotao.search.bean.SearchResult;

@Service
public class SearchService {

	@Autowired
	private HttpSolrServer httpSolrServer;
	

	public static final Integer ROWS = 32;
	public SearchResult query(String keywords , Integer page) {
		try {
			SolrQuery solrQuery = new SolrQuery(); // 构造搜索条件
			solrQuery.setQuery("title:" + keywords + " AND status:1"); // 搜索关键词
			// 设置分页 start=0就是从0开始，，rows=5当前返回5条记录，第二页就是变化start这个值为5就可以了。
			solrQuery.setStart((Math.max(page, 1) - 1) * ROWS);
			solrQuery.setRows(ROWS);

			// 是否需要高亮 keywords 不是* ，也不是空，需要高亮查询
			boolean isHighlighting = !StringUtils.equals("*", keywords)
					&& StringUtils.isNotEmpty(keywords);

			if (isHighlighting) {
				// 设置高亮
				solrQuery.setHighlight(true); // 开启高亮组件
				solrQuery.addHighlightField("title");// 高亮字段
				solrQuery.setHighlightSimplePre("<em>");// 标记，高亮关键字前缀
				solrQuery.setHighlightSimplePost("</em>");// 后缀
			}

			// 执行查询
			QueryResponse queryResponse = this.httpSolrServer.query(solrQuery);

			List<Item> foos = queryResponse.getBeans(Item.class);
			if (isHighlighting) {
				// 将高亮的标题数据写回到数据对象中
				Map<String, Map<String, List<String>>> map = queryResponse
						.getHighlighting();
				for (Map.Entry<String, Map<String, List<String>>> highlighting : map
						.entrySet()) {
					for (Item foo : foos) {
						if (!highlighting.getKey().equals(
								foo.getId().toString())) {
							continue;
						}
						foo.setTitle(StringUtils.join(highlighting.getValue()
								.get("title"), ""));
						break;
					}
				}
			}
			//获取总记录数
			Long total = queryResponse.getResults().getNumFound();
			return new SearchResult(total, foos);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Autowired
	private ItemService itemService;
	
	public void addItemToSolr(Long itemId) throws IOException, SolrServerException {
//		增加   根据itemid 把商品信息获取到，保存到索引库
		Item item = this.itemService.queryItemById(itemId);
		this.httpSolrServer.addBean(item);//item === id 如果有的话，就更新，没有加入
		this.httpSolrServer.commit();
	}
	
	public void deleteItemFromSolr(Long itemId) throws SolrServerException, IOException {
		this.httpSolrServer.deleteById(""+itemId);
		this.httpSolrServer.commit();
	}
	
}








