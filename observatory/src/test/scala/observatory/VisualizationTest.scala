package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class VisualizationTest extends FunSuite with Checkers {

  test("Interpolate Red/Blue (1)") {
    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, red)
    val p2: (Double, Color) = (20, blue)
    val p3: (Double, Color) = (30, green)
    val ks = List(p1, p2)
    val ks2 = List(p1, p2, p3)

    val color15 = Visualization.interpolateColor(ks, 15)
    val color17 = Visualization.interpolateColorPair(p1, p2, 17)
    val color19 = Visualization.interpolateColorPair(p1, p2, 19)
    val color21 = Visualization.interpolateColor(ks, 21)
    val color5 = Visualization.interpolateColor(ks, 5)
    val color25 = Visualization.interpolateColor(ks2, 25)
    val color12 = Visualization.interpolateColor(ks2, 12)
    println(s"color15 = $color15")
    println(s"color17 = $color17")
    println(s"color19 = $color19")
    println(s"color21 = $color21")
    println(s"color5 = $color5")
    println(s"color25 = $color25")
    println(s"color12 = $color12")
  }
  /**
   * Incorrect predicted color: Color(0,0,255). 
   * Expected: Color(128,0,128) 
   * (scale = List((0.0,Color(255,0,0)), (2.147483647E9,Color(0,0,255))), 
   * value = 1.0737418235E9)
   */
  test("fix bad color") {
    val scale = List((0.0,Color(255,0,0)), (2.147483647E9,Color(0,0,255)))
    val value = 1.0737418235E9
    val color = Visualization.interpolateColor(scale, value)
    println(s"bad prediction color = $color")
    val test: Boolean = (color == Color(128,0,128) )
    assert(test, "bad predicted color")
  }
  /**
   * Incorrect predicted color: Color(0,0,0). 
   * Expected: Color(255,0,0) 
   * (scale = List((-76.91204092535045,Color(255,0,0)), (1.0,Color(0,0,255))), 
   * value = -76.91204092535045)
   */
  test("fix bad color 2") {
    val scale = List((-76.91204092535045,Color(255,0,0)), (1.0,Color(0,0,255)))
    val value = -76.91204092535045
    val color = Visualization.interpolateColor(scale, value)
    println(s"bad prediction color = $color")
    val test: Boolean = (color == Color(255,0,0) )
    assert(test, "bad predicted color 2")
  }

}
