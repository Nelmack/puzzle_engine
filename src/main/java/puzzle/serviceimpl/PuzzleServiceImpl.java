package puzzle.serviceimpl;

import java.util.ArrayList;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import puzzle.core.Puzzle;
import puzzle.model.ParameterDTO;
import puzzle.model.PuzzleDTO;
import puzzle.model.WordFoundDTO;
import puzzle.repository.PuzzleRepository;
import puzzle.service.PuzzleService;

@Service
public class PuzzleServiceImpl implements PuzzleService {

	@Autowired
	PuzzleRepository puzzleRepository;

	@PersistenceContext
	private EntityManager em;

	@Override
	public String setParameters(ParameterDTO parameters) {
		// TODO Auto-generated method stub

		ArrayList<String> words = new ArrayList<String>();
		words.add("krizztel");
		words.add("manuel");
		words.add("reina");
		words.add("carnada");
		words.add("bufanda");
		words.add("futuro");
		words.add("Peregil");
		words.add("adormecido");

		Puzzle puzzle = new Puzzle();
		Map<String, Object> settings = new HashMap<String, Object>();

		settings.put("height", parameters.getH() > 0 ? String.valueOf(parameters.getH()) : "15");
		settings.put("width", parameters.getW() > 0 ? String.valueOf(parameters.getW()) : "15");
		
		String message = new String();
		
		int height =  Integer.parseInt((String) settings.get("height"));
		int width =  Integer.parseInt((String) settings.get("width"));
		
		if (   height>=15 
			&& height<=80  
			&& width>=15 
			&& width<=80)
		{

				ArrayList<String> orientations = new ArrayList<String>();
		
				if (parameters.isLtr()) {
					orientations.add("horizontal");
				}
		
				if (parameters.isRtl()) {
					orientations.add("horizontalBack");
				}
		
				if (parameters.isTtb()) {
					orientations.add("vertical");
				}
		
				if (parameters.isBtt()) {
					orientations.add("verticalUp");
				}
		
				if (parameters.isD()) {
					orientations.add("diagonal");
					orientations.add("diagonalUp");
					orientations.add("diagonalBack");
					orientations.add("diagonalUpBack");
				}
		
				if (orientations.size() == 0) {
					orientations.add("horizontal");
					orientations.add("vertical");
				}
		
				settings.put("orientations", orientations);
		
				settings.put("fillBlanks", "true");
				settings.put("maxAttempts", "3");
				settings.put("preferOverlap", "true");
		
				String[][] puzzleContent = puzzle.newPuzzle(words, settings);
				JSONArray jsPuzzleContent = puzzle.convertArrayStringToJSONArray(puzzleContent);
		
				String[][] puzzleContentSolve = puzzle.solveWords(puzzleContent, words);
		
				PuzzleDTO puzzleDto = new PuzzleDTO();
		
				JSONArray jsArraySettings = puzzle.convertMapTOJSONArray(settings);
		
				puzzleDto.setParameters(jsArraySettings.toString());
				puzzleDto.setPuzzle(jsPuzzleContent.toString());
		
				JSONObject jsonPuzzleContentSolve = new JSONObject(puzzle.solve(puzzleContentSolve, words));
				puzzleDto.setSolve(jsonPuzzleContentSolve.toString());
		
				JSONArray jsArray = new JSONArray();
				for (int k = 0; k < words.size(); k++) {
					JSONObject jsObject = new JSONObject();
					jsObject.put("word", words.get(k));
					jsArray.put(jsObject);
				}
		
				puzzleDto.setWords(jsArray.toString());
		
				puzzleRepository.save(puzzleDto);
		
				JSONObject objectJSObject = new JSONObject();
				objectJSObject.put("id", puzzleDto.getCorrId().toString());
				message =objectJSObject.toString();
		}else {
			message= "No paso la validacion de tamaños minimos y maximos! estos deben ser mayores o iguales a 15 y menores o iguales a 80";
			JSONObject objectJSObject = new JSONObject();
			objectJSObject.put("message", message);
			message =objectJSObject.toString();
		}
		
		return message;

	}

