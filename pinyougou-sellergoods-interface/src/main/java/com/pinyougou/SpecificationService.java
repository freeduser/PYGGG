package com.pinyougou;
import java.util.List;
import java.util.Map;

import com.pinyougou.group.Specification;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSpecification;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {

	/**
	 * 增加
	*/
	public void add(Specification specification);
	
	
	/**
	 * 修改
	 */
	public void update(Specification specification);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Specification findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);


	/**
	 *
	 * @return
	 */
	List<Map> findSpecList();

	/**
	 * 关键字收索+分页显示
	 * @param specification
	 * @param page
	 * @param rows
	 * @return
	 */
	PageResult search(TbSpecification specification, int page, int rows);

}
