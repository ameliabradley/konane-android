package us.elephanthunter.konane.core;

/**
 * Represents a move that removes a piece from the board
 */
public class RemovePieceMove extends Move {
	public RemovePieceMove(Piece piece) {
		this.piece = piece;
	}

	public boolean equals(Object object) {
		if (!(object instanceof RemovePieceMove)) { 
			return false;
		}
		// The game pieces must be the same
		RemovePieceMove move = (RemovePieceMove) object;
		return (move.getPiece() == this.piece);
	}
}