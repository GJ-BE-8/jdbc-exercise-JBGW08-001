package com.nhnacademy.jdbc.user.repository.impl;

import com.nhnacademy.jdbc.user.domain.User;
import com.nhnacademy.jdbc.user.repository.UserRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class PreparedStatementUserRepository implements UserRepository {
    @Override
    public Optional<User> findByUserIdAndUserPassword(String userId, String userPassword) {
        //todo#11 -PreparedStatement- 아이디 , 비밀번호가 일치하는 회원조회
        String sql ="select user_id, user_name, user_password from jdbc_users where user_id=? and user_password=?";
        log.debug("sql: {}", sql);

        ResultSet resultSet = null;
        try(
                Connection connection = DbUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1,userId);
            statement.setString(2,userPassword);
            resultSet = statement.executeQuery();

            if(resultSet.next()){
                User user = new User(resultSet.getString("user_id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("user_password"));

                return Optional.of(user);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(String userId) {
        //todo#12-PreparedStatement-회원조회
        String sql = "select user_id, user_name, user_password from jdbc_users where user_id=?";

        ResultSet resultSet= null;
        try(
                Connection connection = DbUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
        ){
                statement.setString(1,userId);
                resultSet = statement.executeQuery();

            if(resultSet.next()){
                User user = new User(resultSet.getString("user_id"),
                        resultSet.getString("user_name"),
                        resultSet.getString("user_password")
                );
                return Optional.of(user);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }finally {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    @Override
    public int save(User user) {
        //todo#13-PreparedStatement-회원저장
        String sql = "insert into jdbc_users (user_id, user_name, user_password) values (?,?,?)";


        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ){
            statement.setString(1,user.getUserId());
            statement.setString(2, user.getUserName());
            statement.setString(3,user.getUserPassword());
            int result = statement.executeUpdate();
            log.debug("save:{}",result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int updateUserPasswordByUserId(String userId, String userPassword) {
        //todo#14-PreparedStatement-회원정보 수정
        String sql = "update jdbc_users set user_password=? where user_id=?";
        log.debug("update:{}",sql);

        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,userPassword);
            statement.setString(2,userId);

            int result = statement.executeUpdate();
            log.debug("result:{}",result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteByUserId(String userId) {
        //todo#15-PreparedStatement-회원삭제
        String sql ="delete from jdbc_users where user_id=?";
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,userId);
            int result = statement.executeUpdate();
            log.debug("result:{}",result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
