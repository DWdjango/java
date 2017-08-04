package hadoop_namenode.cn.yzw.namenode;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RPC.Builder;
import org.apache.hadoop.ipc.RPC.Server;

import hadoop_namenode.cn.yzw.namenode.impl.TestServiceImpl;
/**
 * 發佈服務的發佈器
 * 打成run的jar發佈至服務器 傳至linux
 * java -jar 包名 來運行這個jar
 * @author pewee
 *
 */
public class TestServicePublisher {

	public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
		// 使用Rpc得到獲取RPC服務的bulider
		Builder builder = new RPC.Builder(new Configuration());
		//bulider和我的server建立聯繫,首先設置接口,即通信雙方的協議
		builder.setProtocol(TestService.class);
		//設置接口的具體實例對象
		builder.setInstance(new TestServiceImpl());
		//設置服務的綁定的地址(設到服務器的ip),我的服務要運行的主機,	PC或linux
		builder.setBindAddress("192.168.2.102");
		builder.setPort(10000);
		//builder建立服務(本質上是socket)
		Server server = builder.build();
		//啟動服務,可以被調用
		server.start();
	}

}
