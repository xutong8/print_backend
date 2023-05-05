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
    public static class UserModify{
        private String applicant;
        private String userModified;
        private String userType;
        private Integer userAuthority;

        public String getApplicant() {
            return applicant;
        }

        public void setApplicant(String applicant) {
            this.applicant = applicant;
        }

        public String getUserModified() {
            return userModified;
        }

        public void setUserModified(String userModified) {
            this.userModified = userModified;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Integer getUserAuthority() {
            return userAuthority;
        }

        public void setUserAuthority(Integer userAuthority) {
            this.userAuthority = userAuthority;
        }
    }

    public String manageUser(UserModify userModify){
        User uApplicant = userRepository.findUserByUserName(userModify.getApplicant());
        if(uApplicant == null) {
            return "申请人不存在";
        }
        User uModified = userRepository.findUserByUserName(userModify.getUserModified());
        if(uModified == null){
            return "被修改人不存在";
        }
        if((uApplicant.getAuthority()&4)==0){
            return "您无修改权限";
        }
        Integer uType = -1;
        switch (userModify.getUserType()){
            case "owner" : uType = 0; break;
            case "administrator" : uType = 1; break;
            case "user" : uType = 2; break;
            default: return "请输入有效的权限名";
        }
        // 1.权限小于被修改人 2.目标权限超过自身权限 3.用户没有修改权限
        if(uApplicant.getUserType() > uModified.getUserType() || uType < uApplicant.getUserType() || uApplicant.getUserType() > 1){
            return "您的权限不足";
        }
        // 比较字符串要用equals 直接比较会比较String内存地址
        if(userModify.getUserType().equals("user")  && (userModify.getUserAuthority()>>2)>=1){
            return "用户不能拥有修改权限";
        }
        System.out.println(updateUserType(uApplicant,uModified,uType));
        // 如果是转让owner这条必定权限不足,因此不会修改owner的权限
        System.out.println(updateUserAuthority(uApplicant,uModified, userModify.getUserAuthority()));
        return userModify.getUserModified() + " 当前的用户类型被修改为" + userModify.getUserType() + "  当前的权限被修改为" + userModify.getUserAuthority();
    }

    /**
     * @param uApplicant        申请修改的用户
     * @param uModified         被修改人的用户
     * @param uType             被修改人的目标类型(Integer)
     * @return
     */
    public String updateUserType (User uApplicant,User uModified,Integer uType){
        // 1.权限小于被修改人 2.目标权限超过自身权限 3.没有修改权限的权限
        if(uApplicant.getUserType() > uModified.getUserType() || uType < uApplicant.getUserType() || ((uApplicant.getAuthority()&4) == 0)){
            return "权限不足";
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
            return "owner 转让给 " + uModified.getUserName();
        }
        return uModified.getUserName() + "用户类别被修改为 " + uType;
    }

    public String updateUserAuthority(User uApplicant,User uModified,Integer userAuthority){
        // 1.权限小于被修改人 2.用户无权修改他人 3.没有修改权限
        if(uApplicant.getUserType() > uModified.getUserType()|| uApplicant.getUserType() > 1 || (uApplicant.getAuthority()&4) == 0){
            return "权限不足";
        }
        // 位运算还要看运算位的位置
        if(uModified.getUserType() > 1 && (userAuthority>>2)>=1){
            return "用户不能拥有修改权限";
        }
        uModified.setAuthority(userAuthority);
        userRepository.save(uModified);
        return uModified.getUserName() + "用户权限被修改为 " + userAuthority;
    }

    public static class PasswordModify{
        private String applicant;
        private String userModified;
        private String password;

        public String getApplicant() {
            return applicant;
        }

        public void setApplicant(String applicant) {
            this.applicant = applicant;
        }

        public String getUserModified() {
            return userModified;
        }

        public void setUserModified(String userModified) {
            this.userModified = userModified;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    // public String updatePassword(String applicant,String userModified,String password){
    public String updatePassword(PasswordModify passwordModify){
        User uApplicant = userRepository.findUserByUserName(passwordModify.getApplicant());
        if(uApplicant == null) {
            return "申请人不存在";
        }
        User uModified = userRepository.findUserByUserName(passwordModify.getUserModified());
        if(uModified == null){
            return "被修改人不存在";
        }
        if(uApplicant.getUserType() > uModified.getUserType() || ((uApplicant.getUserType().equals(uModified.getUserType())) && uApplicant != uModified)){
            return "您无权修改该用户密码";
        }
        uModified.setPassword(passwordModify.getPassword());
        userRepository.save(uModified);
        return "密码已修改";
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
