/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package carogame;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Cuong
 */
public class Main {
    private static int sec = 0;
    private static Timer timer = new Timer();
    private static JLabel lblTime;
    private static JButton btnStart;
    private static Board board;
    public static void main(String[] args) {
        board = new Board();
        board.setEndGame(new EndGame() {
            @Override
            public void end(String player) {
                if(player.equals("DRAW")){
                    JOptionPane.showMessageDialog(null, "It's draw");
                }
                else{
                    if(player.equals(Cell.O_VALUE))
                        JOptionPane.showMessageDialog(null, "Computer win");
                    else
                        JOptionPane.showMessageDialog(null, "Human win");
                }
                stopGame();
            }
        });
        JPanel jPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(jPanel,BoxLayout.Y_AXIS);
        jPanel.setLayout(boxLayout);
        
        board.setPreferredSize(new Dimension(300,300));
        
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER,20,0);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(flowLayout);
        
        btnStart = new JButton("Start");
        
        lblTime = new JLabel("00:00");
        bottomPanel.add(lblTime);
        bottomPanel.add(btnStart);
        btnStart.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(btnStart.getText().equals("Start")){
                    startGame();
                }
                else{
                    stopGame();
                }                
            }            
        });
        jPanel.add(board);
        jPanel.add(bottomPanel);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        
        JFrame jFrame = new JFrame("Caro Game 3x3");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.add(jPanel);
        int x = (int)dimension.getWidth()/2-jFrame.getWidth()/2;
        int y = (int)dimension.getHeight()/2-jFrame.getHeight()/2;
        jFrame.setResizable(true);
        jFrame.setLocation(x,y);
        jFrame.pack();
        jFrame.setVisible(true);
    }
    private static void startGame(){
        int choice = JOptionPane.showConfirmDialog(null, "Do you want to start first?", "Ai là người đi trước?", JOptionPane.YES_NO_OPTION);
        System.out.println("Choice: "+choice);
        String currentPlayer = Cell.O_VALUE;
        board.reset();
        board.setCurrentPlayer(currentPlayer);
        board.setFirstTurn(choice);
        sec = 0;
        lblTime.setText("00:00");
        timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                sec ++;
                String value = getFormatTime(sec/60, sec%60);
                lblTime.setText(value);
            }
        }, 1000, 1000);
        btnStart.setText("Stop");
    }
    private static void stopGame(){
        btnStart.setText("Start");
        sec = 0;
        lblTime.setText("00:00");
        timer.cancel();
        timer = new Timer();
    }
    public static String getFormatTime(int minute,int sec){
        return String.format("%02d", minute) + ":" + String.format("%02d", sec);
    }
}
