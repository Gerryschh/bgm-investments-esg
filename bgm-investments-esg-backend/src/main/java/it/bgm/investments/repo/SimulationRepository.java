package it.bgm.investments.repo;

import it.bgm.investments.domain.Simulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {
}