package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.UserRepository;
import com.zju.vis.print_backend.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    // 返回用户标准信息
    public class UserStandard{
        Integer status;
        String userName;
        String userType;
        Integer authority;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Integer getAuthority() {
            return authority;
        }

        public void setAuthority(Integer authority) {
            this.authority = authority;
        }
    }

    // 返回用户简要信息
    public class UserSimple{
        String userName;
        String userType;
        Integer authority;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Integer getAuthority() {
            return authority;
        }

        public void setAuthority(Integer authority) {
            this.authority = authority;
        }
    }

    // 打包标准化用户信息
    public UserStandard packUser(User user){
        UserStandard userStandard = new UserStandard();
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
    public List<UserSimple> findAllUserSimple(){
        List<UserSimple> userSimpleList = new ArrayList<>();
        for(User user: userRepository.findAll()){
            UserSimple userSimple = new UserSimple();
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

    public UserStandard doLogin(String userName, String password){
        User user = userRepository.findUserByUserNameAndPassword(userName, password);
        System.out.println("userName: " + userName + " password: " + password);
        System.out.println(user == null);
        return packUser(user);
    }

    public UserStandard doRegister(String userName, String password){
        User user = new User();
        if (userRepository.findUserByUserName(userName) != null){
            System.out.println("添加失败");
            return packUser(null);
        }
        user.setUserName(userName);
        user.setPassword(password);
        user.setUserType(2);
        user.setAuthority(0);
        return packUser(userRepository.save(user));
    }

    //改
    //-------------------------------------------------------------------------

    /**
     * @param applicant     申请修改的用户名
     * @param userModified  被修改人的用户名
     * @param userType      被修改人的目标类型
     * @return
     */
    public String updateUserType (String applicant,String userModified,String userType){
        User uApplicant = userRepository.findUserByUserName(applicant);
        if(uApplicant == null) {
            return "申请人不存在";
        }
        User uModified = userRepository.findUserByUserName(userModified);
        if(uModified == null){
            return "被修改人不存在";
        }
        Integer uType = -1;
        switch (userType){
            case "owner" : uType = 0; break;
            case "administrator" : uType = 1; break;
            case "user" : uType = 2; break;
            default: return "请输入有效的权限名";
        }
        // 1.权限小于被修改人 2.目标权限超过自身权限 3.没有修改权限的权限
        if(uApplicant.getUserType() > uModified.getUserType() || uType < uApplicant.getUserType() || ((uApplicant.getAuthority()&4) == 0)){
            return "权限不足";
        }
        // 如果被修改为用户则失去修改他人权限的权限
        if(uType == 2){
            uModified.setAuthority(uModified.getAuthority()%4);
        }
        uModified.setUserType(uType);
        userRepository.save(uModified);
        // 如果是更改owner则视为转让owner降级为administrator
        if(uType == 0 && uApplicant.getUserType() == 0){
            // 被转让者获得所有权限
            updateUserAuthority(uApplicant.getUserName(),uModified.getUserName(),7);
            updateUserType(uApplicant.getUserName(),uApplicant.getUserName(),"administrator");
        }
        return userModified + "用户类别被修改为" + userType;
    }

    public String updateUserAuthority(String applicant,String userModified,Integer userAuthority){
        User uApplicant = userRepository.findUserByUserName(applicant);
        if(uApplicant == null) {
            return "申请人不存在";
        }
        User uModified = userRepository.findUserByUserName(userModified);
        if(uModified == null){
            return "被修改人不存在";
        }
        // 1.权限小于被修改人 2.用户无权修改他人 3.没有修改权限
        if(uApplicant.getUserType() > uModified.getUserType()|| uApplicant.getUserType() > 1 || (uApplicant.getAuthority()&4) == 0){
            return "权限不足";
        }
        if(uModified.getUserType() > 1 && (userAuthority&4) == 1){
            return "用户不能拥有修改权限";
        }
        uModified.setAuthority(userAuthority);
        userRepository.save(uModified);
        return userModified + "用户权限被修改为" + userAuthority;
    }

    //删
    //-------------------------------------------------------------------------
    @Transactional
    public Boolean doDelete(String userName){
        // 拥有者不能删除，只能转让
        if(userRepository.findUserByUserName(userName).getUserType() == 0){
            return false;
        }
        userRepository.deleteByUserName(userName);
        return true;
    }


}
