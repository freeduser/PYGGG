package com.pinyougou.content.services.impl;

import java.util.List;

import com.pinyougou.content.interfaces.ContentService;
import com.pinyougou.pojo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     * 为了保证数据库和redis缓存库数据同步
     */
    @Override
    public void add(TbContent content) {
        contentMapper.insert(content);
        //删除redis服务器中根据contentCategoryId为键保存的集合数据
        redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
    }


    /**
     * 修改
     * 需要保证数据库和redis缓存库数据同步
     */
    @Override
    public void update(TbContent content) {
        //1.删除修改后的以分类id为键缓存的数据
        redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
        //2.判断修改后分类Id与修改前的分类Id是否一样
        //2.1根据contentId获得数据库中的content对象
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        if (!tbContent.getCategoryId().equals(content.getCategoryId())){
            //如果不相等就删除修改前的以分类Id为键保存的数据
            //相等的话，没有必要，前一步已经删除了
            redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
        }
        contentMapper.updateByPrimaryKey(content);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     * 为了保证数据库和redis缓存库数据同步
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //1.根据id查询ceotent
            TbContent tbContent = contentMapper.selectByPrimaryKey(id);
            //2.删除redis服务器中根据contentCategoryId为键保存的集合数据
            redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());
            //3.执行删除
            contentMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据分类id查询广告信息
     *
     * @param id
     * @return
     */
    @Override
    public List<TbContent> findByCategoryId(String id) {
        //从缓存数据库中拿到图片
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(id);
        if (contentList == null) {
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(Long.parseLong(id));
            System.out.println("MySQL中取得");
            List<TbContent> contents = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("contentList").put(id,contents);
            return contents;
        }
        System.out.println("缓存数据库中取得");
        return contentList;
    }

}
