package puzzle.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import puzzle.model.ParameterDTO;
import puzzle.model.WordFoundDTO;
import puzzle.service.PuzzleService;

@RestController
@RequestMapping("/alphabetSoup")
public class PuzzleController {

	@Autowired
	PuzzleService puzzleService;

	@PostMapping("/")
	public ResponseEntity<Object> setParameters(ParameterDTO parameterDTO) {
		try {

			return new ResponseEntity<>(puzzleService.setParameters(parameterDTO), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject objectJSObject = new JSONObject();
			objectJSObject.put("message", e.toString());

			return new ResponseEntity<>(objectJSObject.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/view/{uuid}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> viewPuzzle(
			@ApiParam(name = "uuid", type = "String", value = "Identificador del puzzle a obtener.", example = "1", required = true) @PathVariable(required = true) String uuid) {

		try {
			return new ResponseEntity<>(puzzleService.viewPuzzle(uuid), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject objectJSObject = new JSONObject();
			objectJSObject.put("message", e.toString());

			return new ResponseEntity<>(objectJSObject.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/list/{uuid}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> viewWords(
			@ApiParam(name = "uuid", type = "String", value = "Identificador del puzzle a obtener.", example = "1", required = true) @PathVariable(required = true) String uuid) {
		try {
			return new ResponseEntity<>(puzzleService.viewWords(uuid), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject objectJSObject = new JSONObject();
			objectJSObject.put("message", e.toString());

			return new ResponseEntity<>(objectJSObject.toString(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping(value = "/{uuid}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> foundWord(
			@ApiParam(name = "uuid", type = "String", value = "Identificador del puzzle a obtener.", example = "1", required = true) @PathVariable(required = true) String uuid,
			WordFoundDTO wordFound) {
		try {
			return new ResponseEntity<>(puzzleService.foundWord(wordFound, uuid), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject objectJSObject = new JSONObject();
			objectJSObject.put("message", e.toString());

			return new ResponseEntity<>(objectJSObject.toString(), HttpStatus.BAD_REQUEST);
		}
	}

}
