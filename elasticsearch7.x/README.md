# analysis-ik-online for elasticsearch7.x

## **安装步骤**

- cd analysis-ik-online/elasticsearch7.x
- gradle release
- 采用离线方式安装analysis-ik-online插件
	- cd /path/to/elasticsearch
	- bin/elasticsearch-plugin install file:///path/to/analysis-ik-online-${version}.zip
	- bin/elasticsearch-plugin list
- 编辑/path/to/elasticsearch/plugins/analysis-ik-online/IKAnalyzer.cfg.xml文件，修改对应MongoDB配置
- 启动/重启elasticsearch，访问http://127.0.0.1:9200/

## 参数说明
- dictSerial：词典编码，用于区分不同词典
- useSmart：是否采用智能分词，true为粗粒度，false为细粒度
- useArabic：是否拆分数值
- useEnglish：是否拆分字母
- useSynonym：是否采用同义词

## 分词演示

在IKAnalysisPlugin中，默认提供了四种分析器，分别是：

```
map.put("text_ik_i_index", IKAnalyzerProvider::enhanceIndexerForIndex);
map.put("text_ik_i_query", IKAnalyzerProvider::enhanceIndexerForQuery);
map.put("text_ik_q_index", IKAnalyzerProvider::enhanceQuerierForIndex);
map.put("text_ik_q_query", IKAnalyzerProvider::enhanceQuerierForQuery);
```

## 测试示例：

```
POST _analyze
{
  "analyzer": "text_ik_q_query",
  "text":     "请参照API接口文档管理词库"
}
```

## 映射配置

```
{
	"mappings": {
		"default": {
			"properties": {
            	"position" : {
                	"type" : "geo_point"
                },
				"keyword": {
					"type": "text",
					"analyzer": "text_ik_q_index",
                    "search_analyzer": "text_ik_q_query",
                    "search_quote_analyzer": "text_ik_q_index"
				}
			}
		}
	}
}
```

## 常见问题
- Mapping支持的字段类型

    参照org.elasticsearch.index.mapper.TypeParsers.java文件。

- access denied ("java.lang.RuntimePermission" "createClassLoader")

    修改java.policy策略文件。

- must reference a class loader local Plugin class
    
    在(PluginsService.java:640)异常处打断点，强制跳过验证。
    ```
    loader = pluginClass.getClassLoader()
    ```