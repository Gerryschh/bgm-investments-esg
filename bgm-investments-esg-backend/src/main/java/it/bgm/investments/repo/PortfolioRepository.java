package it.bgm.investments.repo;

import it.bgm.investments.domain.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByOwnerId(Long ownerId);

    @Query("select p from Portfolio p where p.id=:id and p.owner.id=:ownerId")
    Optional<Portfolio> findOwned(@Param("id") Long id, @Param("ownerId") Long ownerId);
}