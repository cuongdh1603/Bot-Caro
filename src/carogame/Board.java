/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package carogame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 *
 * @author Cuong
 */
public class Board extends JPanel {

    private static final int SIDE = 3;
    private EndGame endGame;
    private Image imgX;
    private Image imgO;
    private Cell grid[][] = new Cell[SIDE][SIDE];
    private String currentPlayer = Cell.EMPTY_VALUE;
    private static int moveIndex, x, y;
    private static char board[][] = new char[SIDE][SIDE];
    private static char HUMANMOVE = 'O';
    private static char COMPUTERMOVE = 'X';

    public Board(String player) {
        this();
        this.currentPlayer = player;
    }

    public Board() {
        this.initGrids();
        //-------AI-------------------
        moveIndex = 0;
        x = 0;
        y = 0;
        initialise();
        //----------------------------*/
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e); //To change body of generated methods, choose Tools | Templates.
                int clickX = e.getX();
                int clickY = e.getY();
                if (currentPlayer.equals(Cell.EMPTY_VALUE)) {
                    return;
                }
                soundClick();
                for (int i = 0; i < SIDE; i++) {
                    for (int j = 0; j < SIDE; j++) {
                        Cell c = grid[i][j];
                        if (clickX >= c.getX() && clickX < c.getX() + 100 && clickY >= c.getY() && clickY < c.getY() + 100) {
                            if (c.getValue().equals(Cell.EMPTY_VALUE)) {
                                System.out.println("Row: " + j + " Col: " + i + " Width: " + j * c.getW() + " Height: " + i * c.getH());
                                board[j][i] = HUMANMOVE;
                                moveIndex++;
                                c.setValue(Cell.O_VALUE);
                                printBoard();
                                if(moveIndex < SIDE * SIDE){
                                    int n = bestMove();
                                    x = n / SIDE;
                                    y = n % SIDE;
                                    board[x][y] = COMPUTERMOVE;
                                    Cell c1 = grid[y][x];
                                    c1.setValue(Cell.X_VALUE);
                                    moveIndex++;
                                    printBoard();
                                }
                                
                                repaint();
                                if (!gameOver() && moveIndex == SIDE * SIDE) {
                                    System.out.println("It's draw");
                                    endGame.end("DRAW");
                                } else if (gameOver()) {
                                    if (currentPlayer.equals(Cell.O_VALUE)) {
                                        System.out.println("Computer win");
                                    } else {
                                        System.out.println("Human win");
                                    }
                                    endGame.end(currentPlayer);
                                }
                            }
                        }
                    }
                }
            }
        });
        try {
            imgX = ImageIO.read(getClass().getResource("x.png"));
            imgO = ImageIO.read(getClass().getResource("o.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private synchronized void soundClick() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("Mousclik.wav"));
                    clip.open(audioInputStream);
                    clip.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void initGrids() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                Cell c = new Cell();
                grid[i][j] = c;
            }
        }
    }

    public void reset() {
        this.initGrids();
        moveIndex = 0;
        initialise();
        this.setCurrentPlayer(currentPlayer);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        int width = getWidth() / 3;
        int height = getHeight() / 3;
        Graphics2D graphics2D = (Graphics2D) g;
        int k = 0;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                int x = i * width;
                int y = j * height;
                Cell c = grid[i][j];
                c.setX(x);
                c.setY(y);
                c.setW(width);
                c.setH(height);
                Color color = k % 2 == 0 ? Color.BLUE : Color.GREEN;
                graphics2D.setColor(color);
                graphics2D.fillRect(x, y, width, height);
                if (c.getValue().equals(Cell.X_VALUE)) {
                    Image img = imgX;
                    graphics2D.drawImage(img, x, y, width, height, this);
                } else if (c.getValue().equals(Cell.O_VALUE)) {
                    Image img = imgO;
                    graphics2D.drawImage(img, x, y, width, height, this);
                }
                k++;
            }
        }
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setEndGame(EndGame endGame) {
        this.endGame = endGame;
    }

    public void setFirstTurn(int firstTurn) {
        if(firstTurn==1){
            int n = bestMove();
            x = n / SIDE;
            y = n % SIDE;
            board[x][y] = COMPUTERMOVE;
            Cell c1 = grid[y][x];
            c1.setValue(Cell.X_VALUE);
            moveIndex++;
            printBoard();
            repaint();
        }
    }

    private static void initialise() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                board[i][j] = ' ';
            }
        }
    }

    //----------AI Algorithms-----------
    private static int minimax(int depth, boolean isAI) {
        int score = 0, bestScore = 0;
        if (gameOver()) {
            if (isAI) return -1;
             else return 1;
        } else {
            if (depth < 9) {
                if (isAI) {
                    bestScore = Integer.MIN_VALUE;
                    for (int i = 0; i < SIDE; i++) {
                        for (int j = 0; j < SIDE; j++) {
                            if (board[i][j] == ' ') {
                                board[i][j] = COMPUTERMOVE;
                                score = minimax(depth + 1, false);
                                board[i][j] = ' ';
                                if (score > bestScore) {
                                    bestScore = score;
                                }
                            }
                        }
                    }
                    return bestScore;
                }
                else {
                    bestScore = Integer.MAX_VALUE;
                    for (int i = 0; i < SIDE; i++) {
                        for (int j = 0; j < SIDE; j++) {
                            if (board[i][j] == ' ') {
                                board[i][j] = HUMANMOVE;
                                score = minimax(depth + 1, true);
                                board[i][j] = ' ';
                                if (score < bestScore) {
                                    bestScore = score;
                                }
                            }
                        }
                    }
                    return bestScore;
                }
            } 
            else{
                return 0;
            }
        }
    }

    private static int bestMove() {
        int x = -1, y = -1;
        int score = 0, bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = COMPUTERMOVE;
                    score = minimax(moveIndex + 1, false);
                    board[i][j] = ' ';
                    if (score > bestScore) {
                        bestScore = score;
                        x = i;
                        y = j;
                    }
                }
            }
        }
        System.out.println(x*3+y);
        return x * 3 + y;
    }

    //--------------------------------*/
    private static boolean rowCrossed() {
        for (int i = 0; i < SIDE; i++) {
            if (board[i][0] == board[i][1]
                    && board[i][1] == board[i][2]
                    && board[i][0] != ' ') {
                return true;
            }
        }
        return false;
    }

    private static boolean columnCrossed() {
        for (int i = 0; i < SIDE; i++) {
            if (board[0][i] == board[1][i]
                    && board[1][i] == board[2][i]
                    && board[0][i] != ' ') {
                return true;
            }
        }
        return false;
    }

    private static boolean diagonalCrossed() {
        if (board[0][0] == board[1][1]
                && board[1][1] == board[2][2]
                && board[0][0] != ' ') {
            return true;
        }
        if (board[0][2] == board[1][1]
                && board[1][1] == board[2][0]
                && board[0][2] != ' ') {
            return true;
        }
        return false;
    }

    private static boolean gameOver() {
        return (rowCrossed() || columnCrossed() || diagonalCrossed());
    }
    private static void printBoard(){
        for (int k = 0; k < SIDE; k++) {
            for (int h = 0; h < SIDE; h++) {
                if(board[k][h] != ' ')
                    System.out.print(board[k][h] + " ");
                else
                    System.out.print("~ ");
            }
            System.out.println("");
        }
    }
}
