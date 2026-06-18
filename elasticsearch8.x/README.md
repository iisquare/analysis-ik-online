# analysis-ik-online for elasticsearch7.x

## **安装步骤**

- cd analysis-ik-online/elasticsearch7.x
- gradle release
- 采用离线方式安装analysis-ik-online插件
	- cd /path/to/elasticsearch
	- bin/elasticsearch-plugin install file:///path/to/analysis-ik-online-${version}.zip
	- bin/elasticsearch-plugin list
- 编辑/path/to/elasticsearch/plugins/analysis-ik-online/IKAnalyzer.cfg.xml文件，修改对应词库配置
- 启动/重启elasticsearch，访问http://127.0.0.1:9200/

## 变更记录
- 移除MongoDB读写，改为从远程接口或本地文件加载词库。

## 参数说明
- dictSerial：词典编码，用于区分不同词典
- useSmart：是否采用智能分词，true为粗粒度，false为细粒度
- useArabic：是否拆分数值
- useEnglish：是否拆分字母
- useSynonym：是否采用同义词

## 分词演示

在IKAnalysisPlugin中，默认提供了五种分析器，分别是：

```
map.put("ik_suggest_index", IKAnalyzerProvider::ikSuggestIndex);
map.put("ik_suggest_query", IKAnalyzerProvider::ikSuggestQuery);
map.put("ik_max_word", IKAnalyzerProvider::ikMaxWord);
map.put("ik_smart", IKAnalyzerProvider::ikSmart);
map.put("ik_no_word", IKAnalyzerProvider::ikNoWord);
```

## 测试示例：

```
POST _analyze
{
  "analyzer": "ik_smart",
  "text":     "请参照API接口文档管理词库"
}
```

## 映射配置

```
{
  "mappings": {
    "default": {
      "properties": {
        "position": {
          "type": "geo_point"
        }, 
        "keyword": {
          "type": "text", 
          "analyzer": "ik_max_word", 
          "search_analyzer": "ik_smart", 
          "search_quote_analyzer": "ik_max_word"
        }
      }
    }
  }
}
```

## 最佳实践

- 推荐采用ik_no_word作为默认的索引和检索，全部拆散为单个字。
- 完整匹配直接使用match_phrase短语查询。
- 常规检索通过调用ik_smart获取分词结果后，转换为短语查询。
- 维护专用于ik_smart分词检索接口的词库，避免用于索引（词库变更后需重建）。
- fixed: ik_max_word会在中间位置插入细粒度分词，导致ik_smart粗粒度分词短语查询失败。
- fixed: 使用match operator(and/or)查询standard索引，即不能处理特殊字符，也存在太多分词无关的内容。

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

## 参考文档

- [IK Analysis for Elasticsearch and OpenSearch](https://github.com/infinilabs/analysis-ik)