	@Override
	public String foundWord(WordFoundDTO wordFound, String id) {

		String query = " select pu.id, pu.solve, pu.parameters  from PuzzleDTO pu" + " where pu.corrId = :id";

		Query q = em.createQuery(query);

		if (null != id) {
			q.setParameter("id", id);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();

		JSONObject jsObject = new JSONObject(String.valueOf(results.get(0)[1]));
		JSONArray jsFound = jsObject.getJSONArray("found");

		String mensaje = "Palabra incorrecta";
		Map<String, Object> mapRespuesta = new HashMap<String, Object>();
		for (int i = 0; i < jsFound.length(); i++) {

			JSONObject jsonFoundWord = jsFound.getJSONObject(i);

			int sr = Integer.parseInt(jsonFoundWord.getString("sr"));
			int sc = Integer.parseInt(jsonFoundWord.getString("sc"));
			int er = Integer.parseInt(jsonFoundWord.getString("er"));
			int ec = Integer.parseInt(jsonFoundWord.getString("ec"));
			String word = String.valueOf(jsonFoundWord.get("word"));

			if (sr == wordFound.getSr() && sc == wordFound.getSc() && er == wordFound.getEr()
					&& ec == wordFound.getEc()) {

				mensaje = "Palabra correcta";

				Optional<PuzzleDTO> optionalPuzzleDto = puzzleRepository.findById((Long) results.get(0)[0]);
				PuzzleDTO puzzleDto = optionalPuzzleDto.get();

				Puzzle puzzle = new Puzzle();

				JSONArray jsonParameters = new JSONArray(String.valueOf(results.get(0)[2]));
				JSONObject jsonParametersObject = jsonParameters.getJSONObject(0);
				int width = jsonParametersObject.getInt("width") > 0 ? jsonParametersObject.getInt("width") : 15;
				int height = jsonParametersObject.getInt("height") > 0 ? jsonParametersObject.getInt("height") : 15;

				JSONArray jsPuzzle = new JSONArray(puzzleDto.getPuzzle());
				String[][] puzzleArray = puzzle.convertJSONArrayToArrayString(jsPuzzle, width, height);

				ArrayList<String> wordArray = new ArrayList<String>();
				wordArray.add(word);

				String[][] puzzleSolve = puzzle.solveWords(puzzleArray, wordArray);

				JSONArray jsPuzzleContentSolve = puzzle.convertArrayStringToJSONArray(puzzleSolve);

				puzzleDto.setPuzzle(jsPuzzleContentSolve.toString());

				puzzleRepository.save(puzzleDto);

			}

			mapRespuesta.put("mensaje", mensaje);
		}

		JSONObject jsonMensajeRespuesta = new JSONObject(mapRespuesta);

		return String.valueOf(jsonMensajeRespuesta.toString());

	}

	@Override
	public String viewPuzzle(String id) {
		// TODO Auto-generated method stub

		String query = " select pu.puzzle, pu.parameters from PuzzleDTO pu" + " where pu.corrId = :id";

		Query q = em.createQuery(query);

		if (null != id) {
			q.setParameter("id", id);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		String jsonString = String.valueOf(results.get(0)[0]);

		JSONArray arrayPuzzle = new JSONArray(jsonString);

		JSONArray jsonParameters = new JSONArray(String.valueOf(results.get(0)[1]));
		JSONObject jsonParametersObject = jsonParameters.getJSONObject(0);
		int width = jsonParametersObject.getInt("width") > 0 ? jsonParametersObject.getInt("width") : 15;
		int height = jsonParametersObject.getInt("height") > 0 ? jsonParametersObject.getInt("height") : 15;

		Puzzle puzzle = new Puzzle();

		String[][] puzzleArray = puzzle.convertJSONArrayToArrayString(arrayPuzzle, width, height);

		return String.valueOf(puzzle.showPuzzle(puzzleArray));

	}

	@Override
	public String viewWords(String id) {
		// TODO Auto-generated method stub

		String query = "select pu.words from PuzzleDTO pu" + " where pu.corrId = :id";

		Query q = em.createQuery(query);

		if (null != id) {
			q.setParameter("id", id);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.getResultList();
		String jsonString = String.valueOf(results.get(0));
		JSONArray objectJSONArray = new JSONArray(jsonString);

		String words = String.valueOf(objectJSONArray.toString());

		return words;
	}

}
