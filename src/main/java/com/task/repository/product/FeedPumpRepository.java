package com.task.repository.product;

import com.task.entities.product.FeedPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FeedPumpRepository extends JpaRepository<FeedPump, Long> {
    boolean existsByPressSize(String pressSize);

    Optional<FeedPump> findByPressSize(String pressSize);

}
