package com.task.repository.product;

import com.task.entities.product.Plate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlateRepository extends JpaRepository<Plate, Long> {

    boolean existsByPressSizeAndPlateType(String pressSize, String plateType);

    Plate findByPressSizeAndPlateType(String pressSize, String plateType);

    @Query("SELECT DISTINCT p.pressSize FROM Plate p WHERE p.plateType = ?1")
    List<String> findDistinctPressSizeByPlateType(String plateType);


}
