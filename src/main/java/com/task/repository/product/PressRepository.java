package com.task.repository.product;

import com.task.entities.product.Press;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PressRepository extends JpaRepository<Press, Long> {
    boolean existsByPressSize(String pressSize);

    Press findByPressSize(String pressSize);

    @Query("SELECT p.maxChamber FROM Press p WHERE p.pressSize = :pressSize")
    Optional<Integer> findMaxChamberByPressSize(String pressSize);
}
