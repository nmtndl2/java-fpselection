package com.task.repository.product;

import com.task.entities.product.PlateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateTypeRepository extends JpaRepository<PlateType, Long> {
    boolean existsByTypeName(String typeName);
}
