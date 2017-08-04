package cn.yzw.solr.dao;

import java.io.Serializable;
import java.util.List;

import cn.yzw.solr.bean.Products;

public interface ProductsDao {
	public List<Products> findAll();
	
	public Products findById(Serializable id);
	
	public void update(Products products);
	
	public void deleteById(Serializable id);
}
