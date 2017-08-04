package cn.yzw.solr.test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.yzw.solr.service.ProductsService;

public class Test01 {
	
	static{
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		ps = (ProductsService) context.getBean("productsServiceImp");
		
	}
	
	static ProductsService ps;
	
	
	@Test
	public void test01(){
		//System.out.println(ps);
		try {
			ps.createIndexFromDb();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
