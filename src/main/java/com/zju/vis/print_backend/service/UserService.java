package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.Utils.ResultVoUtil;
import com.zju.vis.print_backend.dao.UserRepository;
import com.zju.vis.print_backend.entity.User;
import com.zju.vis.print_backend.vo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    // 打包标准化用户信息
    public UserStandardVo packUser(User user){
        UserStandardVo userStandard = new UserStandardVo();
        if(user == null){
            userStandard.setStatus(-1);
            return userStandard;
        }
        userStandard.setUserName(user.getUserName());
        switch (user.getUserType()){
            case 0 :
                userStandard.setUserType("owner");
                break;
            case 1 :
                userStandard.setUserType("administrator");
                break;
            case 2 :
                userStandard.setUserType("user");
                break;
            default:
                userStandard.setUserType("no such person");
        }
        userStandard.setAuthority(user.getAuthority());
        userStandard.setStatus(1);
        return userStandard;
    }

    // 返回所有用户简要信息
    public List<UserSimpleVo> findAllUserSimple(){
        List<UserSimpleVo> userSimpleList = new ArrayList<>();
        for(User user: userRepository.findAll()){
            UserSimpleVo userSimple = new UserSimpleVo();
            userSimple.setUserName(user.getUserName());
            switch (user.getUserType()){
                case 0 :
                    userSimple.setUserType("owner");
                    break;
                case 1 :
                    userSimple.setUserType("administrator");
                    break;
                case 2 :
                    userSimple.setUserType("user");
                    break;
                default:
                    userSimple.setUserType("no such person");
            }
            userSimple.setAuthority(user.getAuthority());
            userSimpleList.add(userSimple);
        }
        return userSimpleList;
    }

    public ResultVo doLogin(UserLoginVo userLoginVo){
        User user = userRepository.findUserByUserNameAndPassword(userLoginVo.getUserName(), userLoginVo.getPassword());
        if(user == null){
            return ResultVoUtil.error("账号密码错误请重新输入");
        }
        return ResultVoUtil.success(packUser(user));
    }

    public ResultVo doRegister(UserLoginVo userLoginVo){
        User user = new User();
        if (userRepository.findUserByUserName(userLoginVo.getUserName()) != null){
            return ResultVoUtil.error("用户名已被使用注册失败");
        }
        user.setUserName(userLoginVo.getUserName());
        user.setPassword(userLoginVo.getPassword());
        user.setUserType(2);
        user.setAuthority(0);
        return ResultVoUtil.success(packUser(userRepository.save(user)));
    }

    public ResultVo manageUser(UserModifyVo userModify){
        User uApplicant = userRepository.findUserByUserName(userModify.getApplicant());
        if(uApplicant == null) {
            return ResultVoUtil.error("申请人不存在");
        }
        User uModified = userRepository.findUserByUserName(userModify.getUserModified());
        if(uModified == null){
            return ResultVoUtil.error("被修改人不存在");
        }
        if((uApplicant.getAuthority()&4)==0){
            return ResultVoUtil.error("您无修改权限");
        }
        Integer uType = -1;
        switch (userModify.getUserType()){
            case "owner" : uType = 0; break;
            case "administrator" : uType = 1; break;
            case "user" : uType = 2; break;
            default: return ResultVoUtil.error("请输入有效的权限名");
        }
        // 1.权限小于被修改人 2.目标权限超过自身权限 3.用户没有修改权限
        if(uApplicant.getUserType() > uModified.getUserType() || uType < uApplicant.getUserType() || uApplicant.getUserType() > 1){
            return ResultVoUtil.error("您的权限不足");
        }
        // 比较字符串要用equals 直接比较会比较String内存地址
        if(userModify.getUserType().equals("user")  && (userModify.getUserAuthority()>>2)>=1){
            return ResultVoUtil.error("用户不能拥有修改权限");
        }
        // 修改用户类型
        ResultVo result = updateUserType(uApplicant,uModified,uType);
        if(!result.checkSuccess()){
            return result;
        }
        // 转让时不支持同时修改权限
        if(uType != 0){
            // 修改用户权限
            result = updateUserAuthority(uApplicant,uModified, userModify.getUserAuthority());
            if(!result.checkSuccess()){
                return result;
            }
        }
        return ResultVoUtil.success(
                200,
                userModify.getUserModified() + " 用户类型被修改为" + userModify.getUserType() + "  权限被修改为" + userModify.getUserAuthority(),
                null);
    }

    //改
    //-------------------------------------------------------------------------
    /**
     * @param uApplicant        申请修改的用户
     * @param uModified         被修改人的用户
     * @param uType             被修改人的目标类型(Integer)
     * @return
     */
    public ResultVo updateUserType (User uApplicant,User uModified,Integer uType){
        // 1.权限小于被修改人 2.目标权限超过自身权限 3.没有修改权限的权限
        if(uApplicant.getUserType() > uModified.getUserType() || uType < uApplicant.getUserType() || ((uApplicant.getAuthority()&4) == 0)){
            return ResultVoUtil.error("权限不足");
        }
        // 用户则失去修改他人权限的权限
        if(uType == 2){
            uModified.setAuthority(uModified.getAuthority()%4);
        }
        uModified.setUserType(uType);
        userRepository.save(uModified);
        // 如果是更改owner则视为转让owner降级为administrator
        if(uType == 0 && uApplicant.getUserType() == 0){
            updateUserAuthority(uApplicant,uModified,7);
            updateUserType(uApplicant,uApplicant,1);
            return ResultVoUtil.success(200,"拥有者权限转让给 --> " + uModified.getUserName(),null);
        }
        return ResultVoUtil.success(200,uModified.getUserName() + "的用户类别被修改为 --> " + uType,null);
    }

    public ResultVo updateUserAuthority(User uApplicant,User uModified,Integer userAuthority){
        // 1.权限小于被修改人 2.用户无权修改他人 3.没有修改权限
        if(uApplicant.getUserType() > uModified.getUserType()|| uApplicant.getUserType() > 1 || (uApplicant.getAuthority()&4) == 0){
            return ResultVoUtil.error("权限不足");
        }
        // 位运算还要看运算位的位置
        if(uModified.getUserType() > 1 && (userAuthority>>2)>=1){
            return ResultVoUtil.error("用户不能拥有修改权限");
        }
        uModified.setAuthority(userAuthority);
        userRepository.save(uModified);
        return ResultVoUtil.success(200,uModified.getUserName() + "的用户权限被修改为 --> " + userAuthority,null);
    }

    // public String updatePassword(String applicant,String userModified,String password){
    public ResultVo updatePassword(PasswordModifyVo passwordModify){
        User uApplicant = userRepository.findUserByUserName(passwordModify.getApplicant());
        if(uApplicant == null) {
            return ResultVoUtil.error("申请人不存在");
        }
        User uModified = userRepository.findUserByUserName(passwordModify.getUserModified());
        if(uModified == null){
            return ResultVoUtil.error("被修改人不存在");
        }
        if(uApplicant.getUserType() > uModified.getUserType() || ((uApplicant.getUserType().equals(uModified.getUserType())) && uApplicant != uModified)){
            return ResultVoUtil.error("您无权修改该用户密码");
        }
        uModified.setPassword(passwordModify.getPassword());
        userRepository.save(uModified);
        return ResultVoUtil.success(200,passwordModify.getUserModified() + "的密码已被修改",null);
    }

    //删
    //-------------------------------------------------------------------------
    @Transactional
    public ResultVo doDelete(String userName){
        // 拥有者不能删除，只能转让
        if(userRepository.findUserByUserName(userName).getUserType() == 0){
            return ResultVoUtil.error("拥有者不能删除，只能转让");
        }
        userRepository.deleteByUserName(userName);
        return ResultVoUtil.success(200,userName + "用户已被删除",null);
    }


}
