package com.zju.vis.print_backend.Utils;

import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.common.ApplicationContextHolder;
import com.zju.vis.print_backend.configuration.FileConfig;
import com.zju.vis.print_backend.vo.ResultVo;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.util.Streams;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

@Slf4j
public class FileUtil {

    // 自定义的枚举类
    @Getter
    public enum ResultCodeEnum {
        SUCCESS(200, "成功")
        ,
        ERROR(301, "错误")
        ,
        PARAM_ERROR(303, "参数错误")
        ,
        FILE_NOT_EXIST(304, "文件不存在")
        ,
        CLOSE_FAILD(305, "关闭流失败")
        ;

        private Integer code;
        private String message;

        ResultCodeEnum(Integer code, String message) {
            this.code = code;
            this.message = message;
        }

    }

    // 自定义异常 ParamErrorException
    @Data
    public static class ParamErrorException extends RuntimeException {
        // 错误码
        private Integer code;

        // 错误消息
        private String msg;

        public ParamErrorException() {
            this(ResultCodeEnum.PARAM_ERROR.getCode(), ResultCodeEnum.PARAM_ERROR.getMessage());
        }
        public ParamErrorException(String msg) {
            this(ResultCodeEnum.PARAM_ERROR.getCode(), msg);
        }
        public ParamErrorException(Integer code, String msg) {
            super(msg);
            this.code = code;
            this.msg = msg;
        }

    }

    private static FileConfig fileConfig = ApplicationContextHolder.getContext().getBean(FileConfig.class);

    // 下划线
    public static final String UNDER_LINE = "_";

