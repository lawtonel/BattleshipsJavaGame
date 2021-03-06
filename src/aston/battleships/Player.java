package aston.battleships;

public interface Player {
    public class QuitException extends Exception {}
    public class ResignException extends QuitException {}

    public void placeShipOnToPlayerBoard(int lengthOfShip) throws ResignException;
    public Coordinates chooseMove() throws QuitException;
    public CellState takeHit(Coordinates coordinates) throws QuitException;
    public boolean hasLost();
    public boolean hasAlreadyGuessed(Coordinates coordinates);
    public void viewState();
    public void updateEnemyBoard(Coordinates coordinates, CellState cellState);
    public View getView();
}
