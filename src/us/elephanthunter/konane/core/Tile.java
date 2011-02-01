package us.elephanthunter.konane.core;

/**
 * Refers to a hard, verified position on the board
 */
public class Tile extends Position {
	private Piece piece;

	public Tile(int x, int y) {
		super(x, y);
	}

	public void removePiece() {
		this.piece = null;
	}

	/* Getters & Setters */
	public void setPiece(Piece piece) { 
		this.piece = piece; 
	}
	
	public Piece getPiece() { 
		return piece; 
	}
}