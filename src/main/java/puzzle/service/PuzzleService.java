package puzzle.service;

import puzzle.model.ParameterDTO;
import puzzle.model.WordFoundDTO;

public interface PuzzleService {
	String setParameters(ParameterDTO parameters);

	String foundWord(WordFoundDTO wordFound, String Id);

	String viewPuzzle(String id);

	String viewWords(String id);

}
