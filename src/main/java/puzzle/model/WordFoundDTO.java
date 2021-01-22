package puzzle.model;

public class WordFoundDTO {
	int sr, sc, er, ec;

	public int getSr() {
		return sr;
	}

	public void setSr(int sr) {
		this.sr = sr;
	}

	public int getSc() {
		return sc;
	}

	public void setSc(int sc) {
		this.sc = sc;
	}

	public int getEr() {
		return er;
	}

	public void setEr(int er) {
		this.er = er;
	}

	public int getEc() {
		return ec;
	}

	public void setEc(int ec) {
		this.ec = ec;
	}

	@Override
	public String toString() {
		return "WordFoundDTO [sr=" + sr + ", sc=" + sc + ", er=" + er + ", ec=" + ec + "]";
	}

}
