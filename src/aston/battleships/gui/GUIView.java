package aston.battleships.gui;

import aston.battleships.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by cooperwd on 21/09/2017.
 */
public class GUIView extends JFrame implements View {
    private static final String INSTRUCTIONS =
            "- Before the game starts you will be asked to place your battleships\n" +
            "- You can click on a square on the board to place a ship\n" +
            "- When the game starts you can select a position on the enemy board to fire on\n" +
            "- You cannot click to fire on the same place twice\n" +
            "- The game is only over when you or your enemy have no battleships left\n" +
            "- You can also resign from the game by closing the window\n" +
            "~ GOOD LUCK PLAYER ~";

    private WindowState state;

    Coordinates startingPosition;
    Orientation orientation;
    Coordinates move;
    boolean isClosed;

    private static final int CELL_SIZE = 30;
    private static final int PADDING = 10;
    private static final int ACTION_BOX_AND_TITLE_HEIGHT = CELL_SIZE*2;
    private static final int INSTRUCTIONS_HEIGHT = 200;

    private final int boardWidth;
    private final int boardHeight;

    private final Panel panel;


    public GUIView(int boardWidth, int boardHeight) {
        state = WindowState.NOT_WAITING;
        startingPosition = null;
        orientation = null;
        move = null;
        isClosed = false;

        this.setSize(
            (PADDING * 3) + (CELL_SIZE * boardWidth) * 2 + CELL_SIZE * 2,
            PADDING * 3 + CELL_SIZE * boardHeight + ACTION_BOX_AND_TITLE_HEIGHT * 2 + INSTRUCTIONS_HEIGHT + CELL_SIZE
        );
        this.setVisible(true);
        this.setResizable(false);

        addMouseListener(new MyMouseListener());
        addWindowListener(new MyWindowAdapter());

        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;

        panel = new Panel();
        this.setContentPane(panel);
    }

    private void sleep() throws Player.ResignException {
        try {
            new Thread().sleep(10);
        } catch(InterruptedException ignored) {}
        if(isClosed) {
            throw new Player.ResignException();
        }
    }

    void waitForShipStartPosition() throws Player.ResignException {
        state = WindowState.WAITING_FOR_STARTING_POSITION;
        startingPosition = null;
        while(startingPosition == null) {
            sleep();
        }
    }
    void waitForShipOrientation() throws Player.ResignException {
        state = WindowState.WAITING_FOR_ORIENTATION;
        orientation = null;
        while(orientation == null) {
            sleep();
        }
    }
    void waitForMove() throws Player.ResignException {
        state = WindowState.WAITING_FOR_MOVE;
        move = null;
        while(move == null) {
            sleep();
        }
    }

    @Override
    public void viewBoards(PlayerBoard playerBoard, EnemyBoard enemyBoard) {

    }

    private void writeToAction(String action) {
        String s = action;
        int x = PADDING * 2 + CELL_SIZE * 3
                + CELL_SIZE * HEIGHT;
        int y = PADDING + CELL_SIZE;
        Graphics g = getGraphics();
        g.drawRect (PADDING * 2 + CELL_SIZE * 3
                + CELL_SIZE * HEIGHT, PADDING + CELL_SIZE, 300, 20);
        g.drawString(action, PADDING * 2 + CELL_SIZE * 3
                + CELL_SIZE * HEIGHT, PADDING + CELL_SIZE);
    }

    // x and y are in pixel space
    private void updateCell(int x, int y, CellState cellState) {

    }

