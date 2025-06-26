package com.agents.builder.common.core.utils.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 文件处理工具类
 *
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils extends FileUtil {

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) {
        String percentEncodedFileName = percentEncode(realFileName);
        String contentDispositionValue = "attachment; filename=%s;".formatted(percentEncodedFileName);
        response.addHeader("Access-Control-Expose-Headers", "Content-Disposition,download-filename");
        response.setHeader("Content-disposition", contentDispositionValue);
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return encode.replaceAll("\\+", "%20");
    }


    /**
     * 获取文件md5值
     *
     * @param inputStream 文件输入流
     * @return {@link String} 文件md5值
     */
    public static String getMd5(InputStream inputStream) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SneakyThrows
    public static void exportExcel(HttpServletResponse response, String filePath, String fileName, String contentType) {
        FileUtils.setAttachmentResponseHeader(response, fileName);
        response.setContentType(contentType);
        response.getOutputStream().write(FileUtil.readBytes(filePath));
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
