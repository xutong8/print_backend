package com.zju.vis.print_backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserModifyVo {
    private String applicant;
    private String userModified;
    private String userType;
    private Integer userAuthority;
}
