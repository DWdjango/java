package cn.yzw.solr.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;

import cn.yzw.solr.bean.Products;
import cn.yzw.solr.bean.ProductsQuery;

public interface ProductsService  {
	
	public void createIndexFromDb() throws SolrServerException, IOException ;
	
	public void delete(Serializable id);
	
	public void deleteAllIndex() throws SolrServerException, IOException;
	
	public void update(Products products) throws SolrServerException, IOException ;
	
	public List<Products> search(ProductsQuery productsQuery) throws SolrServerException ;
}
