# RTSP转HLS配置指南

## 概述

本系统使用HLS (HTTP Live Streaming) 方式播放RTSP视频流，需要后端转码服务将RTSP转换为HLS格式。本文档提供了多种转码方案的配置说明。

## 方案一：使用FFmpeg快速转码（测试环境）

### 安装FFmpeg

#### Windows
```bash
# 使用Chocolatey安装
choco install ffmpeg

# 或者下载预编译版本
# https://ffmpeg.org/download.html
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install ffmpeg
```

#### macOS
```bash
# 使用Homebrew安装
brew install ffmpeg
```

### 转码命令

```bash
# 基本转码命令
ffmpeg -i "rtsp://摄像头地址" -c:v h264 -c:a aac -f hls -hls_time 2 -hls_list_size 3 -hls_flags delete_segments ./hls/stream.m3u8

# 示例：转码摄像头RTSP流
ffmpeg -i "rtsp://192.168.1.100:554/Streaming/Channels/101" \
  -c:v h264 -c:a aac -f hls \
  -hls_time 2 -hls_list_size 3 -hls_flags delete_segments \
  -hls_base_url "http://localhost:8080/hls/" \
  ./hls/stream.m3u8
```

### 参数说明

- `-i "rtsp://..."`: 输入RTSP流地址
- `-c:v h264`: 视频编码器（保持H264格式）
- `-c:a aac`: 音频编码器
- `-f hls`: 输出格式为HLS
- `-hls_time 2`: 每个切片时长（秒）
- `-hls_list_size 3`: 播放列表中保留的切片数量
- `-hls_flags delete_segments`: 自动删除旧切片
- `-hls_base_url`: 播放列表中的基础URL

### 启动HTTP服务器

```bash
# 使用Python简单HTTP服务器
cd ./hls
python -m http.server 8080

# 或者使用Node.js http-server
npx http-server ./hls -p 8080
```

## 方案二：使用专业流媒体服务器（生产环境）

### 1. EasyDarwin

#### 安装
```bash
# Docker方式安装
docker run -d --name easydarwin -p 10000:10000 \
  -v /path/to/config:/easydarwin/config \
  -v /path/to/movies:/easydarwin/movies \
  easydarwin/easydarwin:latest
```

#### 配置RTSP转HLS
```json
{
  "hls": {
    "enable": true,
    "segment_duration": 2,
    "segment_count": 3,
    "delete_segments": true,
    "output_path": "./hls"
  }
}
```

### 2. ZLMediaKit

#### 安装
```bash
# 克隆代码
git clone https://github.com/ZLMediaKit/ZLMediaKit.git
cd ZLMediaKit

# 编译安装
git submodule update --init
./configure && make -j4
```

#### 配置文件 (config.ini)
```ini
[http]
# HTTP服务器端口
port=8080

[hls]
# HLS配置
enable=1
segmentDuration=2
segmentNum=3
deleteSegments=1
```

#### 启动RTSP转HLS
```bash
# 启动ZLMediaKit
./Release/Linux/ZLMediaKit -c config.ini

# 添加RTSP流并转换为HLS
curl -X POST "http://localhost:8080/index/api/addStreamProxy" \
  -d "secret=035c73f7-bb6b-4889-a715-d9eb2d1925cc&vhost=__defaultVhost__&app=live&stream=test&url=rtsp://192.168.1.100:554/Streaming/Channels/101"
```

### 3. SRS (Simple Realtime Server)

#### 安装
```bash
# Docker方式安装
docker run -d --name srs -p 1935:1935 -p 8080:8080 \
  -v $PWD/conf:/usr/local/srs/conf \
  ossrs/srs:4
```

#### 配置文件 (conf/rtsp2hls.conf)
```bash
listen              1935;
max_connections     1000;

http_server {
    enabled         on;
    listen          8080;
    dir             ./objs/nginx/html;
}

http_api {
    enabled         on;
    listen          1985;
}

rtsp {
    enabled         on;
    listen          554;
}

hls {
    enabled         on;
    hls_path        ./objs/nginx/html;
    hls_fragment    2;
    hls_window      3;
}

vhost __defaultVhost__ {
    hls {
        enabled         on;
        hls_path        ./objs/nginx/html;
        hls_fragment    2;
        hls_window      3;
    }
    
    http_remux {
        enabled     on;
        mount       [vhost]/[app]/[stream].hls;
    }
}
```

