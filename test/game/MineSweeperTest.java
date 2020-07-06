package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class MineSweeperTest {
  private MineSweeper mineSweeper;

  @BeforeEach
  void init() {
    mineSweeper = new MineSweeper();
  }

  @Test
  void canary() {
    assertTrue(true);
  }

  @Test
  void exposeAnUnExposedCell() {
    mineSweeper.exposeCell(1, 2);

    assertEquals(CellState.EXPOSED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void exposeAnExposedCell() {
    mineSweeper.exposeCell(1, 2);
    mineSweeper.exposeCell(1, 2);

    assertEquals(CellState.EXPOSED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void exposeACellOutOfRange() {
    assertAll(
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> mineSweeper.exposeCell(-1, 2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> mineSweeper.exposeCell(10, 2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> mineSweeper.exposeCell(2, -2)),
      () -> assertThrows(IndexOutOfBoundsException.class,
        () -> mineSweeper.exposeCell(2, 12))
    );
  }

  @Test
  void initialStateIsUnexposed() {
    assertEquals(CellState.UNEXPOSED, mineSweeper.getCellState(3, 2));
  }

  @Test
  void sealAnUnExposedCell() {
    mineSweeper.toggleSeal(1, 2);

    assertEquals(CellState.SEALED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void unsealASealedCell() {
    mineSweeper.toggleSeal(1, 2);
    mineSweeper.toggleSeal(1, 2);

    assertEquals(CellState.UNEXPOSED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void sealAnExposedCell() {
    mineSweeper.exposeCell(1, 2);
    mineSweeper.toggleSeal(1, 2);

    assertEquals(CellState.EXPOSED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void exposeAnSealedCell() {
    mineSweeper.toggleSeal(1, 2);
    mineSweeper.exposeCell(1, 2);

    assertEquals(CellState.SEALED, mineSweeper.getCellState(1, 2));
  }

  @Test
  void exposeCellCallsExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];
    MineSweeper mineSweeper = new MineSweeper() {
      protected void exposeNeighbors(int row, int column){
        exposeNeighborsCalled[0] = true;
      }
    };

    mineSweeper.exposeCell(2, 1);

    assertTrue(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeCellOnAnExposedCellDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    MineSweeper mineSweeper = new MineSweeper() {
      protected void exposeNeighbors(int row, int column){
        exposeNeighborsCalled[0] = true;
      }
    };

    mineSweeper.exposeCell(2, 1);
    exposeNeighborsCalled[0] = false;
    mineSweeper.exposeCell(2, 1);

    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeCellOnASealedCellDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    MineSweeper mineSweeper = new MineSweeper() {
      protected void exposeNeighbors(int row, int column){
        exposeNeighborsCalled[0] = true;
      }
    };
    mineSweeper.toggleSeal(2, 1);
    mineSweeper.exposeCell(2, 1);

    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void exposeNeighborsCallsExposeOnEightNeighbors() {
    var neighbors = new ArrayList<String>();

    MineSweeper mineSweeper = new MineSweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    mineSweeper.exposeNeighbors(4, 4);

    assertEquals(List.of("3-3", "3-4", "3-5", "4-3", "4-4", "4-5",
      "5-3", "5-4", "5-5"), neighbors);
  }

  @Test
  void exposeNeighborsCallsExposeOnTopLeft() {
    var neighbors = new ArrayList<String>();

    MineSweeper mineSweeper = new MineSweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    mineSweeper.exposeNeighbors(0, 0);

    assertEquals(List.of("0-0", "0-1", "1-0", "1-1"), neighbors);
  }

  @Test
  void exposeNeighborsCallsExposeOnBottomRight() {
    var neighbors = new ArrayList<String>();

    MineSweeper mineSweeper = new MineSweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    mineSweeper.exposeNeighbors(9, 9);

    assertEquals(List.of("8-8", "8-9", "9-8", "9-9"), neighbors);
  }

  @Test
  void exposeNeighborsCallsExposeOnBorderCell() {
    var neighbors = new ArrayList<String>();

    MineSweeper mineSweeper = new MineSweeper() {
      public void exposeCell(int row, int column) {
        neighbors.add(row + "-" + column);
      }
    };

    mineSweeper.exposeNeighbors(3, 0);

    assertEquals(List.of("2-0", "2-1", "3-0", "3-1", "4-0", "4-1"),
      neighbors);
  }

  @Test
  void noMineAtLocation(){
    assertFalse(mineSweeper.isMineAt(0, 0));
  }

  @Test
  void isAMineAtPosition() {
    mineSweeper.setMine(3, 2);

    assertTrue(mineSweeper.isMineAt(3, 2));
  }

  @Test
  void isMineAtboundsCheck() {
    assertAll(
      () -> assertFalse(mineSweeper.isMineAt(-1, 4)),
      () -> assertFalse(mineSweeper.isMineAt(10, 5)),
      () -> assertFalse(mineSweeper.isMineAt(5, -1)),
      () -> assertFalse(mineSweeper.isMineAt(7, 10))
    );
  }

  @Test
  void exposeAdjacentCellDoesNotCallExposeNeighbors() {
    boolean[] exposeNeighborsCalled = new boolean[1];

    MineSweeper minesweeper = new MineSweeper() {
      protected void exposeNeighbors(int row, int column) {
        exposeNeighborsCalled[0] = true;
      }
    };

    minesweeper.setMine(3, 5);

    minesweeper.exposeCell(3, 4);

    assertFalse(exposeNeighborsCalled[0]);
  }

  @Test
  void verifyAdjacentMinesIsZero() {
    assertEquals(0, mineSweeper.adjacentMinesCountAt(4, 6));
  }

  @Test
  void setMineAtALocationAndVerifyAdjacentMinesCountIsZero() {
    mineSweeper.setMine(3, 4);

    assertEquals(0, mineSweeper.adjacentMinesCountAt(3, 4));
  }

  @Test
  void setMineAtALocationAndVerifyAdjacentMinesCountIsOne() {
    mineSweeper.setMine(3, 4);

    assertEquals(1, mineSweeper.adjacentMinesCountAt(3, 5));
  }

  @Test
  void setTwoMinesAndVerifyAdjacentCountMinesCountIsTwo() {
    mineSweeper.setMine(3, 4);
    mineSweeper.setMine(2, 6);

    assertEquals(2, mineSweeper.adjacentMinesCountAt(3, 5));
  }

  @Test
  void setMineOnTopBorderAndVerifyAdjacentMinesCountIsOne() {
    mineSweeper.setMine(0, 1);

    assertEquals(1, mineSweeper.adjacentMinesCountAt(0, 0));
  }

  @Test
  void noMinesAtTopMineRightCellAndVerifyAdjacentMinesCountIsZero() {
    assertEquals(0, mineSweeper.adjacentMinesCountAt(0, 9));
  }

  @Test
  void setMineAtBottomBorderAndVerifyAdjacentMinesCountIsOne() {
    mineSweeper.setMine(9, 8);

    assertEquals(1, mineSweeper.adjacentMinesCountAt(9, 9));
  }

  @Test
  void noMinesAtBottomLeftCellAndVerifyAdjacentMinesCountIsZero() {
    assertEquals(0, mineSweeper.adjacentMinesCountAt(9, 0));
  }

  @Test
  void getGameStatusReturnsINPROGRESS() {
    assertEquals(GameStatus.IN_PROGRESS, mineSweeper.getGameStatus());
  }

  @Test
  void exposeAMinedCellAndGetGameStatusReturnsLOST(){
    mineSweeper.setMine(0, 0);
    mineSweeper.exposeCell(0, 0);

    assertEquals(GameStatus.LOST, mineSweeper.getGameStatus());
  }

  @Test
  void gameInProgressWhenAMineIsUnSealed(){
    mineSweeper.setMine(0, 0);

    assertEquals(GameStatus.IN_PROGRESS, mineSweeper.getGameStatus());
  }

  @Test
  void gameInProgressAfterAllMinesSealedButCellsRemainUnexposed(){
    mineSweeper.setMine(1, 2);
    mineSweeper.setMine(1, 3);

    mineSweeper.toggleSeal(1, 2);
    mineSweeper.toggleSeal(1, 3);

    assertEquals(GameStatus.IN_PROGRESS, mineSweeper.getGameStatus());
  }

  @Test
  void gameInProgressAfterAllMinesAreSealedButAnEmptyCellIsSealed(){
    mineSweeper.setMine(1, 2);
    mineSweeper.setMine(2, 1);

    mineSweeper.toggleSeal(1, 2);
    mineSweeper.toggleSeal(2, 1);

    mineSweeper.toggleSeal(1, 1);
    mineSweeper.exposeCell(3, 4);

    assertEquals(GameStatus.IN_PROGRESS, mineSweeper.getGameStatus());
  }

  @Test
  void gameInProgressAfterAllMinesSealedButAnAdjacentCellIsUnexposed(){
    mineSweeper.setMine(1, 2);
    mineSweeper.toggleSeal(1, 2);

    assertEquals(GameStatus.IN_PROGRESS, mineSweeper.getGameStatus());
  }

  @Test
  void gameWONAfterAllMinesAreSealedAndAllOtherCellsExposed(){
    mineSweeper.setMine(3,  3);
    mineSweeper.toggleSeal(3, 3);
    mineSweeper.exposeCell(6, 6);

    assertEquals(GameStatus.WON, mineSweeper.getGameStatus());
  }

  @Test
  void callSetMinesAndVerifyThereAreATotalOfTenMines(){
    mineSweeper.setMines(0);
    int count = 0;
    for (int i = 0; i < 10; i++){
      for (int j =0; j < 10; j++){
        if (mineSweeper.isMineAt(i, j))
          count++;
      }
    }

    assertEquals(10, count);
  }

  @Test
  void SetMinesAndCheckAtLeastOneMineHasADifferentLocationBetweenTwoInstances(){
    MineSweeper mineSweeper1 = new MineSweeper();
    MineSweeper mineSweeper2 = new MineSweeper();

    mineSweeper1.setMines(0);
    mineSweeper2.setMines(1);

    mineSweeper.setMines(0);
    mineSweeper.setMines(1);

    boolean differentBombLocation = false;
    for (int i = 0; i < 10; i++){
      for (int j = 0; j < 10; j++){
        if (mineSweeper1.isMineAt(i, j) && !mineSweeper2.isMineAt(i, j)){
          differentBombLocation = true;
        }
      }
    }

    assertTrue(differentBombLocation);
  }
}
