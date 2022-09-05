import org.junit.*;
import static org.junit.Assert.*;
import javafx.scene.control.Button;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Dimension2D;

/**
 * Test class Tsuro
 * @author Ali Puccio
 */
public class TestTsuro {
  
  /**
   * Test the switchTurns method
   */
  @Test
  public void testSwitchTurns(){
   assertEquals(true, Tsuro.getPlayer1Turn());
   assertEquals(false, Tsuro.getPlayer2Turn());
   Tsuro.switchTurns();
   assertEquals(false, Tsuro.getPlayer1Turn());
   assertEquals(true, Tsuro.getPlayer2Turn());
   Tsuro.switchTurns();
   assertEquals(true, Tsuro.getPlayer1Turn());
   assertEquals(false, Tsuro.getPlayer2Turn());
  }
  
  /**
   * Test the rotate method
   */
  @Test
  public void testTestableRotate(){
    int[] array = {7, 6, 3, 2, 5, 4, 1, 0};
    int[] array2 = {2, 3, 0, 1, 5, 4, 7, 6};
    int[] array3 = {1, 0, 5, 4, 3, 2, 7, 6};
    int[] array4 = {1, 0, 3, 2, 6, 7, 4, 5};
    Assert.assertArrayEquals(array2, Tsuro.testableRotate(array));
    Assert.assertArrayEquals(array3, Tsuro.testableRotate(array2));
    Assert.assertArrayEquals(array4, Tsuro.testableRotate(array3));
  }
  
  /**
   * Test the getPlayerHandSize() method
   */
  @Test
  public void testGetPlayerHandSize(){
    Tsuro.setPlayerHandSize(4);
    assertEquals(4, Tsuro.getPlayerHandSize());
  }
  
  /**
   * Test the getBoardHeight() method
   */
  @Test
  public void testGetBoardHeight(){
    Tsuro.setBoardHeight(4);
    assertEquals(4, Tsuro.getBoardHeight());
  }
  
  /**
   * Test the getBoardWidth() method
   */
  @Test
  public void testGetBoardWidth(){
    Tsuro.setBoardWidth(4);
    assertEquals(4, Tsuro.getBoardWidth());
  }
    
}             
                 
    
    
  
 