    @Override
    public void welcomeUser() {
        Graphics g = panel.getGraphics();
        g.setColor(Color.BLACK);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 40));
        g.drawString("BATTLE SHIPS", PADDING, 40);

        writeToAction("WELCOME TO BATTLESHIPS");
    }

    @Override
    public void viewInstructions() {
        Graphics g = panel.getGraphics();
        g.setColor(Color.BLACK);
        String[] instructionsArray = INSTRUCTIONS.split("\n");
        int sentenceHeight = 20;
        int sentenceY = 0;
        for(String instruction : instructionsArray) {
            g.drawString(
                instruction,
                PADDING,
                PADDING * 2 + CELL_SIZE * boardHeight + ACTION_BOX_AND_TITLE_HEIGHT * 2 + CELL_SIZE + sentenceY
            );
            sentenceY += sentenceHeight;
        }
    }

    @Override
    public void announceGameOver(GameOverMessage message) {
        writeToAction(message.toString());
    }

    @Override
    public void viewResultOfMove(Coordinates coordinates, CellState cellState) {
        updateCell(
            coordinatesToXPlayerBoard(coordinates),
            coordinatesToY(coordinates),
            cellState
        );
        switch(cellState){
            case MISS:
                break;
            case SHIP_HIT:
                break;
            case SHIP_NOT_HIT:
                break;
            case SHIP_SUNK:
                break;
        }
    }

    @Override
    public void viewResultOfEnemyMove(Coordinates coordinates, CellState cellState) {
        updateCell(
            coordinatesToXEnemyBoard(coordinates),
            coordinatesToY(coordinates),
            cellState
        );
        switch(cellState){
            case MISS:
                break;
            case SHIP_HIT:
                break;
            case SHIP_NOT_HIT:
                break;
            case SHIP_SUNK:
                break;
        }    }

    @Override
    public void viewShipsLeftToPlace(List<Integer> shipLengths) {
        if(shipLengths.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Ships left to place\n");
            for (int shipLen : shipLengths) {
                sb.append("Ship of length -");
                for (int i = 0; i < shipLen; i++) {
                    sb.append(" #");
                }
                sb.append("\n");
            }
            writeToAction(sb.toString());
        } else {
            writeToAction("There are no more ships left to place.");
        }
    }

    private boolean withinPlayerBoard(int x, int y) {
        x -= PADDING + CELL_SIZE;
        y -= PADDING + ACTION_BOX_AND_TITLE_HEIGHT + CELL_SIZE;
        return x >= 0 && x < CELL_SIZE * boardWidth && y >= 0 && y < CELL_SIZE * boardHeight;
    }

    private boolean withinEnemyBoard(int x, int y){
        x -= PADDING*2 + CELL_SIZE*2 + CELL_SIZE*boardWidth;
        y -= PADDING + ACTION_BOX_AND_TITLE_HEIGHT + CELL_SIZE;
        return x >= 0 && x < CELL_SIZE * boardWidth && y >= 0 && y < CELL_SIZE * boardHeight;
    }

    private Coordinates pixelToCoordinatesPlayer(int x, int y) {
        if (withinPlayerBoard(x, y)) {
            x = (x - PADDING - CELL_SIZE) / CELL_SIZE;
            y = (y - PADDING - ACTION_BOX_AND_TITLE_HEIGHT - CELL_SIZE) / CELL_SIZE;
            return new Coordinates(x, y);
        } else {
            return null;
        }
    }
    private Coordinates pixelToCoordinatesEnemy(int x, int y) {
        if(withinEnemyBoard(x,y)) {
            x = (x - PADDING*2 - CELL_SIZE*2 - CELL_SIZE*boardWidth) / CELL_SIZE;
            y = (y - PADDING - ACTION_BOX_AND_TITLE_HEIGHT - CELL_SIZE) / CELL_SIZE;
            return new Coordinates(x, y);
        } else {
            return null;
        }
    }

    private int coordinatesToXPlayerBoard(Coordinates coordinates) {
        return PADDING + CELL_SIZE + (coordinates.x*CELL_SIZE);
    }

    private int coordinatesToXEnemyBoard(Coordinates coordinates) {
        return (PADDING*2) + (CELL_SIZE*2) + (CELL_SIZE*boardWidth) + (coordinates.x*CELL_SIZE);
    }

    private int coordinatesToY(Coordinates coordinates) {
        return PADDING + (CELL_SIZE*3) + (coordinates.y*CELL_SIZE);
    }

    public class MyMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            switch(state) {
                case NOT_WAITING:
                    break;
                case WAITING_FOR_STARTING_POSITION:
                    startingPosition = pixelToCoordinatesPlayer(e.getX(),e.getY());
                    break;
                case WAITING_FOR_ORIENTATION:
                    //TODO BUTTON SELECT
                    break;
                case WAITING_FOR_MOVE:
                    move = pixelToCoordinatesEnemy(e.getX(),e.getY());
                    break;
            }
            state = WindowState.NOT_WAITING;
        }
    }
    class MyWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent we) {
            System.exit(0);
        }
    }
}
