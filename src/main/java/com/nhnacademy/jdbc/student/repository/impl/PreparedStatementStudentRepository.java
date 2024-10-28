package com.nhnacademy.jdbc.student.repository.impl;

import com.nhnacademy.jdbc.student.domain.Student;
import com.nhnacademy.jdbc.student.repository.StudentRepository;
import com.nhnacademy.jdbc.util.DbUtils;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

@Slf4j
public class PreparedStatementStudentRepository implements StudentRepository {

    @Override
    public int save(Student student){
        //todo#1 학생 등록
        String sql = "insert into jdbc_students(id,name,gender,age) values(?,?,?,?)";

        try(
                Connection connection = DbUtils.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ) {
            statement.setString(1,student.getId());
            statement.setString(2,student.getName());
            statement.setString(3,student.getGender().toString());
            statement.setInt(4,student.getAge());

            int result = statement.executeUpdate();
            log.debug("result: {}",result);
            return result;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Student> findById(String id){
        //todo#2 학생 조회

        String sql = "select * from jdbc_students where id=?";

        ResultSet resultSet = null;
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);)
        {
            statement.setString(1,id); // 쿼리 실행 전 설정해줘야 함
            resultSet = statement.executeQuery(); //select 쿼리 실행

            if(resultSet.next()){
                Student student = new Student(resultSet.getString("id"),
                       resultSet.getString("name"),
                        Student.GENDER.valueOf(resultSet.getString("gender")),
                                resultSet.getInt("age"),
                                resultSet.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(student);
            }
        }catch (SQLException e){
            e.printStackTrace();
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
    public int update(Student student){
        //todo#3 학생 수정 , name 수정
        String sql = String.format("update jdbc_students set name=?, gender=?, age=? where id=?");

        log.debug("update:{}",sql);

        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,student.getName());
            statement.setString(2,student.getGender().toString());
            statement.setInt(3,student.getAge());
            statement.setString(4,student.getId());

            int result = statement.executeUpdate();
            log.debug("result:{}",result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteById(String id){
        //todo#4 학생 삭제
        String sql = String.format("delete from jdbc_students where id=?");
        try(Connection connection = DbUtils.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1,id);
            int result = statement.executeUpdate();
            log.debug("result:{}",result);
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    }

