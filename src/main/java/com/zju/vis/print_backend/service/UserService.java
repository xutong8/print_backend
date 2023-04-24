package com.zju.vis.print_backend.service;

import com.zju.vis.print_backend.dao.UserRepository;
import com.zju.vis.print_backend.entity.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserRepository userRepository;

    public class UserStandard{
        Integer status;
        String userName;
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

        public Integer getAuthority() {
            return authority;
        }

        public void setAuthority(Integer authority) {
            this.authority = authority;
        }
    }

    public UserStandard packUser(User user){
        UserStandard userStandard = new UserStandard();
        if(user == null){
            userStandard.setStatus(-1);
            return userStandard;
        }
        userStandard.setUserName(user.getUserName());
        userStandard.setAuthority(user.getAuthority());
        userStandard.setStatus(1);
        return userStandard;
    }

    public UserStandard doLogin(String userName, String password){
        User user = userRepository.findUserByUserNameAndPassword(userName, password);
        System.out.println("userName: " + userName + " password: " + password);
        System.out.println(user == null);
        return packUser(user);
    }
}
