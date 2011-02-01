package us.elephanthunter.konane.core;

public class Piece {
	private PlayerColor color;
	private Tile tile;

	/* Getters & Setters */
	public void setTile(Tile tile) { 
		this.tile = tile;
	}
	
	public Tile getTile() { 
		return tile; 
	}

	public void setColor(PlayerColor color) {
		this.color = color; 
	}
	
	public PlayerColor getColor() { 
		return this.color;
	}
}