## 方案三：使用容器化部署

### Docker Compose配置

```yaml
version: '3.8'

services:
  ffmpeg:
    image: jrottenberg/ffmpeg:4.4-alpine
    command: -i rtsp://192.168.1.100:554/Streaming/Channels/101 -c:v h264 -c:a aac -f hls -hls_time 2 -hls_list_size 3 -hls_flags delete_segments -hls_base_url http://localhost:8080/hls/ /data/hls/stream.m3u8
    volumes:
      - ./hls:/data/hls
    ports:
      - "8080:8080"
    depends_on:
      - nginx

  nginx:
    image: nginx:alpine
    ports:
      - "8080:80"
    volumes:
      - ./hls:/usr/share/nginx/html/hls
      - ./nginx.conf:/etc/nginx/nginx.conf
```

### Nginx配置 (nginx.conf)
```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    server {
        listen 80;
        server_name localhost;

        location /hls/ {
            types {
                application/vnd.apple.mpegurl m3u8;
                video/mp2t ts;
            }
            root /usr/share/nginx/html;
            add_header Cache-Control no-cache;
            add_header Access-Control-Allow-Origin *;
        }
    }
}
```

## 前端配置

### 修改RTSP到HLS的转换规则

在 `src/views/monitor/index.vue` 文件中的 `convertRtspToHls` 函数中，根据您的转码服务配置修改转换规则：

```javascript
const convertRtspToHls = (rtspUrl) => {
  try {
    // 提取IP地址和端口
    const urlMatch = rtspUrl.match(/rtsp:\/\/([^\/]+)/)
    if (!urlMatch) {
      throw new Error('无效的RTSP地址格式')
    }
    
    const host = urlMatch[1]
    
    // 提取通道号
    const channelMatch = rtspUrl.match(/Channels\/(\d+)/)
    const channel = channelMatch ? channelMatch[1] : '101'
    
    // 根据您的转码服务配置修改这里
    // 示例1: FFmpeg本地转码
    const hlsUrl = `http://localhost:8080/hls/stream${channel}.m3u8`
    
    // 示例2: ZLMediaKit转码
    // const hlsUrl = `http://${host}:8080/live/${channel}.m3u8`
    
    // 示例3: EasyDarwin转码
    // const hlsUrl = `http://${host}:10000/hls/${channel}.m3u8`
    
    return hlsUrl
  } catch (error) {
    console.error('RTSP到HLS地址转换失败:', error)
    ElMessage.warning('无法自动转换RTSP地址，使用示例HLS地址')
    return 'http://localhost:8080/hls/stream.m3u8'
  }
}
```

## 故障排除

### 1. HLS流无法播放

- 检查M3U8文件是否可访问：`curl http://localhost:8080/hls/stream.m3u8`
- 检查TS切片文件是否存在：`ls -la ./hls/`
- 检查网络防火墙设置

### 2. 延迟过高

- 减少HLS切片时长：`-hls_time 1`
- 减少播放列表大小：`-hls_list_size 1`
- 考虑使用WebRTC方案（更低延迟）

### 3. CPU占用过高

- 使用硬件加速：`-c:v h264_nvenc` (NVIDIA) 或 `-c:v h264_vaapi` (Intel)
- 降低视频分辨率和码率
- 使用专业流媒体服务器

## 性能优化建议

1. **生产环境**：推荐使用ZLMediaKit或SRS等专业流媒体服务器
2. **多路流**：考虑使用集群部署和负载均衡
3. **存储**：HLS切片文件会占用磁盘空间，定期清理
4. **网络**：确保网络带宽足够支持多路视频流
5. **监控**：监控转码服务的CPU、内存和网络使用情况

## 安全注意事项

1. **访问控制**：配置RTSP流的用户名和密码
2. **HTTPS**：生产环境建议使用HTTPS传输HLS流
3. **防火墙**：限制对转码服务的访问
4. **认证**：考虑添加Token认证机制

## 总结

1. 测试环境可使用FFmpeg快速搭建转码服务
2. 生产环境推荐使用专业流媒体服务器
3. 根据实际需求调整转码参数和配置
4. 注意性能优化和安全配置

如有问题，请查看控制台日志获取详细错误信息。