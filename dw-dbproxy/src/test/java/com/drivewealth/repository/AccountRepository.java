package com.drivewealth.repository;

import com.drivewealth.cr.grpc.server.AccountRequest;
import com.drivewealth.dbproxy.entity.Account;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;




@Repository
public interface AccountRepository {


    String INSERT = "";

    String SELECT_BY_ACCOUNTID = "";

    String DELETE_BY_ACCOUNTID = "";

    String UPDATE_BY_ACCOUNTID = "";


    @Insert(INSERT)
    int insertAccount(AccountRequest accountRequest);

    @Select(SELECT_BY_ACCOUNTID)
    Account selectAccount(@Param("id") String id);

    @Delete(DELETE_BY_ACCOUNTID)
    int deleteAccount(@Param("id") String id);

    @Update(UPDATE_BY_ACCOUNTID)
    int updateAccount(AccountRequest accountRequest);


    //    @Select("SELECT * from accounts")
//    List<AccountRequest> findAll();

    //    @Select("SELECT accountID, accountNo, commissionID,currencyID,accountType,cash,disableSubscriptions,id from accounts WHERE id = #{id}")
//    List<AccountRequest> selectAccountByID(@Param("id") String id);
}
