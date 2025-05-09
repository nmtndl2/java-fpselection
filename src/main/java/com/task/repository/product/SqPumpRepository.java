package com.task.repository.product;

import com.task.entities.product.SqPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SqPumpRepository extends JpaRepository<SqPump, Long> {
    boolean existsByPressSize(String pressSize);

    SqPump findByPressSize(String pressSize);
}
