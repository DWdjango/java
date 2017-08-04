package cn.yzw.solr.handler;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.yzw.solr.bean.Products;
import cn.yzw.solr.bean.ProductsQuery;
import cn.yzw.solr.service.ProductsService;

@Controller
public class ProductsHandler {
	@Autowired
	ProductsService productsService;
	
	
	@RequestMapping("/search.action")
	public String search(Model model,ProductsQuery productsQuery){
		try {
			List<Products> list = productsService.search(productsQuery);
			model.addAttribute("list", list);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "search";
	}
}
