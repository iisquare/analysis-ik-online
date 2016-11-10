# analysis-ik-online for solr

> **项目导入**

1. cd analysis-ik-online/solr4.x
2. gradle eclipse
3. 打开Eclipse，执行导入(import)
4. 在项目上单击右键，选择configure->Convert to Gradle(STS) Project
5. 在项目上单击右键，选择Gradle(STS)->Refresh All

> **项目调试**

1. 将mongdb/mongo-lucene.js导入到数据库
2. 修改analysis-ik-online/solr4.x/src/test/resources/IKAnalyzer.cfg.xml配置
3. 执行com.iisquare.solr.jetty.StartSolrJetty.java
4. 访问http://127.0.0.1:8983/solr

> **项目发布**

1. gradle release

> **项目清理**

1. gradle cleanEclipse
2. gradle clean

> **词库说明**

1. 集合名称格式：

	```
		dbName.collectionName.dictSerial
	```

2. 集合字段说明：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| text | String | 无 | 字典内容 |
	| identity | String | 无 | 分类标识，只为方便管理 |
	| time_create | Integer | 无 | 创建时间 |
	| time_update | Integer| 无 | 修改时间 |

3. 字典格式说明：
	- 基础词条为单个字或连续的词，单个字主要用于细粒度分词
	- 同义词格式为“词一,词二,词三=>词一,词二,词三”，其中每个词项必须是分词词库中已存在的词条

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

> **相关链接**

- 通用接口：[solr-api](./solr-api.md)
- 配置说明：[solr-config-4.x](./solr-config-4.x.md)
- 配置说明：[solr-config-6.x](./solr-config-6.x.md)
