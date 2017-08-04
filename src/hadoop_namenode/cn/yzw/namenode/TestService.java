package hadoop_namenode.cn.yzw.namenode;
/**
 * 服務類接口,用於遠程過程調用
 * @author pewee
 *
 */
public interface TestService {
	//定義雙方通信的一致版本號(即在此客戶,服務版本號都必須1L)
	public static final long versionID = 1L;
	
	//雙方可調用的方法
	//如這裡可以實現調用hdfs,查看filepath的文件信息
	public String getData(String filepath);
	
}
