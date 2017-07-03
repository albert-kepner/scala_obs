package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class Visualization2Test extends FunSuite with Checkers {
  
  val colors = List( (0.0d, Color(0,0,0)), (1.0d, Color(255,0,0) ) )
  
  def myGrid (x: Int, y: Int) : Double = 0.6d
  
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

  test("interp (0.9,0.9)") {
    val x = 0.9
    val y = 0.9
    val d00 = 0d
    val d10 = 2.0
    val d01 = 10.0
    val d11 = 12.0
    val interp = Visualization2.bilinearInterpolation(x, y, d00, d01, d10, d11)
    println(s"interp (0.9,0.9) = $interp")
  }


}
