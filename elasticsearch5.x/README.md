# analysis-ik-online for elasticsearch5.x

> **安装步骤**

1. cd analysis-ik-online/elasticsearch5.x
2. gradle release
3. 将mongdb/mongo-lucene.js导入到数据库
4. 采用离线方式安装analysis-ik-online插件
	- cd /path/to/elasticsearch
	- bin/elasticsearch-plugin install file:///path/to/analysis-ik-online-${version}.zip
	- bin/elasticsearch-plugin list
5. 编辑/path/to/elasticsearch/plugins/analysis-ik-online/IKAnalyzer.cfg.xml文件，修改对应MongoDB配置
6.  启动/重启elasticsearch，访问http://127.0.0.1:9200/

> **分词演示**

在IKAnalysisPlugin中，默认提供了四种分析器，分别是：

```
map.put("text_ik_i_index", IKAnalyzerProvider::enhanceIndexerForIndex);
map.put("text_ik_i_query", IKAnalyzerProvider::enhanceIndexerForQuery);
map.put("text_ik_q_index", IKAnalyzerProvider::enhanceQuerierForIndex);
map.put("text_ik_q_query", IKAnalyzerProvider::enhanceQuerierForQuery);
```

测试示例：

```
POST _analyze
{
  "analyzer": "text_ik_q_query",
  "text":     "请参照API接口文档管理词库"
}
```
