package hadoop_namenode.cn.yzw.namenode.impl;

import hadoop_namenode.cn.yzw.namenode.TestService;

public class TestServiceImpl implements TestService{

	@Override
	public String getData(String filepath) {
		// TODO Auto-generated method stub
		return "you get file : " + filepath;
	}
	
}
