package cn.yzw.solr.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.yzw.solr.bean.Products;
import cn.yzw.solr.bean.ProductsQuery;
import cn.yzw.solr.dao.ProductsDao;

@Service
@Transactional
public class ProductsServiceImp implements ProductsService {
	@Autowired
	ProductsDao productsDao;

	@Value("${solrurl}")
	String solrurl;

	@Override
	public void createIndexFromDb() throws SolrServerException, IOException {
		List<Products> list = productsDao.findAll();

		HttpSolrServer server = new HttpSolrServer(solrurl);

		ArrayList<SolrInputDocument> list2 = new ArrayList<SolrInputDocument>();
		for (Products products : list) {
			SolrInputDocument document = new SolrInputDocument();
			document.addField("id", products.getPid());
			document.addField("product_name", products.getName());
			document.addField("product_catalog_name", products.getCatalog_name());
			document.addField("product_price", products.getPrice());
			document.addField("product_description", products.getDescription());
			document.addField("product_picture", products.getPicture());
			list2.add(document);
		}

		UpdateResponse response = server.add(list2);
		server.commit();
	}

	public void deleteAllIndex() throws SolrServerException, IOException {
		HttpSolrServer server = new HttpSolrServer(solrurl);
		server.deleteByQuery("id:*");
		server.commit();

	}