    // 将上传的文件转换为一个新的文件名
    public static String getNewFileName(MultipartFile file) {
        // 1.获取上传的文件名称（包含后缀。如：test.jpg）
        String originalFilename = file.getOriginalFilename();
        log.info("【上传文件】上传的文件名为{}", originalFilename);
        // 2.以小数点进行分割
        String[] split = originalFilename.split("\\.");
        String newFileName = null;
        if (null == split || split.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (1 == split.length) {
            // 3.此文件无后缀
            newFileName = builder.append(originalFilename).append(UNDER_LINE).append(System.nanoTime()).toString();
            return newFileName;
        }
        // 4.获取文件的后缀
        String fileSuffix = split[split.length - 1];
        for (int i = 0; i < split.length - 1; i++) {
            builder.append(split[i]);
            if (null != split[i + 1] && "" != split[i + 1]) {
                builder.append(UNDER_LINE);
            }
        }
        newFileName = builder.append(System.nanoTime()).append(".").append(fileSuffix).toString();
        return newFileName;
    }

    // 上传文件
    public static ResultVo<String> uploadFile(MultipartFile file) {
        // 1.获取一个新的文件名
        String newFileName = getNewFileName(file);
        if (StringUtil.isBlank(newFileName)) {
            log.error("【上传文件】转换文件名称失败");
            return ResultVoUtil.error("【上传文件】转换文件名称失败");
        }
        // 2.获取文件上传路径
        String uploadPath = fileConfig.getUploadPath();
        if (StringUtil.isBlank(uploadPath)) {
            log.error("【上传文件】获取文件上传路径失败");
            return ResultVoUtil.error("【上传文件】获取文件上传路径失败");
        }
        uploadPath = uploadPath + File.separator +  DateUtil.getCurrentDate();
        // 3.生成上传目录
        File uploadDir = mkdirs(uploadPath);
        if (!uploadDir.exists()) {
            log.error("【上传文件】生成上传目录失败");
            return ResultVoUtil.error("【上传文件】生成上传目录失败");
        }
        // 4.文件全路径
        String fileFullPath = uploadPath + File.separator + newFileName;
        log.info("上传的文件：" + file.getName() + "，" + file.getContentType() + "，保存的路径为：" + fileFullPath);
        try {
            // 5.上传文件
            doUploadFile(file, fileFullPath);
        } catch (IOException e) {
            log.error("【上传文件】上传文件报IO异常，异常信息为{}", e.getMessage());
            return ResultVoUtil.error(e.getMessage());
        }
        return ResultVoUtil.success(fileFullPath);
    }

    // 生成相应的目录
    public static File mkdirs(String path) {
        File file = new File(path);
        if(!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }

    // 上传文件
    public static void doUploadFile(MultipartFile file, String path) throws IOException {
        // 法一:
        Streams.copy(file.getInputStream(), new FileOutputStream(path), true);

        // 法二: 通过MultipartFile#transferTo(File)
        // 使用此方法保存，必须要绝对路径且文件夹必须已存在,否则报错
        //file.transferTo(new File(path));

        // 法三：通过NIO将字节写入文件
        //Path filePath = Paths.get(path);
        //Files.write(filePath, file.getBytes());

        // 法四：
        /*try (InputStream in = file.getInputStream();
             FileOutputStream out = new FileOutputStream(path)) {
            IOUtils.copy(in, out);
        } catch (Exception e) {
            log.error("【上传文件】上传文件失败，失败信息为：{}", e.getMessage());
        }*/

        // 法五：
        /*InputStream in = file.getInputStream();
        OutputStream out = new FileOutputStream(path);
        int len = 0;
        byte[] bytes = new byte[1024];
        while ((len = in.read(bytes)) != -1) {
            out.write(bytes, 0, len);
        }
        in.close();
        out.close();*/

        // 法六：
        /*byte[] bytes = file.getBytes();
        OutputStream out = new FileOutputStream(path);
        out.write(bytes);
        out.close();*/
    }

    public static ResultVo<String> downloadFile(File file, HttpServletResponse response) {
        try {
            // 1.设置响应头
            setResponse(file, response);
        } catch (UnsupportedEncodingException e) {
            log.error("文件名{}不支持转换为字符集{}", file.getName(), "UTF-8");
            return ResultVoUtil.error(e.getMessage());
        }
        // 2.下载文件
        return doDownLoadFile(file, response);
    }

    public static void setResponse(File file, HttpServletResponse response) throws UnsupportedEncodingException {
        // 清空response
        response.reset();
        response.setCharacterEncoding("UTF-8");
        // 返回给客户端类型，任意类型
        response.setContentType("application/octet-stream");
        // Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        // attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", String.valueOf(file.length()));
    }

    public static ResultVo<String> doDownLoadFile(File file, HttpServletResponse response) {
        // 法一：IOUtils
    /*try (FileInputStream in = new FileInputStream(file);
         OutputStream out = response.getOutputStream()) {
        // 2.下载文件
        IOUtils.copy(in, out);
        log.info("【文件下载】文件下载成功");
        return null;
    } catch (FileNotFoundException e) {
        log.error("【文件下载】下载文件时，没有找到相应的文件，文件路径为{}", file.getAbsolutePath());
        return ResultVoUtil.error(e.getMessage());
    } catch (IOException e) {
        log.error("【文件下载】下载文件时，出现文件IO异常");
        return ResultVoUtil.error(e.getMessage());
    }*/

        // 法二：将文件以流的形式一次性读取到内存，通过响应输出流输出到前端
    /*try (InputStream in = new BufferedInputStream(new FileInputStream(file));
         OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        out.write(buffer);
        log.info("【文件下载】文件下载成功");
        return null;
    } catch (IOException e) {
        log.error("【文件下载】下载文件时，出现文件IO异常");
        return ResultVoUtil.error(e.getMessage());
    }*/

        // 法三：将输入流中的数据循环写入到响应输出流中，而不是一次性读取到内存，通过响应输出流输出到前端
        try (InputStream in = new FileInputStream(file);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            log.info("【文件下载】文件下载成功");
            return null;
        } catch (FileNotFoundException e){
            log.error("【文件下载】下载文件时，没有找到相应的文件，文件路径为{}", file.getAbsolutePath());
            return ResultVoUtil.error(e.getMessage());
        } catch (IOException e) {
            log.error("【文件下载】下载文件时，出现文件IO异常");
            return ResultVoUtil.error(e.getMessage());
        }
    }

    // 递归删除目录下的所有文件及子目录下所有文件
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            String[] children = file.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteFile(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return file.delete();
    }

    // 获取文件下载时生成文件的路径
    public static String getDownLoadPath() {
        return fileConfig.getDownloadPath();
    }
}
