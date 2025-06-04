package com.task.repository.product;

import com.task.entities.product.Input;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InputRepository extends JpaRepository<Input, Long> {

}
