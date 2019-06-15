package com.pinyougou.controller;

import com.pinyougou.pojo.Result;
import com.pinyougou.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 作    者： yingming shen
 * 修改时间： 2019/6/12 9:29.
 * 描   述：  处理后台上传的文件到FASTDFS服务器
 */
@RestController
public class UpLoadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result upLoad(MultipartFile file){
        try {
            //0.新建文件上传客户端工具类
            FastDFSClient client = new FastDFSClient("classpath:conf/FastDFS_client.conf");
            //1.获得文件名
            String fimeName = file.getOriginalFilename();
            //2.获得文件名后缀
            String suffix = fimeName.substring(fimeName.lastIndexOf(".")+1);
            //3.上传，并获得图片在服务器中的地址
            String path = client.uploadFile(file.getBytes(),suffix);
            //4.绑定数据并返回
            return new Result(true,FILE_SERVER_URL+path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false ,"文件上传失败！");
        }

    }
}
