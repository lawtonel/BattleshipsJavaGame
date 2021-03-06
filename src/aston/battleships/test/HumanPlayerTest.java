package aston.battleships.test;

import aston.battleships.*;

public class HumanPlayerTest {
    private HumanPlayer player = new HumanPlayer(7, 7, 3);

    public static void main(String[] args) throws Player.ResignException {
        new HumanPlayerTest().testPlaceShip();
    }

    public void testPlaceShip() throws Player.ResignException {
        player.placeShipOnToPlayerBoard(3);
    }

    public void testChooseMove() {
        try {
            player.chooseMove();
        } catch(Player.QuitException ignored) {}
    }
}
