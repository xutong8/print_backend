package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 返回用户简要信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleVo {
    String userName;
    String userType;
    Integer authority;
}
