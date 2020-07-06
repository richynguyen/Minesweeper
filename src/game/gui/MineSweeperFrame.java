package game.gui;

import game.CellState;
import game.GameStatus;
import game.MineSweeper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class MineSweeperFrame extends JFrame {
  private static final int SIZE = 10;
  private MineSweeper mineSweeper;
  private JPanel board;
  private JButton reset;
  private ArrayList<MineSweeperCell> cells;

  @Override
  protected void frameInit() {
    super.frameInit();

    setUI();
    placesMinesRandomly();
    pack();
    ImageIcon unexposed = new ImageIcon("Assign1/src/images/10.png");

    for (int i = 0; i < SIZE; i++) {
      for (int j = 0; j < SIZE; j++) {
        MineSweeperCell cell = new MineSweeperCell(i, j);
        board.add(cell);
        cells.add(cell);
        cell.setIcon(unexposed);

        cell.addMouseListener(new CellClickedHandler());
      }
    }

  }

  public static void main(String[] args) {
    JFrame frame = new MineSweeperFrame();
    frame.setSize(550, 550);
    frame.setVisible(true);
  }

  private void setUI() {
    ImageIcon sun = new ImageIcon("Assign1/src/images/15.png");
    board = new JPanel();
    JPanel menuBar = new JPanel();
    cells  = new ArrayList<MineSweeperCell>();
    reset = new JButton("");

    reset.setIcon(sun);
    reset.setPreferredSize(new Dimension(25, 25));
    reset.addActionListener(new newGameHandler());
    GridLayout temp = new GridLayout(SIZE, SIZE, -1, 0);
    board.setLayout(temp);
    board.setBackground(Color.darkGray);
    menuBar.add(reset);
    add(board, BorderLayout.CENTER);
    add(menuBar, BorderLayout.NORTH);

    setResizable(false);
    setTitle("Minesweeper");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  private void placesMinesRandomly() {
    Random random = new Random();
    mineSweeper = new MineSweeper();

    mineSweeper.setMines(random.nextInt());
  }

  private void newGame() {
    ImageIcon unexposed = new ImageIcon("Assign1/src/images/10.png");
    paintResetButton(15);
    for (int i = 0; i < 100; i++) {
      cells.get(i).setEnabled(true);
      cells.get(i).setIcon(unexposed);
      repaint();
    }
    placesMinesRandomly();
  }

  private class CellClickedHandler implements MouseListener {

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
      MineSweeperCell cell = (MineSweeperCell) mouseEvent.getSource();

      if (gameResults()) {
        if (mouseEvent.getButton() == 1) {
          leftClickAction(cell);
          gameResults();
        }
        else if (mouseEvent.getButton() == 3) {
          rightClickAction(cell);
          gameResults();
        }
      }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }
  }

  private class newGameHandler implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      newGame();
    }
  }

  private void leftClickAction(MineSweeperCell cell) {
    int state = mineSweeper.adjacentMinesCountAt(cell.row, cell.column);

    if (mineSweeper.getCellState(cell.row, cell.column) != CellState.SEALED) {
      if (mineSweeper.isMineAt(cell.row, cell.column)) {
        clickedABomb(cell);
      }
      else if (state == 0) {
        clickedAEmptyCell(cell, state);
        makeUnclickAble();
      }
      else {
        mineSweeper.exposeCell(cell.row, cell.column);
        paintComponent(cell, state);
        makeUnclickAble();
      }
    }
  }

  private void rightClickAction(MineSweeperCell cell) {
    int state = 0;
    mineSweeper.toggleSeal(cell.row, cell.column);

    if (mineSweeper.getCellState(cell.row, cell.column) != 
      CellState.EXPOSED) {
      state = 
        mineSweeper.getCellState(cell.row, cell.column) == CellState.SEALED ?
        9 : 10;
      paintComponent(cell, state);
      mineSweeper.getGameStatus();
    }
  }

  private void clickedABomb(MineSweeperCell cell) {
    int state = 12;
    mineSweeper.exposeCell(cell.row, cell.column);
    findAllBombs();
    paintComponent(cell, state);
    paintResetButton(14);
  }

  private void clickedAEmptyCell(MineSweeperCell cell, int state) {
    mineSweeper.exposeCell(cell.row, cell.column);
    findAllEmpty();
  }

  private void paintComponent(MineSweeperCell cell, int state) {
    ImageIcon currentState = new ImageIcon("Assign1/src/images/" + state + ".png");
    cell.setIcon(currentState);
    repaint();
  }

  private void paintResetButton(int state) {
    ImageIcon sun = new ImageIcon("Assign1/src/images/"+state+".png");
    reset.setIcon(sun);
    repaint();
  }

  private boolean gameResults() {
    if (mineSweeper.getGameStatus() != GameStatus.IN_PROGRESS) {
      int state = mineSweeper.getGameStatus() == GameStatus.WON ? 13 : 14;
      paintResetButton(state);
      return false;
    }
    return true;
  }

  private void findAllEmpty() {
    for (int i = 0; i < 100; i++) {
      if (mineSweeper.getCellState(cells.get(i).row, cells.get(i).column) ==
        CellState.EXPOSED) {
        paintComponent(cells.get(i), 
          mineSweeper.adjacentMinesCountAt(
            cells.get(i).row, cells.get(i).column));
        cells.get(i).setEnabled(false);
      }
    }
  }

  private void findAllBombs() {
    for (int i = 0; i < 100; i++) {
      if (mineSweeper.isMineAt(cells.get(i).row, cells.get(i).column)) {
        paintComponent(cells.get(i), 11);
      }
    }
  }

  private void makeUnclickAble() {
    for (int i = 0; i < 100; i++) {
      if(mineSweeper.getCellState(cells.get(i).row, cells.get(i).column) == CellState.EXPOSED && !mineSweeper.isMineAt(cells.get(i).row, cells.get(i).column)){
        Icon apply = cells.get(i).getIcon();
        cells.get(i).setDisabledIcon(apply);
        repaint();
        cells.get(i).setEnabled(false);
      }
    }
  }
}
