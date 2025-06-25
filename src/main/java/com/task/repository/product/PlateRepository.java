package com.task.repository.product;

import com.task.entities.product.Plate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {

    boolean existsByPressSizeAndPlateType(String pressSize, String plateType);

    Plate findByPressSizeAndPlateType(String pressSize, String plateType);

    boolean existsByPlateType(String plateType);
}