	@Override
	public void delete(Serializable id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(Products products) throws SolrServerException, IOException {
		productsDao.update(products);
		Products products2 = productsDao.findById(products.getPid());

		SolrInputDocument document = new SolrInputDocument();
		document.addField("id", products2.getPid());
		document.addField("product_name", products2.getName());
		document.addField("product_catalog_name", products2.getCatalog_name());
		document.addField("product_price", products2.getPrice());
		document.addField("product_description", products2.getDescription());
		document.addField("product_picture", products2.getPicture());

		HttpSolrServer server = new HttpSolrServer(solrurl);
		UpdateResponse response = server.add(document);
		server.commit();
	}

	@Override
	public List<Products> search(ProductsQuery productsQuery) throws SolrServerException {
		HttpSolrServer server = new HttpSolrServer(solrurl);
		SolrQuery query = new SolrQuery();

		// 进行参数的合法性校验
		if (productsQuery == null) {
			productsQuery = new ProductsQuery();
		}

		if (productsQuery.getKeywords() != null && !"".equals(productsQuery.getKeywords())) {
			// 查询关键字，q不能省略，必须写q
			query.set("q", "product_keywords:" + productsQuery.getKeywords());
		} else {
			// 否则查询全部
			query.set("q", "*:*");
		}

		// 指定过虑
		// query.set("fq", "product_price:[1 TO 100]");
		// 使用过虑
		// 商品分类 搜索
		if (productsQuery.getCatalog_name() != null && !productsQuery.getCatalog_name().equals("")) {
			query.add("fq", "product_catalog_name:" + productsQuery.getCatalog_name());
		}
		// 价格过虑
		// 下边要拼接product_price:[1 TO 3]
		String price_filterString = null;

		if (productsQuery.getPrice_start() != null) {
			price_filterString = "product_price:[" + productsQuery.getPrice_start();
			if (productsQuery.getPrice_end() != null) {
				price_filterString += " TO " + productsQuery.getPrice_end() + "]";
			} else {
				price_filterString += " TO *]";
			}
		}
		if (price_filterString != null) {// 表示进行价格范围查询
			query.add("fq", price_filterString);
		}

		// 排序
		// 参数：field域，排序类型（asc、desc）
		// query.addSort("product_price", ORDER.desc);
		// 加多个排序
		// query.addSort(field, order)
		if (productsQuery.getSortField() != null && !productsQuery.getSortField().equals("")) {
			if (productsQuery.getSortType() != null && productsQuery.getSortType().equals("asc")) {// 升序
				query.addSort(productsQuery.getSortField(), ORDER.asc);
			} else if (productsQuery.getSortType() != null && productsQuery.getSortType().equals("desc")) {
				query.addSort(productsQuery.getSortField(), ORDER.desc);
			}
		}

		// 分页
		// 实际开发时，知道当前页码和每页显示的个数最后求出开始下标
		// int curPage = 1;
		// int rows = 15;
		// 计算出开始记录下标
		// int start = rows * (curPage - 1);
		// 向 query中设置分页参数
		// query.setStart(start);
		// query.setRows(rows);
		// 如果当前页码为空
		if (productsQuery.getCurPage() == null) {
			// 指定当前第一页
			productsQuery.setCurPage(1);
		}
		// 如果每页显示个数为空
		if (productsQuery.getRows() == null) {
			// 指定每页显示16个记录
			productsQuery.setRows(16);
		}
		// 分页
		int start = productsQuery.getRows() * (productsQuery.getCurPage() - 1);

		// 设置分页参数
		query.setStart(start);
		query.setRows(productsQuery.getRows());

		// 设置高亮
		// 开启高亮
		query.setHighlight(true);
		// 设置高亮 参数
		query.addHighlightField("product_name");
		// 可以添加多个域高亮
		// query.addHighlightField(f)
		// 设置高亮前缀和后缀
		query.setHighlightSimplePre("<span style=\"color:red\">");
		query.setHighlightSimplePost("</span>");

		// 执行查询
		QueryResponse response = server.query(query);

		// 从响应中得到结果
		SolrDocumentList docs = response.getResults();

		// 匹配到的总记录数
		long numFound = docs.getNumFound();
		System.out.println("匹配到的总记录数:" + numFound);

		// 得出总记录数，根据总记录数和每页显示的个数计算出总页数
		int pageCount = (int) Math.ceil(numFound * 1.0 / productsQuery.getRows());
		// 用于将pageCount传到页面
		productsQuery.setPageCount(pageCount);

		// 获取高亮
		// 从响应中获取高亮信息
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();

		// 创建List存Bean
		ArrayList<Products> list = new ArrayList<Products>();
		for (SolrDocument doc : docs) {
			// 将doc转换为用于显示的bean
			Products products = new Products();
			// 判断doc内数据的合理性,避免NULLPOINTEXCEPTION
			if (doc.get("id") != null && !doc.get("id").toString().equals("")) {
				System.out.println("商品id：" + doc.get("id"));
				products.setPid(doc.get("id").toString());
			}

			if (doc.get("product_name") != null && !doc.get("product_name").toString().equals("")) {
				System.out.println("商品名称：" + doc.get("product_name"));
				products.setName(doc.get("product_name").toString());
			}

			if (doc.get("product_price") != null && !doc.get("product_price").toString().equals("")) {
				System.out.println("商品价格：" + doc.get("product_price"));
				products.setPrice(Float.parseFloat(doc.get("product_price").toString()));
			}

			if (doc.get("product_catalog_name") != null && !doc.get("product_catalog_name").toString().equals("")) {
				System.out.println("商品分类名称：" + doc.get("product_catalog_name"));
				products.setCatalog_name(doc.get("product_catalog_name").toString());
			}

			if (doc.get("product_picture") != null && !doc.get("product_picture").toString().equals("")) {
				System.out.println("商品图片：" + doc.get("product_picture"));
				products.setPicture(doc.get("product_picture").toString());
			}

			// 设置高亮信息
			if (highlighting != null) {
				// 根据主键获取高亮信息
				Map<String, List<String>> map = highlighting.get(doc.get("id"));
				if (map != null) {
					// 获取高亮信息
					List<String> list1 = map.get("product_name");
					if (list1 != null) {
						products.setName(list1.get(0));
						// System.out.println("高亮后："+list.get(0));
					}

				}
			}

			// 将index获取的信息封装
			list.add(products);
		}

		return list;
	}

}
