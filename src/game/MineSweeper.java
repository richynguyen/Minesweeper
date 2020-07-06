package game;

import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.IntStream;

public class MineSweeper {
  private static int MAX_BOUNDS = 10;
  private CellState[][] cellStates = new CellState[MAX_BOUNDS][MAX_BOUNDS];
  private boolean[][] isMined = new boolean[MAX_BOUNDS][MAX_BOUNDS];

  public MineSweeper() {
    for (int i = 0; i < MAX_BOUNDS; i++) {
      for (int j = 0; j < MAX_BOUNDS; j++) {
        cellStates[i][j] = CellState.UNEXPOSED;
        isMined[i][j] = false;
      }
    }
  }

  public void exposeCell(int row, int column) {
    if (cellStates[row][column] == CellState.UNEXPOSED) {
      cellStates[row][column] = CellState.EXPOSED;

      if (adjacentMinesCountAt(row, column) == 0)
        exposeNeighbors(row, column);
    }
  }

  public CellState getCellState(int row, int column) {
    return cellStates[row][column];
  }

  public void toggleSeal(int row, int column) {
    if (cellStates[row][column] != CellState.EXPOSED) {
      cellStates[row][column] =
        cellStates[row][column] == CellState.SEALED ?
          CellState.UNEXPOSED : CellState.SEALED;
    }
  }

  protected void exposeNeighbors(int row, int column) {
    for (int numRow = row - 1; numRow <= row + 1; numRow++) {
      for (int numCol = column - 1; numCol <= column + 1; numCol++) {
        if (checkBounds(numRow, numCol)) {
          exposeCell(numRow, numCol);
        }
      }
    }
  }

  private boolean checkBounds(int numRow, int numCol) {
    return numRow >= 0 && numCol >= 0 &&
      numRow < MAX_BOUNDS && numCol < MAX_BOUNDS;
  }

  public void setMine(int row, int column) {
    isMined[row][column] = true;
  }

  public int adjacentMinesCountAt(int row, int column) {
    if(isMineAt(row, column)) {
      return 0;
    }

    return IntStream.rangeClosed(row - 1, row + 1).flatMap(i ->
      IntStream.rangeClosed(column - 1, column + 1)
        .filter(j -> isMineAt(i, j))
        .map(j -> 1))
      .sum();
  }

  public boolean isMineAt(int row, int column) {
    return checkBounds(row, column) && isMined[row][column];
  }

  public GameStatus getGameStatus() {
    if(IntStream.range(0, MAX_BOUNDS)
      .filter(i ->
        IntStream.range(0, MAX_BOUNDS)
          .filter(j ->
            isMineAt(i, j) && cellStates[i][j] == CellState.EXPOSED)
          .count() > 0)
      .count() > 0) {
        return GameStatus.LOST;
    }    

    BiPredicate<Integer, Integer> mineSealOrUnminedExposed =
      (i, j) -> isMineAt(i, j) && cellStates[i][j] == CellState.SEALED ||
        !isMineAt(i, j) && cellStates[i][j] == CellState.EXPOSED;

    if(IntStream.range(0, MAX_BOUNDS)
      .filter(i -> IntStream.range(0, MAX_BOUNDS)
        .filter(j -> !mineSealOrUnminedExposed.test(i, j))
        .count() > 0)
      .count() > 0) {
      return GameStatus.IN_PROGRESS;    
    }
    
    return GameStatus.WON;
  }

  public void setMines(int seed) {
    int minesPlaced = 0;
    Random random = new Random(seed);
    while (minesPlaced < 10) {
      int row = random.nextInt(MAX_BOUNDS);
      int column = random.nextInt(MAX_BOUNDS);
      setMine(row, column);
      minesPlaced++;
    }
  }

}
