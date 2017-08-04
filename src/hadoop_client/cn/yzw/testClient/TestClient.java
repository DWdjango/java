package hadoop_client.cn.yzw.testClient;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import hadoop_namenode.cn.yzw.namenode.TestService;

public class TestClient {
	//定義雙方通信的一致版本號(即在此客戶,服務版本號都必須1L)
	public static final long versionID = 1L;
	
	public static void main(String[] args) throws IOException {
		
		InetSocketAddress address = new InetSocketAddress("192.168.2.102", 10000);
		
		TestService proxy = RPC.getProxy(TestService.class, versionID, address, new Configuration());
		
		String data = proxy.getData("C:/aaa.txt");
		
		System.out.println(data);
		
	}
}
