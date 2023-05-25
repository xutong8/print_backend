package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 返回用户标准信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStandardVo {
    Integer status;
    String userName;
    String userType;
    Integer authority;
}
