# analysis-ik-online for solr

> **通用接口**

1. 在线调试开关
	- 接口地址：/solr/ik/dict/debug/
	- 请求方式：GET/POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| debug | Integer | 0 | 1开启，非1关闭 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "ok",
	    "data": true
	}
	```

2. 索引调试分析
	- 接口地址：/solr/ik/dict/demo/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| keyword | String | 无 | 检索关键词 |
	| content | String | 无 | 待索引内容，每行对应一个文档 |
	| parserOperator | String | 无 | and或者or |
	| pageSize | Integer | 5 | 返回记录条数 |
	| dictSerial | String | 空 | 词典编码 |
	| useSmartIndex | Integer | 0 | 索引时，1采用细粒度分词，0不采用 |
	| useArabicIndex | Integer | 0 | 索引时，1拆分数值，0不拆分 |
	| useEnglishIndex | Integer | 0 | 索引时，1拆分字母，0不拆分 |
	| useSynonymIndex | Integer | 0 | 索引时，1采用同义词，0不采用 |
	| useSmartQuery | Integer | 0 | 检索时，1采用细粒度分词，0不采用 |
	| useArabicQuery | Integer | 0 | 检索时，1拆分数值，0不拆分 |
	| useEnglishQuery | Integer | 0 | 检索时，1拆分字母，0不拆分 |
	| useSynonymQuery | Integer | 0 | 检索时，1采用同义词，0不采用 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "美", // 检索关键词
	    "data": {
	        "totalHits": 1,
	        "docs": [
	            "中国美林湖"
	        ]
	    }
	}
	```

3. 获取分词结果
	- 接口地址：/solr/ik/dict/index/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| keyword | String | 无 | 检索关键词 |
	| dictSerial | String | 空 | 词典编码 |
	| useSmart | Integer | 0 | 索引时，1采用细粒度分词，0不采用 |
	| useArabic | Integer | 0 | 索引时，1拆分数值，0不拆分 |
	| useEnglish | Integer | 0 | 索引时，1拆分字母，0不拆分 |
	| useSynonym | Integer | 0 | 索引时，1采用同义词，0不采用 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "美", // 检索关键词
	    "data": [
	        {
	            "startOffset": 0, // 开始偏移
	            "endOffset": 1, // 结束偏移
	            "term": "美", // 分词
	            "type": "CN_WORD" // 分词类型
	        }
	    ]
	}
	```

4. 批量执行命令
	- 接口地址：/solr/ik/dict/runCommand/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| dictSerial | String | 空 | 词典编码 |
	| cmd | String | 无 | 命令JSON格式字符串 |

	- 命令格式：

	```
	{
		dicName : { // 词典名称
			insert : [{ // 添加
				text : "内容，不能为空",
				identity : "自定义标示（分类_ID），null时自定转为空字符串"
			}],
			update : [{ // 修改
				from : { // 至少要设置一个查询条件
					text : "不设置该key时，不作为查询条件",
					identity : "不设置该key时，不作为查询条件"
				},
				to : {
					text : "内容，不能为空",
					identity : "自定义标示（分类_ID），null时自定转为空字符串"
				}
			}],
			delete : [{ // 删除
				text : "不设置该key时，不作为查询条件",
				identity : "不设置该key时，不作为查询条件"
			}]
		}
	}
	```

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "执行成功",
	    "data": {
	        "status": true,
	        "command": {...} // 执行命令的内容
	}
	```

5. 词典重新载入
	- 接口地址：/solr/ik/dict/reload/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| dictSerial | String | 空 | 词典编码 |
	| forceNode | Integer | 无 | 是否为单节点执行，1为单节点，0为集群，不允许为空 |
	| dicts | Array | 无 | 词典名称数组，可选值["stopword", "quantifier", "word", "synonym"] |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "载入成功",
	    "data": {
	        "word": true,
	        "status": true
	    }
	}
	```

6. 词典内容列表
	- 接口地址：/solr/ik/dict/list/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| dictSerial | String | 空 | 词典编码 |
	| type | String | 无 | 词典名称，可选值["stopword", "quantifier", "word", "synonym"]，不能为空 |
	| identity | String | 空 | 词典标识 |
	| text | String | 空 | 词典内容 |
	| page | Integer | 1 | 当前页码 |
	| pageSize | Integer | 500 | 分页大小 |

	- 返回结果：

	```
	{
	    "page": 1,
	    "total": 40211,
	    "pageSize": 1,
	    "totalPage": 40211,
	    "rows": [
	        {
	            "_id": "57eb6277d4d0481d5946f74f",
	            "text": "美林湖",
	            "identity": "op",
	            "time_create": 1475043959,
	            "time_update": 1475043959
	        }
	    ]
	}
	```

7. 词典修改词汇
	- 接口地址：/solr/ik/dict/update/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| id | String | 无 | 主键 |
	| dictSerial | String | 空 | 词典编码 |
	| type | String | 无 | 词典名称，可选值["stopword", "quantifier", "word", "synonym"]，不能为空 |
	| identity | String | 空 | 词典标识 |
	| text | String | 空 | 词典内容 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "更新成功",
	    "data": 1
	}
	```

8. 词典删除词汇
	- 接口地址：/solr/ik/dict/delete/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| dictSerial | String | 空 | 词典编码 |
	| type | String | 无 | 词典名称，可选值["stopword", "quantifier", "word", "synonym"]，不能为空 |
	| ids | Array | 无 | 主键字符串数组 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "删除成功",
	    "data": 0
	}
	```

9. 词典添加词汇
	- 接口地址：/solr/ik/dict/insert/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| dictSerial | String | 空 | 词典编码 |
	| type | String | 无 | 词典名称，可选值["stopword", "quantifier", "word", "synonym"]，不能为空 |
	| identity | String | 空 | 词典标识 |
	| texts | Array | 无 | 字典内容字符串数组 |

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "添加成功",
	    "data": "58341bf047b13e1492ba4191" // 主键
	}
	```
