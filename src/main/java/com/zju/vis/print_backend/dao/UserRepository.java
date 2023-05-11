package com.zju.vis.print_backend.dao;

import com.zju.vis.print_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 查单个
    User findUserByUserName(String userName);
    User findUserByUserNameAndPassword(String userName, String Password);

    // 删除
    void deleteByUserName(String userName);
}
