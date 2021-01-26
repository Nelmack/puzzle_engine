package puzzle.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class PuzzleDTO {

	@Id
	@GeneratedValue
	private Long id;

	String corrId;

	public PuzzleDTO() {
		this.corrId = UUID.randomUUID().toString();
	}

	private String parameters;
	@Lob
	private String puzzle;
	@Lob
	private String solve;
	private String words;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCorrId() {
		return corrId;
	}

	public void setCorrId(String corrId) {
		this.corrId = corrId;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getPuzzle() {
		return puzzle;
	}

	public void setPuzzle(String puzzle) {
		this.puzzle = puzzle;
	}

	public String getSolve() {
		return solve;
	}

	public void setSolve(String solve) {
		this.solve = solve;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	@Override
	public String toString() {
		return "PuzzleDTO [id=" + id + ", corrId=" + corrId + ", parameters=" + parameters + ", puzzle=" + puzzle
				+ ", solve=" + solve + ", words=" + words + "]";
	}

}
