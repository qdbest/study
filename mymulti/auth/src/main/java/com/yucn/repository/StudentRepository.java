package com.yucn.repository;

import com.yucn.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Administrator on 2018/12/15.
 */
public interface StudentRepository extends JpaRepository<Student,Long> {
}
