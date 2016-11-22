# analysis-ik-online for elasticsearch

> **通用接口**

不同语言针对URL查询字符串的处理差异如下：

| QueryString | PHP | Java | Elastcisearch |
| :----- | :----- | :----- | :----- |
| a=1 | 字符串 | 数组 | 字符串 |
| a=1&a=2 | 最后一个字符串 | 数组 | 最后一个字符串 |
| a[]=1&a[]=2 | 数组Key=a | 数组Key=a[] | 最后一个字符串Key=a[] |
| a[0]=1&a[1]=2 | 数组 | 多个数组 | 多个字符串 |
| a.k1=1&a.k2=2 | 多个字符串 | 多个数组 | 多个字符串 |

该插件的Solr API遵循PHP的QueryString处理方式，Elastcisearch API采用JSON格式数据作为请求参数，请参照[solr-api](./solr-api.md)文档，把/solr/ik/替换为插件的实际地址（默认为/_plugin/analysis-ik-online/），并将对应参数全部转换为JSON对象即可。

在不使用POST方式传递JSON请求数据的情况下，部分接口的GET请求参数依然有效，如线调试开关/dict/debug/接口。另外，批量执行命令/dict/runCommand/接口的cmd参数可直接采用对象传递，也同时支持原对象字符串的方式。

> **调用示例**

1. 在线调试开关
	- 接口地址：/_plugin/analysis-ik-online/dict/debug/
	- 请求方式：POST
	- 接口参数：

	| 名称 | 类型 | 默认 | 说明 |
	| :----- | :----- | :----- | :----- |
	| debug | Integer | 0 | 1开启，非1关闭 |

	- 请求示例：

	```
	POST /_plugin/analysis-ik-online/dict/debug/
	{
	    "debug": 1
	}
	```

	- 返回结果：

	```
	{
	    "code": 0,
	    "message": "ok",
	    "data": true
	}
	```
