package it.bgm.investments.repo;

import it.bgm.investments.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {
    @Query("select a from Asset a where (:activeOnly is null or a.active = :activeOnly) and (:settore is null or a.settore = :settore)")
    List<Asset> search(@Param("activeOnly") Boolean activeOnly, @Param("settore") String settore);
}