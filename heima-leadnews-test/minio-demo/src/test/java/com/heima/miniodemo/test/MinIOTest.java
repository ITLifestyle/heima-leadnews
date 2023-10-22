package com.heima.miniodemo.test;

import com.heima.file.service.FileStorageService;
import com.heima.miniodemo.MinIoDemoApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import okhttp3.OkHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes = MinIoDemoApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {
    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void startTest() throws IOException {
        try (FileInputStream inputStream = new FileInputStream("D:\\IT\\test\\list.html");) {
            String path = fileStorageService.uploadHtmlFile("", "list2.html", inputStream);
            System.out.println(path);
        }
    }

    @Test
    public void uploadToMinIO() throws FileNotFoundException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try (FileInputStream inputStream = new FileInputStream("D:\\IT\\test\\plugins\\js\\vue.min.js");) {
            // 1. 获取 minio 的链接信息, 创建一个minio客户端
            MinioClient build = MinioClient.builder()
                    .credentials("minio", "minio123")
                    .endpoint("http://192.168.200.130:9000")
                    .build();

            // 2. 上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("plugins/js/vue.min.js") // 文件名
                    .contentType("text/javascript") // 文件类型
                    .bucket("leadnews") // minio bucket名称, 即桶名称
                    .stream(inputStream, inputStream.available(), -1)
                    .build();

            build.putObject(putObjectArgs);

            // 访问
            System.out.println("http://192.168.200.130:9000/leadnews/list.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
