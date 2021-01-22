package puzzle.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clase que contiene los metodos para creación, modificación del puzzle Asi
 * como tambien permite recorrerla y validar resultados.
 */
public class Puzzle {

	private String[] letters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "r",
			"s", "t", "u", "v", "w", "y" };
	private ArrayList<String> allOrientations = new ArrayList<String>(Arrays.asList("horizontal", "horizontalBack",
			"vertical", "verticalUp", "diagonal", "diagonalUp", "diagonalBack", "diagonalUpBack"));

	public String[] getLetters() {
		return letters;
	}

	public ArrayList<String> getAllOrientations() {
		return allOrientations;
	}

	/********************************************************************************
	 * 
	 * Metodo que pobla de vacios la matriz del puzzle, obedeciendo los parametros
	 * entregados por el servicio.
	 * 
	 */

	public String[][] fillPuzzle(ArrayList<String> wordArray, Map<String, Object> options) {
		String[][] puzzle = new String[Integer.parseInt((String) options.get("height"))][Integer
				.parseInt((String) options.get("width"))];

		int i, j;

		for (i = 0; i < Integer.parseInt((String) options.get("height")); i++) {
			for (j = 0; j < Integer.parseInt((String) options.get("width")); j++) {
				puzzle[i][j] = "";
			}
		}

		for (i = 0; i < wordArray.size(); i++) {
			if (!placeWordInPuzzle(puzzle, options, wordArray.get(i).toCharArray())) {

				return null;
			}
		}

		return puzzle;
	}

	/********************************************************************************
	 * 
	 * Metodo que crea la matriz del puzle teniendo en cuenta un listado de
	 * palabras, el numero de filas y columnas
	 * 
	 */

	public String[][] newPuzzle(ArrayList<String> words, Map<String, Object> settings) {
		String[][] puzzle = new String[Integer.parseInt((String) settings.get("height"))][Integer
				.parseInt((String) settings.get("width"))];
		int attempts = 0;
		Map<String, Object> opts = settings;

		int major = 0;
		for (int i = 0; i < words.size(); i++) {

			if (i == 0) {
				major = words.get(i).length();
			} else

			if (major < words.get(i).length()) {
				major = words.get(i).length();
			}
		}

		Map<String, Object> options = new HashMap<String, Object>();

		options.put("height", opts.get("height") != null ? opts.get("height").toString() : String.valueOf(major));
		options.put("width", opts.get("width") != null ? opts.get("width").toString() : String.valueOf(major));
		options.put("orientations", opts.get("orientations") != null ? opts.get("orientations") : getAllOrientations());
		options.put("fillBlanks", opts.get("fillBlanks") != null ? opts.get("fillBlanks").toString() : "true");
		options.put("maxAttempts", opts.get("maxAttempts") != null ? opts.get("maxAttempts").toString() : "3");
		options.put("preferOverlap", opts.get("preferOverlap") != null ? opts.get("preferOverlap").toString() : "true");

		while ((attempts < Integer.parseInt((String) options.get("maxAttempts")))) {
			puzzle = (String[][]) fillPuzzle(words, options);
			attempts = attempts + 1;
		}

		options.put("height", String.valueOf(Integer.parseInt((String) options.get("height")) + 1));
		options.put("width", String.valueOf(Integer.parseInt((String) options.get("width")) + 1));
		attempts = 0;

		if (String.valueOf(opts.get("fillBlanks")).equalsIgnoreCase("true")) {
			fillBlanks(puzzle);
		}

		return puzzle;
	}

	/********************************************************************************
	 * 
	 * Este es un proceso posterior que asigna a los espacios vacios letras
	 * aleatorias. Este proceso se ejecuta luega de haber posicionado las palabras
	 * en el puzzle
	 * 
	 * 
	 */

	public void fillBlanks(String[][] puzzle) {
		for (int i = 0, height = puzzle.length; i < height; i++) {
			String[] row = puzzle[i];
			for (int j = 0, width = row.length; j < width; j++) {

				if (puzzle[i][j].equalsIgnoreCase("") || puzzle[i][j].isEmpty()) {
					int randomLetter = (int) Math.floor(Math.random() * getLetters().length);
					puzzle[i][j] = letters[randomLetter];
				}
			}
		}
	}

	/********************************************************************************
	 * 
	 * Metodo que asinga el lugar a cada letra de las palabras que entraran en el
	 * juego, teniendo en cuenta la orientacion y tamanio de la letra.
	 * 
	 * 
	 */

	public void placeWord(String[][] puzzle, char[] word, int x, int y, String orientation) {
		for (int i = 0, len = word.length; i < len; i++) {
			Map<String, Integer> next = getSquare(orientation, x, y, i);
			puzzle[next.get("y")][next.get("x")] = String.valueOf(word[i]);
		}
	}

	/********************************************************************************
	 * 
	 * Entra las posibles ubicaciones donde encajara la palabra, haciendo la
	 * superposicion de palabras posible Luego llama a placeWord que se encarga de
	 * ubicar la letra en el puzzle
	 */

	public Boolean placeWordInPuzzle(String[][] puzzle, Map<String, Object> options, char[] word) {

		ArrayList<Map<String, String>> locations = findBestLocations(puzzle, options, word);
		if (locations.size() == 0) {
			return false;
		}

		Map<String, String> sel = locations.get((int) Math.floor(Math.random() * locations.size()));
		placeWord(puzzle, word, Integer.parseInt(sel.get("x")), Integer.parseInt(sel.get("y")), sel.get("orientation"));

		return true;

	};

	/********************************************************************************
	 * Determina todas las posiciones donde la palabra puede encajar, determina la
	 * coordenada de inicio de la palabra, el numero de letras que se superponen con
	 * las ya existentes
	 * 
	 * 
	 */

	public ArrayList<Map<String, String>> findBestLocations(String[][] puzzle, Map<String, Object> options,
			char[] word) {
		ArrayList<Map<String, String>> locations = new ArrayList<Map<String, String>>();
		Map<String, String> locationInterno = new HashMap<String, String>();
		int height = Integer.parseInt((String) options.get("height"));
		int width = Integer.parseInt((String) options.get("width"));
		int wordLength = word.length;
		int maxOverlap = 0;

		@SuppressWarnings("unchecked")
		ArrayList<String> temporalListWords = (ArrayList<String>) options.get("orientations");

		String[] temporal = temporalListWords.toArray(new String[0]);

		for (int k = 0; k < temporal.length; k++) {
			@SuppressWarnings("unchecked")
			String orientation = ((ArrayList<String>) options.get("orientations")).get(k);
			int x = 0, y = 0;

			while (y < height) {

				if (checkOrientations(orientation, x, y, height, width, wordLength)) {
					int overlap = calcOverlap(word, puzzle, x, y, orientation);
					if (overlap >= maxOverlap || (!String.valueOf(options.get("preferOverlap")).equalsIgnoreCase("true")
							&& overlap > -1)) {
						maxOverlap = overlap;
						locationInterno.put("x", String.valueOf(x));
						locationInterno.put("y", String.valueOf(y));
						locationInterno.put("orientation", orientation);
						locationInterno.put("overlap", String.valueOf(overlap));
						locations.add(locationInterno);
					}

					x = x + 1;
					if (x >= width) {
						x = 0;
						y = y + 1;
					}
				} else {
					Map<String, Integer> nextPossible = skipOrientations(orientation, x, y, wordLength);
					x = nextPossible.get("x");
					y = nextPossible.get("y");

				}

			}
		}

		return String.valueOf(options.get("preferOverlap")).equalsIgnoreCase("true")
				? pruneLocations(locations, maxOverlap)
				: locations;
	}

	/********************************************************************************
	 * 
	 * Maximiza la superposicion, sirve para pulir la lista de ubicaciones validas,
	 * y se queda con la máxima superposición que se calculo previamente.
	 * 
	 * 
	 */

	public ArrayList<Map<String, String>> pruneLocations(ArrayList<Map<String, String>> locations, int overlap) {

		ArrayList<Map<String, String>> pruned = new ArrayList<Map<String, String>>();
		for (int i = 0, len = locations.size(); i < len; i++) {
			if (Integer.parseInt(locations.get(i).get("overlap")) >= overlap) {

				pruned.add(locations.get(i));
			}
		}

		return pruned;
	};

	/********************************************************************************
	 * 
	 * Metodo que determina el salto de la letra en la orientación indicada
	 * 
	 * 
	 */

	public Map<String, Integer> skipOrientations(String orientation, int x, int y, int l) {
		Map<String, Integer> skip = new HashMap<String, Integer>();

		if (orientation.equalsIgnoreCase("horizontal")) {
			x = 0;
			y = y + 1;
		} else if (orientation.equalsIgnoreCase("horizontalBack")) {
			x = l - 1;

		} else if (orientation.equalsIgnoreCase("vertical")) {
			x = 0;
			y = y + 100;
		} else if (orientation.equalsIgnoreCase("verticalUp")) {
			x = 0;
			y = l - 1;
		} else if (orientation.equalsIgnoreCase("diagonal")) {
			x = 0;
			y = y + 1;
		} else if (orientation.equalsIgnoreCase("diagonalBack")) {
			int condicion = l - 1;

			if (x >= condicion) {
				y = y + 1;
			}
			x = l - 1;

		} else if (orientation.equalsIgnoreCase("diagonalUp")) {
			int condicion = l - 1;
			if (y < condicion) {
				y = l - 1;
			} else {
				y = y + 1;
			}
			x = 0;
		} else if (orientation.equalsIgnoreCase("diagonalUpBack")) {
			int condicion = l - 1;
			if (x >= condicion) {
				y = y + 1;
			}
			x = l - 1;
		}

		skip.put("x", x);
		skip.put("y", y);

		return skip;

	}

	/********************************************************************************
	 * 
	 * Determina si una orientación es posible teniendo en cuenta la ubicación
	 * inicial de la palabra en el puzzle, la anchura y la altura de puzzle, como
	 * tambien el tamaño de la palabra. Devuelve verdadero si la palabra encaja.
	 * 
	 * 
	 */

	public Boolean checkOrientations(String orientation, int x, int y, int h, int w, int l) {
		Boolean check = false;

		if (orientation.equalsIgnoreCase("horizontal")) {

			check = w >= x + l ? true : false;
		} else if (orientation.equalsIgnoreCase("horizontalBack")) {
			check = x + 1 >= l ? true : false;
		} else if (orientation.equalsIgnoreCase("vertical")) {
			check = h >= y + l ? true : false;
		} else if (orientation.equalsIgnoreCase("verticalUp")) {
			check = y + 1 >= l ? true : false;
		} else if (orientation.equalsIgnoreCase("diagonal")) {
			check = (w >= x + l) && (h >= y + l) ? true : false;
		} else if (orientation.equalsIgnoreCase("diagonalBack")) {
			check = (x + 1 >= l) && (h >= y + l) ? true : false;
		} else if (orientation.equalsIgnoreCase("diagonalUp")) {
			check = (w >= x + l) && (y + 1 >= l) ? true : false;
		} else if (orientation.equalsIgnoreCase("diagonalUpBack")) {
			check = (x + 1 >= l) && (y + 1 >= l) ? true : false;
		}

		return check;

	}

	/********************************************************************************
	 * 
	 * Calcula la siguiente ubicacion dado un punto de inicio.
	 * 
	 * 
	 */

	public Map<String, Integer> getSquare(String orientation, int x, int y, int i) {
		Map<String, Integer> square = new HashMap<String, Integer>();

		if (orientation.equalsIgnoreCase("horizontal")) {
			x = x + i;
		} else if (orientation.equalsIgnoreCase("horizontalBack")) {
			x = x - i;
		} else if (orientation.equalsIgnoreCase("vertical")) {
			y = y + i;
		} else if (orientation.equalsIgnoreCase("verticalUp")) {
			y = y - i;
		} else if (orientation.equalsIgnoreCase("diagonal")) {
			x = x + i;
			y = y + i;
		} else if (orientation.equalsIgnoreCase("diagonalBack")) {
			x = x - i;
			y = y + i;
		} else if (orientation.equalsIgnoreCase("diagonalUp")) {
			x = x + i;
			y = y - i;
		} else if (orientation.equalsIgnoreCase("diagonalUpBack")) {
			x = x - i;
			y = y - i;
		}

		square.put("x", x);
		square.put("y", y);

		return square;
	}

	/********************************************************************************
	 * 
	 * 
	 * 
	 * 
	 */

	public int calcOverlap(char[] word, String[][] puzzle, int x, int y, String orientation) {
		int overlap = 0;

		for (int i = 0, len = word.length; i < len; i++) {

			Map<String, Integer> next = getSquare(orientation, x, y, i);

			String square = puzzle[next.get("y")][next.get("x")];
			if (square.equalsIgnoreCase(String.valueOf(word[i]))) {
				overlap = overlap + 1;
			}

			else if (square != "") {
				return -1;
			}
		}

		return overlap;
	}

	/********************************************************************************
	 * 
	 * Ayuda en la presenación del puzzle en formato de texto.
	 * 
	 * 
	 */

	public String showPuzzle(String puzzle[][]) {
		String puzzleString = new String();
		for (int i = 0; i < puzzle.length; i++) {
			if (i == 0)
				puzzleString = puzzleString.concat("|");
			for (int k = 0; k < puzzle.length; k++) {
				puzzleString = puzzleString.concat(" " + String.valueOf(puzzle[i][k]) + " |");
			}
			if (i + 1 < puzzle.length)
				puzzleString = puzzleString.concat("\n|");
			else
				puzzleString = puzzleString.concat("\n");
		}
		return puzzleString;
	}

	/********************************************************************************
	 * 
	 * Metodo que encuentra una palabra en el puzle. En caso de haberla la agrega a
	 * las encontradas, caso contrario la adiciona en los no encontrados. Tambien
	 * nos da datos de posicion inicial y final de las palabras encontradas.
	 * 
	 * 
	 */

	public Map<String, Object> solve(String[][] puzzle, ArrayList<String> words) {

		Map<String, Object> options = new HashMap<String, Object>();

		options.put("height", String.valueOf(puzzle.length));
		options.put("width", String.valueOf(puzzle[0].length));
		options.put("orientations", getAllOrientations());
		options.put("preferOverlap", "true");

		ArrayList<Map<String, Object>> found = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> notFound = new ArrayList<Map<String, Object>>();

		Map<String, Object> response = new HashMap<String, Object>();

		ArrayList<Map<String, String>> locations = new ArrayList<Map<String, String>>();
		for (int i = 0, len = words.size(); i < len; i++) {
			String word = words.get(i);
			locations = findBestLocations(puzzle, options, word.toCharArray());
			Map<String, Object> foundAux = new HashMap<String, Object>();
			Map<String, Object> notFoundAux = new HashMap<String, Object>();
			if (locations.size() > 0 && Integer.parseInt(locations.get(0).get("overlap")) == word.length()) {
				foundAux.put("word", word);
				foundAux.put("orientation", locations.get(0).get("orientation"));
				foundAux.put("overlap", locations.get(0).get("overlap").toString());
				foundAux.put("sc", locations.get(0).get("x").toString());
				foundAux.put("sr", locations.get(0).get("y").toString());

				Map<String, Integer> finalPos = getSquare(String.valueOf(locations.get(0).get("orientation")),
						Integer.parseInt(locations.get(0).get("x").toString()),
						Integer.parseInt(locations.get(0).get("y").toString()), (word.length() - 1));
				foundAux.put("ec", finalPos.get("x").toString());
				foundAux.put("er", finalPos.get("y").toString());
				found.add(foundAux);
			} else {
				notFoundAux.put("word", word);
				notFound.add(notFoundAux);

			}
		}
		response.put("found", found);
		response.put("notFound", notFound);

		return response;
	}

	/********************************************************************************
	 * 
	 * Ayuda con la visibilidad de las palabras encontradas, pasandolas a mayusculas
	 * 
	 * 
	 */

	public String[][] solveWords(String[][] puzzle, ArrayList<String> words) {
		Map<String, Object> solution = (Map<String, Object>) solve(puzzle, words);
		@SuppressWarnings("unchecked")
		ArrayList<Map<String, Object>> solutionFound = (ArrayList<Map<String, Object>>) solution.get("found");

		for (int i = 0, len = solutionFound.size(); i < len; i++) {
			char[] word = String.valueOf(solutionFound.get(i).get("word")).toCharArray();
			String orientation = String.valueOf(solutionFound.get(i).get("orientation"));

			int x = Integer.parseInt((String) solutionFound.get(i).get("sc"));
			int y = Integer.parseInt((String) solutionFound.get(i).get("sr"));

			for (int j = 0, size = word.length; j < size; j++) {
				Map<String, Integer> nextPos = getSquare(orientation, x, y, j);
				puzzle[nextPos.get("y")][nextPos.get("x")] = puzzle[nextPos.get("y")][nextPos.get("x")].toUpperCase();
			}
		}

		return puzzle;

	}

	/********************************************************************************
	 * 
	 * Utilidad para mapear un String Array a JSONArray
	 * 
	 * 
	 */

	public JSONArray convertArrayStringToJSONArray(String[][] puzzle) {

		JSONArray jsonConvertPuzzle = new JSONArray();

		for (int i = 0; i < puzzle.length; i++) {
			for (int j = 0; j < puzzle[0].length; j++) {
				JSONObject jsConvertObject = new JSONObject();
				jsConvertObject.put("x", String.valueOf(i));
				jsConvertObject.put("y", String.valueOf(j));
				jsConvertObject.put("v", puzzle[i][j]);
				jsonConvertPuzzle.put(jsConvertObject);
			}
		}
		return jsonConvertPuzzle;
	}

	/********************************************************************************
	 * 
	 * Utilidad para mapear un JSONArray a String Arrays.
	 * 
	 * 
	 */

	public String[][] convertJSONArrayToArrayString(JSONArray jsArrayConvert, int width, int height) {

		String[][] puzzleArray = new String[width][height];

		for (int k = 0; k < jsArrayConvert.length(); k++) {
			JSONObject jsObject = new JSONObject();
			jsObject = jsArrayConvert.getJSONObject(k);
			puzzleArray[jsObject.getInt("x")][jsObject.getInt("y")] = jsObject.getString("v");

		}

		return puzzleArray;
	}

	/********************************************************************************
	 * 
	 * Utilidad para mapear un Map a un JSONArray
	 * 
	 * 
	 */

	public JSONArray convertMapTOJSONArray(Map<String, Object> mapToConvert) {

		JSONArray jsArrayConvert = new JSONArray();
		JSONObject jsObjectConvert = new JSONObject();
		for (Map.Entry<String, Object> entry : mapToConvert.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			jsObjectConvert.put(key, value);
		}
		jsArrayConvert.put(jsObjectConvert);
		return jsArrayConvert;
	}

}