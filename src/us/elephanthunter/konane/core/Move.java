package us.elephanthunter.konane.core;

/**
 * Represents a possible Konane move for a piece
 */
public abstract class Move {
	protected Piece piece;

	/**
	 * Set the piece for this move
	 * @param piece
	 */
	public void setPiece(Piece piece) { 
		this.piece = piece;
	}

	/**
	 * Get the piece for this move
	 * @return
	 */
	public Piece getPiece() { 
		return piece;	
	}
}
