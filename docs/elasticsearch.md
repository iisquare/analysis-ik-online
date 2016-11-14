# analysis-ik-online for elasticsearch

> **项目导入**

1. cd analysis-ik-online/elasticsearch5.x
2. gradle eclipse
3. 打开Eclipse，执行导入(import)
4. 在项目上单击右键，选择configure->Convert to Gradle(STS) Project
5. 在项目上单击右键，选择Gradle(STS)->Refresh All

> **项目调试**

1. 将mongdb/mongo-lucene.js导入到数据库
2. 修改analysis-ik-online/elasticsearch5.x/src/test/resources/IKAnalyzer.cfg.xml配置
3. 执行gradle deployTest
4. 执行org.elasticsearch.bootstrap.StartElasticsearchSingle.java
5. 访问http://127.0.0.1:9200/

	```
	若执行时提示jar hell包冲突，可将build.gradle文件中configurations模块暂时注释掉，我们将尽快解决这个问题。
	```

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

> **默认分析器**

在IKAnalysisPlugin中，默认提供了四个分析器，分别是：

```
map.put("text_ik_i_index", IKAnalyzerProvider::enhanceIndexerForIndex);
map.put("text_ik_i_query", IKAnalyzerProvider::enhanceIndexerForQuery);
map.put("text_ik_q_index", IKAnalyzerProvider::enhanceQuerierForIndex);
map.put("text_ik_q_query", IKAnalyzerProvider::enhanceQuerierForQuery);
```

> **分析器参数**

1. IKAnalyzerProvider
	- dictSerial	：词典编码，用于区分不同词典
	- useSmart：是否采用细粒度分词
	- useArabic：是否拆分数值
	- useEnglish	：是否拆分字母
	- useSynonym：是否采用同义词
2. IKTokenizerFactory
	- dictSerial	：词典编码，用于区分不同词典
	- useSmart：是否采用细粒度分词
	- useArabic：是否拆分数值
	- useEnglish	：是否拆分字母
3. IKSynonymFilterFactory
	- dictSerial：词典编码，用于区分不同词典
