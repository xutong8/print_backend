package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 密码修改用结构
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordModifyVo {
    private String applicant;
    private String userModified;
    private String password;
}
