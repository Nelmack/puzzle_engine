package puzzle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import puzzle.model.PuzzleDTO;

@Repository
public interface PuzzleRepository extends JpaRepository<PuzzleDTO, Long> {

}
