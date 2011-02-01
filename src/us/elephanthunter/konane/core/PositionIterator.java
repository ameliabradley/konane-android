package us.elephanthunter.konane.core;
import java.util.Iterator;

/**
 * Iterates from one Position to another
 * Does not function diagonally
 */
public class PositionIterator implements Iterator<Position> {
	protected Position positionFrom;
	protected Position positionTo;

	protected Position positionIndex;
	protected int increment;
	protected int end;
	protected PositionIteratorDirection direction;

	public void setPositions(Position positionFrom, Position positionTo) {
		this.positionFrom = positionFrom;
		this.positionTo = positionTo;
		onUpdatePosition();
	}

	public void setPositionTo(Position position) {
		positionTo = position;
		onUpdatePosition();
	}

	public void setPositionFrom(Position position) {
		positionFrom = position;
		onUpdatePosition();
	}

	private void onUpdatePosition() {
		int deltaX = positionFrom.getX() - positionTo.getX();
		int deltaY = positionFrom.getY() - positionTo.getY();

		positionIndex = new Position(positionFrom);

		if (deltaX == 0) {
			direction = PositionIteratorDirection.VERTICAL;
			increment = (deltaY < 0) ? 1 : -1;
			end = positionTo.getY();
		} else {
			direction = PositionIteratorDirection.HORIZONTAL;
			increment = (deltaX < 0) ? 1 : -1;
			end = positionTo.getX();
		}
	}

	public boolean hasNext() {
		if (direction == PositionIteratorDirection.HORIZONTAL) {
			return (positionIndex.getX() != end);
		} else {
			return (positionIndex.getY() != end);
		}
   }

	public Position next() {
		if (hasNext()) {
			if (direction == PositionIteratorDirection.HORIZONTAL) {
				positionIndex.setX(positionIndex.getX() + increment);
			} else {
				positionIndex.setY(positionIndex.getY() + increment);
			}
			return positionIndex;
		}

		throw new IndexOutOfBoundsException("No more items...");
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove unsupported");
	}
	
	private enum PositionIteratorDirection {
		HORIZONTAL, VERTICAL
	}
}
