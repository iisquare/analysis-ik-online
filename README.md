# analysis-ik-online

> **项目简介**

	基于IKAnalyzer深度定制拓展的一款分析器插件，支持特殊字符、数值、字母的分词策略，支持词典在线管理和热重载，支持多分析器多词库配置。
	同时，为便于开发调试，项目中直接集成了Solr和Elasticsearch的单机、集群调试功能，可直接在IDE中运行。

通过调整以下参数可实现不同分词效果：

	1. dictSerial：词典编码，用于区分不同词典
	2. useSmart：是否采用细粒度分词
	3. useArabic：是否拆分数值
	4. useEnglish：是否拆分字母
	5. useSynonym：是否采用同义词

> **相关资源**

- Solr使用说明文档：[solr](./docs/solr.md)
- Elasticsearch使用说明文档：[elasticsearch](./docs/elasticsearch.md)

