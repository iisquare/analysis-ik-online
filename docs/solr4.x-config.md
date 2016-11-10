# analysis-ik-online for solr4.x

> **配置说明**

1. 解压至solr-4.10.4目录，提取./example/webapps/solr.war文件。
2. 解压solr.war至tomcat的./webapps/solr目录。
3. 拷贝solr-4.10.4/example/lib/ext目录下的全部jar包至tomcat/webapps/solr/WEB-INF/lib目录下。
4. 拷贝工程jar包至tomcat/webapps/solr/WEB-INF/lib目录下，版本号以编译依赖为准：
	- ik-solr4.x-0.1.jar
	- bson-2.13.2.jar
	- commons-beanutils-1.8.0.jar
	- commons-collections-3.2.1.jar
	- commons-logging-1.1.1.jar
	- ezmorph-1.0.6.jar
	- json-lib-2.4-jdk15.jar
	- mongo-java-driver-2.13.2.jar
5. 创建/data/solr家目录，拷贝solr.xml到家目录并完善以下配置：
```
33     <int name="hostPort">${jetty.port:8080}</int>
```
6. 修改tomcat/webapps/solr/WEB-INF/web.xml文件，完善以下配置：
```
40   <env-entry>
41       <env-entry-name>solr/home</env-entry-name>
42       <env-entry-value>/data/solr</env-entry-value>
43       <env-entry-type>java.lang.String</env-entry-type>
44   </env-entry>
```
7. 拷贝analysis-ik-online/solr4.xconfig/IKAnalyzer.cfg.xml至tomcat/webapps/solr/WEB-INF/classes/目录下，修改对应MongoDB配置。
8. 修改tomcat/webapps/solr/WEB-INF/web.xml文件，增加IKControllerServlet配置。
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
9. 拷贝solr-4.10.4/example/solr/collection1目录到/data/solr家目录，参照analysis-ik-online/solr4.xconfig/schema.xml修改文档模板。
10. 拷贝solr-4.10.4/resources/log4j.properties至tomcat/webapps/solr/WEB-INF/classes/目录，自定义输出日志。

> **Schema示例**

```
<fieldType name="text_ik" class="solr.TextField">
    <analyzer type="index">
        <tokenizer class="com.iisquare.solr.wltea.lucene.IKTokenizerFactory" dictSerial="suggest" useSmart="true" useArabic="false" useEnglish="false" />
        <filter class="com.iisquare.solr.wltea.lucene.IKSynonymFilterFactory" dictSerial="suggest" />
    </analyzer>
    <analyzer type="query">
        <tokenizer class="com.iisquare.solr.wltea.lucene.IKTokenizerFactory" dictSerial="suggest" useSmart="true" useArabic="false" useEnglish="false" />
    </analyzer>
</fieldType>
```

> **Schema参数**

1. IKTokenizerFactory
	- dictSerial	：词典编码，用于区分不同词典
	- useSmart：是否采用细粒度分词
	- useArabic：是否拆分数值
	- useEnglish	：是否拆分字母
2. IKSynonymFilterFactory
	- dictSerial：词典编码，用于区分不同词典

