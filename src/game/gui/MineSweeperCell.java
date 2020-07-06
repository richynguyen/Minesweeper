package game.gui;

import javax.swing.*;

public class MineSweeperCell extends JButton {
  public final int row;
  public final int column;

    public MineSweeperCell(int theRow, int theColumn) {
      row = theRow;
      column = theColumn;
      setSize(50, 50);
  }
}
