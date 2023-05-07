package com.zju.vis.print_backend.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileConfig {

    // 上传路径
    private String uploadPath;
    private String downloadPath;
}

