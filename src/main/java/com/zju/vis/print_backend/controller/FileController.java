package com.zju.vis.print_backend.controller;

import com.zju.vis.print_backend.service.FileService;
import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Api(description = "文件管理")
@RestController
@CrossOrigin
@RequestMapping("/file")
public class FileController {
    @Resource
    private FileService fileService;

    // 上传单个文件
    @ApiOperation(value = "上传单个文件")
    @RequestMapping(value = "/upload")
    @ResponseBody
    public ResultVo<String> uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @PostMapping("/uploadFiles")
    public ResultVo uploadFiles(@RequestParam("files") MultipartFile[] files) {
        return fileService.uploadFiles(files);
    }

    @PostMapping("/download")
    public ResultVo<String> downloadFile(@RequestParam("filePath") String filePath, final HttpServletResponse response) {
        return fileService.downloadFile(filePath, response);
    }

    @PostMapping("/importExcel")
    public ResultVo importExcel(@RequestParam("file") MultipartFile excel) {
        return fileService.importExcel(excel);
    }

    @PostMapping("/exportExcel")
    public ResultVo exportExcel(final HttpServletResponse response) {
        return fileService.exportExcel(response);
    }

}
