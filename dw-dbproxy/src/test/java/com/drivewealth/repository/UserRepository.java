package com.drivewealth.repository;


import com.drivewealth.cr.grpc.server.UserRequest;
import com.drivewealth.dbproxy.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository {


    String INSERT_SQL_STRING = "";
    String DELETE_BY_USERID = "";
    String SELECT_BY_USERID = "";
    String SELECT_ALL_USERS = "";
    String UPDATE_BY_USERID = "";


    @Insert(INSERT_SQL_STRING)
    int insertUser(UserRequest userRequest);

    @Select(SELECT_BY_USERID)
    User selectUser(@Param("userID") String userID);

    @Delete(DELETE_BY_USERID)
    int deleteUser(@Param("userID") String userID);

    @Select(SELECT_ALL_USERS)
    List<User> findAll();

    @Update(UPDATE_BY_USERID)
    int updateUser(UserRequest userRequest);
}