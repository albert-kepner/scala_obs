package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class Visualization2Test extends FunSuite with Checkers {
  
  val colors = List( (0.0d, Color(0,0,255)), (15.0d, Color(255,0,0) ) )
  
  def myGrid (x: Int, y: Int) : Double = 0.6d
  
  def myGridDetail(x: Int, y: Int) : Double = {
    // f(x,y) = 5 x + 10 y // 
    5.0 * x  + 10.0 * y
  }
  
  test("visualizeGrid try 1") {
    Visualization2.visualizeGrid(myGrid, colors, 0, 1, 1)
  }
  
  test("interp (0.1,0.1)") {
    val x = 0.1
    val y = 0.1
    val d00 = 0d
    val d10 = 2.0
    val d01 = 10.0
    val d11 = 12.0
    val interp = Visualization2.bilinearInterpolation(x, y, d00, d01, d10, d11)
    println(s"interp (0.1,0.1) = $interp")
  }

  test("interp ( 8.1, -50, 12.6, -1 at 0,0)") {
    val x = 0.0
    val y = 0.0
    val d00 = 8.1
    val d01 = -50
    val d10 = 12.6
    val d11 = -1.0
    val interp = Visualization2.bilinearInterpolation(x, y, d00, d01, d10, d11)
    println(s"interp ( 8.1, -50, 12.6, -1 at 0,0) = $interp")
  }
  
  test("visualizeGrid to file 01") {
    // visualizeGridToFile
    val file: String = "file01"
    Visualization2Grid.visualizeGridToFile(file, myGridDetail, colors, 0, 0, 0)
  }


}
