package snakepackage;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import enums.GridSize;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author jd-
 *
 */
public class SnakeApp {

    private static SnakeApp app;
    public static final int MAX_THREADS = 8;
    Snake[] snakes = new Snake[MAX_THREADS];
    private static final Cell[] spawn = {
        new Cell(1, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2,
        3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, GridSize.GRID_HEIGHT - 2),
        new Cell(1, 3 * (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell(GridSize.GRID_WIDTH - 2, (GridSize.GRID_HEIGHT / 2) / 2),
        new Cell((GridSize.GRID_WIDTH / 2) / 2, 1),
        new Cell(3 * (GridSize.GRID_WIDTH / 2) / 2,
        GridSize.GRID_HEIGHT - 2)};
    private JFrame frame;
    private JButton iniciar;
    private JButton reanudar;
    private JButton pausar;
    private int deadSnake = -1;
    private static Board board;
    int nr_selected = 0;
    Thread[] thread = new Thread[MAX_THREADS];

    public SnakeApp() {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("The Snake Race");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(618, 640);
        frame.setSize(GridSize.GRID_WIDTH * GridSize.WIDTH_BOX + 17,
                GridSize.GRID_HEIGHT * GridSize.HEIGH_BOX + 40);
        frame.setLocation(dimension.width / 2 - frame.getWidth() / 2,
                dimension.height / 2 - frame.getHeight() / 2);
        board = new Board();
        
        
        frame.add(board,BorderLayout.CENTER);
        
        JPanel actionsBPabel=new JPanel();
        actionsBPabel.setLayout(new FlowLayout());
        iniciar = new JButton("Iniciar");
        pausar = new JButton("Pausar");
        reanudar = new JButton("Reanudar");
        actionsBPabel.add(iniciar);
        actionsBPabel.add(pausar);
        actionsBPabel.add(reanudar);
        frame.add(actionsBPabel,BorderLayout.SOUTH);
        prepareActionButtons();
    }

    private void preparePanelPause(){
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame1 = new JFrame("Juego Pausado");
        frame1.setVisible(true);
        frame1.setSize(200, 100);
        frame1.setLocation(dimension.width / 2 - frame.getWidth() / 3,
                dimension.height / 2 - frame.getHeight() / 4);
        frame1.setLayout(new BorderLayout());
        JPanel snakesInfo=new JPanel();
        snakesInfo.setLayout(new FlowLayout());
        JLabel bigSnake = new JLabel("Serpiente mas larga: "+getBiggest());
        snakesInfo.add(bigSnake);
        JLabel worstSnake = new JLabel("Peor serpiente: "+getFirstSnake());
        snakesInfo.add(worstSnake);
        frame1.add(snakesInfo);
    }

    private void prepareActionButtons(){
        iniciar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                opcionReanudar();
                iniciar.setEnabled(false);
            }
        });

        pausar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                try {
                    opcionPausar();
                    preparePanelPause();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        reanudar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                opcionReanudar();
            }
        });
    }

    private void opcionPausar() throws InterruptedException {
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i].stopThread();
        }
    }

    private void opcionReanudar(){
        for (int i = 0; i != MAX_THREADS; i++) {
            snakes[i].restartThread();
        }
    }

    public static void main(String[] args) {
        app = new SnakeApp();
        app.init();
    }

    private void init() {
        
        
        
        for (int i = 0; i != MAX_THREADS; i++) {
            
            snakes[i] = new Snake(i + 1, spawn[i], i + 1);
            snakes[i].addObserver(board);
            thread[i] = new Thread(snakes[i]);
            thread[i].start();
        }

        frame.setVisible(true);

            
        while (true) {
            int x = 0;
            for (int i = 0; i != MAX_THREADS; i++) {
                if (snakes[i].isSnakeEnd() == true) {
                    if (deadSnake == -1){
                        deadSnake = i+1;
                    }
                    x++;
                }
            }
            if (x == MAX_THREADS) {
                break;
            }
        }


        System.out.println("Thread (snake) status:");
        for (int i = 0; i != MAX_THREADS; i++) {
            System.out.println("["+i+"] :"+thread[i].getState());
        }

    }

    public static SnakeApp getApp() {
        return app;
    }

    public int getBiggest(){
        int maxValue = 0;
        int position = 0;
        for (int i = 0; i != MAX_THREADS; i++) {
            if (snakes[i].getSnakeBody() > maxValue && !snakes[i].getSnakeEnd()){
                maxValue = snakes[i].getSnakeBody();
                position = i;
            }
        }
        return position+1;
    }

    public int getFirstSnake() {
        return deadSnake;
    }
}
