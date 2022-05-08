package com.gjl.controller;

import com.gjl.common.R;
import org.apache.ibatis.javassist.ClassPath;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    /**
     * 获取配置的文件上传放置的路径
     */
    @Value("${reggie.path}")
    private String BasePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //获取上传的文件名
        String originalFilename = file.getOriginalFilename();
        //截取上传文件的后缀名
        String suffix=originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名
        String fileName= UUID.randomUUID().toString()+suffix;
        //创建一个目录对象
        File dir=new File(BasePath);
        //判断当前目录是否存在
        if (!dir.exists()){
            //不存在则，创建
            dir.mkdir();
        }
        try {
            //将上传的文件放置在指定的文件夹
            file.transferTo(new File(BasePath+fileName));
        }catch (IOException e){
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            FileInputStream fileInputStream=new FileInputStream(new File(BasePath+name));

            ServletOutputStream outputStream=response.getOutputStream();

            response.setContentType("image/jpeg");

            int len=0;
            byte[] bytes=new byte[1024];

            while (( len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
