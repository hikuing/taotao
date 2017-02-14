package com.taotao.manage.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public class BaseService <T extends BasePojo>{
	
	/**
	 * spring4之后支持，如果是4之前的版本，使用这个方式：
	 * public abstract Mapper<T> mapper;
	 */
	@Autowired
	private Mapper<T> mapper;
	
	/**
	 * 定义一个抽象方法，去获取 ，某一个具体的service使用的mapper
	 */
	//public abstract Mapper<T> mapper;
	

	
	/**
	 * 根据主键获取一个对象
	 * @param id 主键的值
	 * @return
	 */
	public T queryById(Object id) {
		return mapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 查询所有对象
	 * @return
	 */
	public List<T> queryAll() {
		return mapper.select(null);
	}
	
	/**
	 * 根据条件查询一个对象
	 * @param record 
	 * @return
	 */
	public T queryOne(T record ) {
		return mapper.selectOne(record);
	}
	
	/**
	 * 根据条件查询list集合
	 * @param record
	 * @return
	 */
	public List<T> queryListByWhere(T record) {
		return mapper.select(record);
	}
	

	/**
	 * 根据条件查询分页数据
	 * @param record 查询条件
	 * @param pageNum 当前页码
	 * @param pageSize 每页大小
	 * @return
	 */
	public PageInfo<T> queryPageByWhere(T record,Integer pageNum,Integer pageSize) {
		//开启分页
		PageHelper.startPage(pageNum, pageSize);
		List<T> list = mapper.select(record);
		return new PageInfo<>(list);
	}
	
	/**
	 * 插入数据
	 * @param record
	 * @return
	 */
	public Integer save(T record) {
		/**
		 * 设置一些默认值：如创建时间，修改时间
		 */
		record .setCreated(new Date());
		record.setUpdated(record.getCreated());
		return mapper.insertSelective(record);
	}
	
	/**
	 * 根据条件修改
	 * @param record
	 * @return
	 */
	public Integer updateByPrimaryKey(T record) {
		//修改的时候设置默认值
		record.setUpdated(new Date());
		//设置不允许修改的字段，创建时间不允许修改
		record.setCreated(null);//强制不修改
		return mapper.updateByPrimaryKeySelective(record);
	}
	
	/**
	 * 根据id删除对象
	 * @param id
	 * @return
	 */
	public Integer deleteById(Object id) {
		return mapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 根据id批量删除
	 * @param clazz 泛型class对象
	 * @param property 对象的主键的属性名称
	 * @param ids 要删除的主键的值的集合
	 * @return
	 */
	public Integer deleteByIds(Class<T> clazz,String property, List<Object> ids) {
		//delete...from ... where id in ()
		Example example = new Example(clazz);
		example.createCriteria().andIn(property, ids);
		return mapper.deleteByExample(example);
	}
	
	/**
	 * 根据条件删除记录
	 * @param record
	 * @return
	 */
	public Integer deleteByWhere(T record) {
		return mapper.delete(record);
	}
	
	
}
