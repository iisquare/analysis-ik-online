# analysis-ik-online for solr6.x

> **配置说明**

1. 解压至solr-6.2.1目录，拷贝solr-6.2.1/server/solr-webapp/webapp至tomcat的./webapps/solr目录。
2. 拷贝solr-6.2.1/server/lib/ext目录下的全部jar包至tomcat/webapps/solr/WEB-INF/lib目录下。
3. 拷贝工程jar包至tomcat/webapps/solr/WEB-INF/lib目录下，版本号以编译依赖为准：
	- ik-solr6.x-0.1.jar
	- bson-2.13.2.jar
	- commons-beanutils-1.8.0.jar
	- commons-collections-3.2.1.jar
	- commons-logging-1.1.1.jar
	- ezmorph-1.0.6.jar
	- json-lib-2.4-jdk15.jar
	- mongo-java-driver-2.13.2.jar
4. 创建/data/solr家目录，拷贝solr.xml到家目录并完善以下配置：

	```
	33     <int name="hostPort">${jetty.port:8080}</int>
	```

5. 修改tomcat/webapps/solr/WEB-INF/web.xml文件，完善以下配置：

	```
	40   <env-entry>
	41       <env-entry-name>solr/home</env-entry-name>
	42       <env-entry-value>/data/solr</env-entry-value>
	43       <env-entry-type>java.lang.String</env-entry-type>
	44   </env-entry>
	```

6. 拷贝analysis-ik-online/solr4.xconfig/IKAnalyzer.cfg.xml至tomcat/webapps/solr/WEB-INF/classes/目录下，修改对应MongoDB配置。
7. 修改tomcat/webapps/solr/WEB-INF/web.xml文件，增加IKControllerServlet配置。

	```
	<servlet>
		<servlet-name>IKControllerServlet</servlet-name>
		<servlet-class>com.iisquare.solr.wltea.web.FrameServlet</servlet-class>
		<init-param>
			<param-name>appPath</param-name>
			<param-value>/solr/ik/</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>IKControllerServlet</servlet-name>
		<url-pattern>/ik/*</url-pattern>
	</servlet-mapping>
	```

8. 拷贝solr-6.2.1/server/solr/configsets/basic_configs目录到/data/solr家目录，参照analysis-ik-online/solr6.x/config/managed-schema修改文档模板。
9. 拷贝solr-6.2.1/server/resources/log4j.properties至tomcat/webapps/solr/WEB-INF/classes/目录，自定义输出日志。
