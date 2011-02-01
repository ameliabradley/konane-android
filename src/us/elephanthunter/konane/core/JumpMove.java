package us.elephanthunter.konane.core;

public class JumpMove extends Move {
	protected Tile tileTo;

	public JumpMove(Piece piece, Tile tileTo) {
		this.piece = piece;
		this.tileTo = tileTo;
	}

	public boolean equals(Object object) {
		if (!(object instanceof JumpMove)) { 
			return false;
		}

		// The game pieces and tiles must be the same
		JumpMove move = (JumpMove) object;
		return ((move.getPiece() == this.piece) && (move.getTileTo() == this.tileTo));
	}

	public void setTileTo(Tile tileTo) { 
		this.tileTo = tileTo;
	}

	public Tile getTileTo() { 
		return tileTo; 
	}
}