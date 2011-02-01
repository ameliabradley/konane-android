package us.elephanthunter.konane.core;

public class Position {
	private int x;
	private int y;

	public Position(Position position) {
		this.move(position.getX(), position.getY());
	}

	public Position(int x, int y) {
		this.move(x, y);
	}

	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("BoardPosition (");
		result.append(Integer.toString(x + 1));
		result.append(", ");
		result.append(Integer.toString(y + 1));
		result.append(")");
		return result.toString();
	}

	/* Getters & Setters */
	public void setX(int x) {
		this.x = x; 
	}
	
	public int getX() { 
		return x; 
	}

	public void setY(int y) { 
		this.y = y; 
	}
	
	public int getY() { 
		return y; 
	}
}
