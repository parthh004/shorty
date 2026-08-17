package com.tss.shorty.repository;

import com.tss.shorty.entity.SystemConfig;
import com.tss.shorty.repository.projection.ConfigProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ConfigRepository extends JpaRepository<SystemConfig, String> {
    @Query(value =
            "SELECT c.code AS code, " +
                    "       c.description AS description, " +
                    "       c.value AS value, " +
                    "       v.type AS dataType " +
                    "FROM system_config c " +
                    "JOIN value_types v ON c.type_id = v.type_id " +
                    "WHERE c.code IN (:codes)",
            nativeQuery = true)
    List<ConfigProjection> findSpecificConfigsWithTypes(@Param("codes") List<String> codes);
}
