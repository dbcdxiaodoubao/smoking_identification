# RTSP转HLS配置指南

## 概述

本指南将帮助您配置RTSP到HLS的转码服务，使监控页面能够正常播放摄像头视频流。

## 问题说明

当前监控页面已经修复了RTSP到HLS地址转换的问题，转换后的地址格式为：
```
原始RTSP地址: rtsp://admin:lmp220607@192.168.0.101:554/Streaming/Channels/1
转换后HLS地址: http://192.168.0.101:8080/hls/stream1.m3u8
```

但是，要使HLS流能够正常播放，您需要配置一个转码服务，将RTSP流转换为HLS格式。

## 解决方案

### 方案一：使用FFmpeg（推荐用于测试）

1. **安装FFmpeg**
   - Windows: 下载FFmpeg并添加到PATH
   - Linux: `sudo apt-get install ffmpeg`
   - macOS: `brew install ffmpeg`

2. **运行转码命令**
   ```bash
   ffmpeg -i "rtsp://admin:lmp220607@192.168.0.101:554/Streaming/Channels/1" \
   -c:v h264 -c:a aac -f hls \
   -hls_time 2 -hls_list_size 3 -hls_flags delete_segments \
   -hls_base_url http://192.168.0.101:8080/hls/ \
   ./hls/stream1.m3u8
   ```

3. **启动HTTP服务器提供HLS文件**
   ```bash
   # 在hls目录下启动HTTP服务器
   cd ./hls
   python -m http.server 8080
   ```

### 方案二：使用Docker和FFmpeg（推荐用于生产环境）

1. **创建Dockerfile**
   ```dockerfile
   FROM alpine:latest
   RUN apk add --no-cache ffmpeg
   WORKDIR /app
   COPY . .
   CMD ["ffmpeg", "-i", "rtsp://admin:lmp220607@192.168.0.101:554/Streaming/Channels/1", "-c:v", "h264", "-c:a", "aac", "-f", "hls", "-hls_time", "2", "-hls_list_size", "3", "-hls_flags", "delete_segments", "/app/hls/stream1.m3u8"]
   ```

2. **创建docker-compose.yml**
   ```yaml
   version: '3'
   services:
     rtsp-to-hls:
       build: .
       ports:
         - "8080:8080"
       volumes:
         - ./hls:/app/hls
     http-server:
       image: nginx:alpine
       ports:
         - "8080:80"
       volumes:
         - ./hls:/usr/share/nginx/html/hls
   ```

3. **启动服务**
   ```bash
   docker-compose up -d
   ```

### 方案三：使用专业流媒体服务器（推荐用于大规模部署）

#### 1. EasyDarwin

1. **下载EasyDarwin**
   ```bash
   wget https://github.com/EasyDarwin/EasyDarwin/releases/download/v8.1.0/EasyDarwin-8.1.0-190211-release-win64.zip
   ```

2. **配置EasyDarwin**
   - 编辑EasyDarwin.ini文件
   - 添加RTSP推流配置

3. **启动EasyDarwin**
   ```bash
   ./EasyDarwin.exe
   ```

#### 2. ZLMediaKit

1. **下载ZLMediaKit**
   ```bash
   git clone https://github.com/xia-chu/ZLMediaKit.git
   cd ZLMediaKit
   git submodule update --init
   ```

2. **编译和安装**
   ```bash
   ./build_for_linux.sh
   ```

3. **配置ZLMediaKit**
   - 编辑config.ini文件
   - 添加RTSP和HLS配置

4. **启动ZLMediaKit**
   ```bash
   ./Release/Linux/Server/ZLMediaKit -c config.ini
   ```

#### 3. SRS (Simple Realtime Server)

1. **下载SRS**
   ```bash
   git clone https://github.com/ossrs/srs.git
   cd srs
   ```

2. **编译和安装**
   ```bash
   ./configure
   make
   ```

3. **配置SRS**
   - 编辑conf/rtsp2hls.conf文件

4. **启动SRS**
   ```bash
   ./objs/srs -c conf/rtsp2hls.conf
   ```

## 配置验证

1. **检查HLS流是否可访问**
   - 在浏览器中访问: `http://192.168.0.101:8080/hls/stream1.m3u8`
   - 应该能看到m3u8文件内容

2. **使用VLC播放器测试**
   - 打开VLC播放器
   - 选择"媒体" > "打开网络串流"
   - 输入HLS地址: `http://192.168.0.101:8080/hls/stream1.m3u8`
   - 确认能正常播放

3. **在监控页面中测试**
   - 访问监控页面
   - 选择摄像头
   - 点击"连接"按钮
   - 确认视频能正常播放

## 常见问题

### 1. HLS流无法播放

**问题**: 浏览器显示"视频播放失败"

**解决方案**:
- 检查转码服务是否正常运行
- 确认HLS文件是否正确生成
- 检查网络连接和防火墙设置
- 确认HTTP服务器是否正确配置CORS

### 2. 转码服务性能问题

**问题**: 视频延迟高或卡顿

**解决方案**:
- 调整FFmpeg参数，降低编码延迟
- 使用硬件加速编码
- 优化网络带宽
- 考虑使用专业流媒体服务器

### 3. 多摄像头支持

**问题**: 需要同时转码多个摄像头

**解决方案**:
- 为每个摄像头创建独立的FFmpeg进程
- 使用流媒体服务器支持多路流
- 考虑使用容器化部署

## 安全注意事项

1. **RTSP认证信息保护**
   - 不要在前端暴露RTSP认证信息
   - 考虑使用后端代理转码请求

2. **网络安全**
   - 配置防火墙规则限制访问
   - 使用HTTPS保护HLS流
   - 定期更新转码软件

3. **资源限制**
   - 限制转码进程的资源使用
   - 监控服务器性能
   - 设置合理的并发连接数

## 总结

配置RTSP到HLS转码服务是实现实时监控的关键步骤。根据您的具体需求和环境，选择合适的方案：

- **测试环境**: 使用FFmpeg + HTTP服务器
- **小型生产环境**: 使用Docker + FFmpeg
- **大型生产环境**: 使用专业流媒体服务器

配置完成后，监控页面将能够正常播放摄像头视频流。