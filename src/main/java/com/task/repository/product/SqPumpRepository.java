package com.task.repository.product;

import com.task.entities.product.SqPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SqPumpRepository extends JpaRepository<SqPump, Long> {
    boolean existsByPressSize(String pressSize);

    SqPump findByPressSize(String pressSize);
}
