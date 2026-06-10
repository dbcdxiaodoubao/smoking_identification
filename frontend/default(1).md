# 香烟识别系统-RESTful API 文档


**简介**:香烟识别系统-RESTful API 文档


**HOST**:localhost:8080


**联系人**:zzh


**Version**:v0.0.1


**接口路径**:/v2/api-docs


[TOC]






# AI接口


## 分析数据（传入1为分析近24小时传入2为分析近7天）


**接口地址**:`/ai/{flag}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|flag|flag|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


# 可视化接口


## 查询等级分布


**接口地址**:`/echart/level`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体«List«趋势实体»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|array|趋势实体|
|&emsp;&emsp;incidentCount|条数|integer(int64)||
|&emsp;&emsp;level|预警等级(1-3代表低、中、高危险)|integer(int64)||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": [
		{
			"incidentCount": 0,
			"level": 0
		}
	],
	"msg": ""
}
```


## 查询地区分布


**接口地址**:`/echart/location`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体«List«趋势实体»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|array|趋势实体|
|&emsp;&emsp;incidentCount|条数|integer(int64)||
|&emsp;&emsp;level|预警等级(1-3代表低、中、高危险)|integer(int64)||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": [
		{
			"incidentCount": 0,
			"level": 0
		}
	],
	"msg": ""
}
```


## 吸烟行为检测次数


**接口地址**:`/echart/overview`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>查询系统吸烟行为检测总数量</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 查询时间分布


**接口地址**:`/echart/time`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体«List«时间段分布实体»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|array|时间段分布实体|
|&emsp;&emsp;hourSlot|小时时间段|string||
|&emsp;&emsp;incidentCount|条数|integer(int64)||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": [
		{
			"hourSlot": "",
			"incidentCount": 0
		}
	],
	"msg": ""
}
```


## 查询趋势


**接口地址**:`/echart/trend`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体«List«趋势实体»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|array|趋势实体|
|&emsp;&emsp;incidentCount|条数|integer(int64)||
|&emsp;&emsp;level|预警等级(1-3代表低、中、高危险)|integer(int64)||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": [
		{
			"incidentCount": 0,
			"level": 0
		}
	],
	"msg": ""
}
```


# 处理事件管理


## 查询处理事件列表


**接口地址**:`/dispose`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|pageNum|第几页|query|true|integer(int32)||
|pageSize|分页大小|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|表格分页数据对象«List«处理事件信息列表»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|消息状态码|integer(int32)|integer(int32)|
|msg|消息内容|string||
|rows|列表数据|array|处理事件信息列表|
|&emsp;&emsp;createTime|处理时间|Timestamp|Timestamp|
|&emsp;&emsp;&emsp;&emsp;date||integer||
|&emsp;&emsp;&emsp;&emsp;day||integer||
|&emsp;&emsp;&emsp;&emsp;hours||integer||
|&emsp;&emsp;&emsp;&emsp;minutes||integer||
|&emsp;&emsp;&emsp;&emsp;month||integer||
|&emsp;&emsp;&emsp;&emsp;nanos||integer||
|&emsp;&emsp;&emsp;&emsp;seconds||integer||
|&emsp;&emsp;&emsp;&emsp;time||integer||
|&emsp;&emsp;&emsp;&emsp;timezoneOffset||integer||
|&emsp;&emsp;&emsp;&emsp;year||integer||
|&emsp;&emsp;disposeId|处理事件id|integer(int64)||
|&emsp;&emsp;incidentId|预警事件id|integer(int64)||
|&emsp;&emsp;userId|处理人id|integer(int64)||
|total|总记录数|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"rows": [
		{
			"createTime": {
				"date": 0,
				"day": 0,
				"hours": 0,
				"minutes": 0,
				"month": 0,
				"nanos": 0,
				"seconds": 0,
				"time": 0,
				"timezoneOffset": 0,
				"year": 0
			},
			"disposeId": 0,
			"incidentId": 0,
			"userId": 0
		}
	],
	"total": 0
}
```


# 学生管理


## 查询学生信息列表


