package com.pinyougou.server.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.GoodsService;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 *	增加
	 * @param goods  自定义组合类
	 */
	@Override
	public void add(Goods goods) {
		//得到TbGoods，添加到数据库
		TbGoods tbGoods  = goods.getGoods();
		goodsMapper.insert(tbGoods);
		//查询刚添加的tbGoods对象的主键,这个插入功能可以将对象id赋值
		//得到TbGoodsDesc
		TbGoodsDesc goodsDesc = goods.getGoodsDesc();
		//设置goodsDesc主键
		goodsDesc.setGoodsId(tbGoods.getId());
		//添加到数据库
		goodsDescMapper.insert(goodsDesc);

		//3.添加sku商品表的数据
		List<TbItem> items = goods.getItems();
		//4.遍历数据
		for (TbItem item : items) {
			item.setTitle(goods.getGoods().getGoodsName());
			item.setCategoryid(goods.getGoods().getCategory3Id());
			//1.得到图像列表
			String itemImages = goods.getGoodsDesc().getItemImages();
			//2.将图像列表转换为java对象
			List<Map> maps = JSON.parseArray(itemImages, Map.class);
			//3.得到第一张图片并设置给当前的item对象
			item.setImage(maps.get(0).get("url")+"");

			//4.根据sellerId查询seller对象
			TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			//5.将seller商家名称设置给当前的item对象
			if (tbSeller!=null){item.setSeller(tbSeller.getNickName());}
			//5.查询品牌（根据品牌id）
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
			//6.为当前的item设置brand
			item.setBrand(tbBrand.getName());

			item.setUpdateTime(new Date());
			item.setCreateTime(new Date());
			//7.根据三级分类查询分类对象
			TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			//8.为当前的item设置分类名称
			item.setCategory(itemCat.getName());
			//9.设置goodsid
			item.setGoodsId(goods.getGoods().getId());

			//10.添加数据到tb_item表中（sku表）
			itemMapper.insert(item);
		}



	}

	
	/**
	 * 修改
	 */
	@Override
	@Transactional   //事务注解，具有原子性
	public void update(Goods goods){
		//1.更新TbGoods表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		//2.跟新TbGoodsDesc表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//3.更新Sku商品表
		TbItemExample example = new TbItemExample();
		//3.1根据goodsId删除TbItems中的Spu信息
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//3.2得到Items集合，向TbItems表中插入Spu信息
		List<TbItem> items = goods.getItems();
		for (TbItem item : items) {
			itemMapper.insert(item);
		}
	}
	
	/**
	 * 根据ID获取Goods组合类实体
	 * @param id
	 * @return Goods
	 */
	@Override
	public Goods findOne(Long id){
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		TbItemExample example  = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		return new Goods(tbGoods,goodsDesc,tbItems);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			goodsMapper.deleteByPrimaryKey(id);
		}		
	}

	/**
	 * 查询所有商品
	 * @param goods
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
