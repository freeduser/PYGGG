package com.pinyougou.server.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.SpecificationService;
import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    /**
     * 增加
     */
    @Override
    public void add(Specification specification) {
        //1.首先，在主表tb_specification表中添加数据(在dao层的mapper映射文件中insert方法中添加如下)
        /**
         *此方法的作用就是在添加完一个规格后，为其对应的实体类的id（主键）赋值
         *     <selectKey resultType="java.lang.Long" order="AFTER" keyProperty="id">
         *       SELECT LAST_INSERT_ID() AS id
         *     </selectKey>
         *     执行完成后，就可以通过specification.getSpec().getId()得到刚才添加的规格id了
         */
        specificationMapper.insert(specification.getSpec());
        //2.向子表tb_specitionOption中添加相关数据
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            //2.1)设置规格选项的外键
            option.setSpecId(specification.getSpec().getId());
            //2.2)在tb_specificationOption表中添加数据
            specificationOptionMapper.insert(option);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Specification specification) {
        //1.修改tb_specification表
        specificationMapper.updateByPrimaryKey(specification.getSpec());

        //2.根据specification.getSpec().getId()这个外键从tb_specificationOption表中删除指定记录
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpec().getId());
        specificationOptionMapper.deleteByExample(example);

        //3.向tb_specificationOption添加新的规格选项即可
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            option.setSpecId(specification.getSpec().getId());
            specificationOptionMapper.insert(option);
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specification findOne(Long id) {
        //1.查询出TbSpecification
        TbSpecification spec = specificationMapper.selectByPrimaryKey(id);
        //2.根据外键spec.id查询出tb_sepecificationOption表的数据
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        //3.添加外键作为查询条件
        criteria.andSpecIdEqualTo(id);
        //4.根据外键查询出集合
        List<TbSpecificationOption> specificationOptionList = specificationOptionMapper.selectByExample(example);
        //5.构造Specification 组合对象
        Specification specification = new Specification();
        specification.setSpec(spec);
        specification.setSpecificationOptionList(specificationOptionList);
        return specification;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //1.删除主表中的对应的id的记录
            specificationMapper.deleteByPrimaryKey(id);

            //2.根据外键从tb_specificationOption表中删除
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionMapper.deleteByExample(example);

        }
    }


    @Override
    public List<Map> findSpecList() {
        return specificationMapper.findSpecList();
    }

    @Override
    public PageResult search(TbSpecification specification, int page, int rows) {
        PageHelper.startPage(page, rows);
        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (specification != null) {
            if (specification.getSpecName() != null) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }
        }
        Page<TbSpecification> specifications = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(specifications.getTotal(), specifications.getResult());
    }



}




