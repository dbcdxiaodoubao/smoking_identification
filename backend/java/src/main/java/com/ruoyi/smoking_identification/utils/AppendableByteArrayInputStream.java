package com.ruoyi.smoking_identification.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 支持增量追加的 ByteArrayInputStream（适配 FFmpegFrameGrabber 增量解码）
 */
public class AppendableByteArrayInputStream extends InputStream {
    // 底层用 ByteArrayOutputStream 存储数据（支持动态追加）
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    // 用于读取的输入流（基于 outputStream 的字节数组）
    private ByteArrayInputStream inputStream;

    /**
     * 追加字节数组到流中
     */
    public synchronized void append(byte[] data) throws IOException {
        outputStream.write(data);
        // 重置输入流（指向最新的字节数组）
        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * 读取字节（重写 InputStream 方法）
     */
    @Override
    public synchronized int read() throws IOException {
        if (inputStream == null) {
            return -1; // 无数据时返回-1（流结束）
        }
        return inputStream.read();
    }

    /**
     * 批量读取字节（提升效率，FFmpeg 会调用此方法）
     */
    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (inputStream == null) {
            return -1;
        }
        return inputStream.read(b, off, len);
    }

    /**
     * 可用字节数（FFmpeg 会通过此方法判断是否有新数据）
     */
    @Override
    public synchronized int available() throws IOException {
        return inputStream != null ? inputStream.available() : 0;
    }

    /**
     * 重置流（可选，用于异常后恢复）
     */
    @Override
    public synchronized void reset() throws IOException {
        if (inputStream != null) {
            inputStream.reset();
        }
    }

    /**
     * 关闭流（释放资源）
     */
    @Override
    public synchronized void close() throws IOException {
        outputStream.close();
        if (inputStream != null) {
            inputStream.close();
        }
    }
}