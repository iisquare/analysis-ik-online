# analysis-ik-online for solr4.x

> **安装步骤**

1. cd analysis-ik-online/solr4.x
2. gradle release
3. 将mongdb/mongo-lucene.js导入到数据库
4. 拷贝analysis-ik-online/solr4.x/target目录下的全部jar文件到tomcat/webapps/solr/WEB-INF/lib目录下
5. 拷贝analysis-ik-online/solr4.xconfig/IKAnalyzer.cfg.xml至tomcat/webapps/solr/WEB-INF/classes/目录下，修改对应MongoDB配置
6. 修改tomcat/webapps/solr/WEB-INF/web.xml文件，增加IKControllerServlet配置

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

7. 参照analysis-ik-online/solr4.x/config/schema.xml修改文档模板
8. 启动/重启tomcat，访问http://127.0.0.1:8080/solr
