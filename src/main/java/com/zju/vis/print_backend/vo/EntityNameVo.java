package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 用户返回简单实体名
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityNameVo {
    private Long id;
    private String name;
}