**接口地址**:`/student`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|pageNum|第几页|query|true|integer(int32)||
|pageSize|分页大小|query|true|integer(int32)||
|banji|banji|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|表格分页数据对象«List«学生列表»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|消息状态码|integer(int32)|integer(int32)|
|msg|消息内容|string||
|rows|列表数据|array|学生列表|
|&emsp;&emsp;banji|班级|string||
|&emsp;&emsp;name|姓名|string||
|&emsp;&emsp;pictureUrl|人脸图片路径|string||
|&emsp;&emsp;sex|性别（1代表男生，2代表女生）|integer(int64)||
|&emsp;&emsp;studentId|学生id|integer(int64)||
|total|总记录数|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"rows": [
		{
			"banji": "",
			"name": "",
			"pictureUrl": "",
			"sex": 0,
			"studentId": 0
		}
	],
	"total": 0
}
```


## 新增学生


**接口地址**:`/student`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|banji|班级|query|true|string||
|name|姓名|query|true|string||
|sex|性别|query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 上传人脸


**接口地址**:`/student/face`


**请求方式**:`POST`


**请求数据类型**:`multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|image|image|body|true|string||
|studentId|studentId|query|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 删除学生


**接口地址**:`/student/{studentId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|studentId|studentId|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|204|No Content||
|401|Unauthorized||
|403|Forbidden||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


# 摄像头管理


## 查询摄像头列表


**接口地址**:`/camera`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|pageNum|第几页|query|true|integer(int32)||
|pageSize|分页大小|query|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|表格分页数据对象«List«摄像头信息列表»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|消息状态码|integer(int32)|integer(int32)|
|msg|消息内容|string||
|rows|列表数据|array|摄像头信息列表|
|&emsp;&emsp;cameraId||integer(int64)||
|&emsp;&emsp;cameraName|摄像头名称|string||
|&emsp;&emsp;level|预警等级(1-3代表低、中、高危险)|integer(int64)||
|&emsp;&emsp;location|所在位置|string||
|total|总记录数|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"rows": [
		{
			"cameraId": 0,
			"cameraName": "",
			"level": 0,
			"location": ""
		}
	],
	"total": 0
}
```


## 新增摄像头


**接口地址**:`/camera`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "cameraName": "",
  "dtl": "",
  "level": 0,
  "location": "",
  "rtsp": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|cameraCreateQuety|cameraCreateQuety|body|true|摄像头新增实体|摄像头新增实体|
|&emsp;&emsp;cameraName|||true|string||
|&emsp;&emsp;dtl|||false|string||
|&emsp;&emsp;level|||true|integer(int64)||
|&emsp;&emsp;location|||true|string||
|&emsp;&emsp;rtsp|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 更新摄像头


**接口地址**:`/camera`


**请求方式**:`PUT`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "cameraName": "",
  "dtl": "",
  "level": 0,
  "location": "",
  "rtsp": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|cameraUpdateQuety|cameraUpdateQuety|body|true|摄像头新增实体|摄像头新增实体|
|&emsp;&emsp;cameraName|||true|string||
|&emsp;&emsp;dtl|||false|string||
|&emsp;&emsp;level|||true|integer(int64)||
|&emsp;&emsp;location|||true|string||
|&emsp;&emsp;rtsp|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 查询摄像头详情


