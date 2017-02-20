package com.taotao.common.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 帮助我们发送http的请求
 * 
 * @author Administrator
 *
 */
@Service
public class ApiService implements BeanFactoryAware{

//	@Autowired
//	private CloseableHttpClient httpclient;
	
	@Autowired(required = false)
	private RequestConfig requestConfig;

	/**
	 * POST ,GET
	 */
	/**
	 * 无参的get请求
	 * 
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public String doGet(String url) throws ClientProtocolException, IOException {

		// 创建http GET请求
		HttpGet httpGet = new HttpGet(url);

		httpGet.setConfig(requestConfig);
		
		CloseableHttpResponse response = null;
		try {
			// 执行请求
			response = getHttpClient().execute(httpGet);
			// 判断返回状态是否为200
			if (response.getStatusLine().getStatusCode() == 200) {
				String content = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				return content;
			}
			return null;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 带有参数的get请求
	 * 
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String doGet(String url, Map<String, String> params) throws URISyntaxException, ClientProtocolException, IOException {

		URIBuilder builder = new URIBuilder(url);
		if(params != null){
			for (String key : params.keySet()) {
				builder.setParameter(key, params.get(key));// 第一个参数 ；参数名称，第二个参数 参数的值
			}
		}

		URI uri = builder.build();
		return this.doGet(uri.toString());
	}
	
	/**
	 * 带有参数的post请求
	 * @param url
	 * @param params
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public HttpResult doPost(String url , Map<String, String> params) throws ParseException, IOException{

        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        if(params != null){
        	
        	
        	// 设置2个post参数，一个是scope、一个是q
        	List<NameValuePair> parameters = new ArrayList<NameValuePair>(0);
        	for(String key : params.keySet()){
        		parameters.add(new BasicNameValuePair(key, params.get(key)));
        	}
        	// 构造一个form表单式的实体
        	UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters);
        	// 将请求实体设置到httpPost对象中
        	httpPost.setEntity(formEntity);
        }
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
	}
	
	
	/**
	 * 创建一个可以发送json数据的请求
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public HttpResult doPostJson(String url , String json) throws ClientProtocolException, IOException{
		// 创建http POST请求
        HttpPost httpPost = new HttpPost(url);
        if(StringUtils.isNotBlank(json)){
        	StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        	httpPost.setEntity(stringEntity);
        }
        httpPost.setConfig(requestConfig);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = getHttpClient().execute(httpPost);
            return new HttpResult(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	
	public HttpResult doPost(String url) throws ParseException, IOException{
		return this.doPost(url, null);
	}

	private BeanFactory beanFactory;
	
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	private CloseableHttpClient getHttpClient(){
		return this.beanFactory.getBean(CloseableHttpClient.class);
	}
	
}
