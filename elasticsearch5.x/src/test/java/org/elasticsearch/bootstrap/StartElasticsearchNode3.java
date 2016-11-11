package org.elasticsearch.bootstrap;

/**
 * 若程序执行受限，请参照config/java.policy配置${JRE_HOME}/lib/security目录下的同名文件。
 * @author Ouyang <iisquare@163.com>
 *
 */
public class StartElasticsearchNode3 {

	public static void main(String[] args) throws Exception {
		System.setProperty("es.path.home", "server/example/node3");
		Elasticsearch.main(args);
	}
	
}