**接口地址**:`/camera/{cameraId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|cameraId|cameraId|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 删除摄像头


**接口地址**:`/camera/{cameraId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|cameraId|cameraId|path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|204|No Content||
|401|Unauthorized||
|403|Forbidden||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


# 用户管理接口


## 获取用户信息


**接口地址**:`/user/info`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|Authorization|header|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 登录


**接口地址**:`/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "password": "",
  "userName": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|loginQuery|loginQuery|body|true|登录实体|登录实体|
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;userName|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 注册


**接口地址**:`/user/register`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "email": "",
  "password": "",
  "phone": "",
  "userName": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|registerQuery|registerQuery|body|true|注册实体|注册实体|
|&emsp;&emsp;email|||true|string||
|&emsp;&emsp;password|||true|string||
|&emsp;&emsp;phone|||true|string||
|&emsp;&emsp;userName|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


# 识别接口


## 识别接口


**接口地址**:`/detection`


**请求方式**:`POST`


**请求数据类型**:`multipart/form-data`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|cameraSubmitQuery|cameraSubmitQuery|query|true|string||
|image|image|body|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|检查结果|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|class_name|检查结果|string||
|confidence|置信值|number(double)|number(double)|
|h|框的坐标|number(double)|number(double)|
|w|框的坐标|number(double)|number(double)|
|x|框的坐标|number(double)|number(double)|
|y|框的坐标|number(double)|number(double)|


**响应示例**:
```javascript
[
	{
		"class_name": "",
		"confidence": 0,
		"h": 0,
		"w": 0,
		"x": 0,
		"y": 0
	}
]
```


# 预警管理


## 获取预警事件信息列表


**接口地址**:`/incident`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|pageNum|第几页|query|true|integer(int32)||
|pageSize|分页大小|query|true|integer(int32)||
|status|status|query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|表格分页数据对象«List«预警事件信息列表»»|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|消息状态码|integer(int32)|integer(int32)|
|msg|消息内容|string||
|rows|列表数据|array|预警事件信息列表|
|&emsp;&emsp;cameraId|摄像头id|integer(int64)||
|&emsp;&emsp;createTime|发生时间|Timestamp|Timestamp|
|&emsp;&emsp;&emsp;&emsp;date||integer||
|&emsp;&emsp;&emsp;&emsp;day||integer||
|&emsp;&emsp;&emsp;&emsp;hours||integer||
|&emsp;&emsp;&emsp;&emsp;minutes||integer||
|&emsp;&emsp;&emsp;&emsp;month||integer||
|&emsp;&emsp;&emsp;&emsp;nanos||integer||
|&emsp;&emsp;&emsp;&emsp;seconds||integer||
|&emsp;&emsp;&emsp;&emsp;time||integer||
|&emsp;&emsp;&emsp;&emsp;timezoneOffset||integer||
|&emsp;&emsp;&emsp;&emsp;year||integer||
|&emsp;&emsp;incidentId|预警事件id|integer(int64)||
|&emsp;&emsp;level|预警等级(1-3代表低、中、高危险)|integer(int64)||
|&emsp;&emsp;location|所在位置|string||
|&emsp;&emsp;pictureUrl|图片路径|string||
|&emsp;&emsp;status|状态|integer(int64)||
|total|总记录数|integer(int64)|integer(int64)|


**响应示例**:
```javascript
{
	"code": 0,
	"msg": "",
	"rows": [
		{
			"cameraId": 0,
			"createTime": {
				"date": 0,
				"day": 0,
				"hours": 0,
				"minutes": 0,
				"month": 0,
				"nanos": 0,
				"seconds": 0,
				"time": 0,
				"timezoneOffset": 0,
				"year": 0
			},
			"incidentId": 0,
			"level": 0,
			"location": "",
			"pictureUrl": "",
			"status": 0
		}
	],
	"total": 0
}
```


## 新增预警


**接口地址**:`/incident`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求示例**:


```javascript
{
  "cameraId": 0,
  "location": "",
  "pictureUrl": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|incidentCreateQuery|incidentCreateQuery|body|true|预警新增实体|预警新增实体|
|&emsp;&emsp;cameraId|||true|integer(int64)||
|&emsp;&emsp;location|||true|string||
|&emsp;&emsp;pictureUrl|||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 查询事件详情


**接口地址**:`/incident/dtl/{incidentId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|incidentId|incidentId|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 处理事件


**接口地址**:`/incident/{incidentId}`


**请求方式**:`POST`


**请求数据类型**:`application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|Authorization|header|true|string||
|incidentId|incidentId|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|201|Created||
|401|Unauthorized||
|403|Forbidden||
|404|Not Found||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```


## 删除预警事件


**接口地址**:`/incident/{incidentId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Authorization|接口认证 Token，格式：{你的Token值}|header|true|||
|incidentId|incidentId|path|true|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|响应信息主体|
|204|No Content||
|401|Unauthorized||
|403|Forbidden||


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码|integer(int32)|integer(int32)|
|data|数据|object||
|msg|状态消息|string||


**响应示例**:
```javascript
{
	"code": 0,
	"data": {},
	"msg": ""
}
```