package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

//import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTestDebug extends FunSuite with Checkers {
  test("tile Image world Zoom Level 0 to file") {
    val l1 = Location(45, -90)
    val l2 = Location(-45, -90)
    val l3 = Location(-45, 90)

    val t1 = (l1, -60.0)
    val t2 = (l2, 0.0)
    val t3 = (l3, 60.0)
    val temps = List(t1, t2, t3)

    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, blue)
    val p2: (Double, Color) = (20, green)
    val p3: (Double, Color) = (30, red)
//    val colors = List(p1, p2, p3)
    val colors = ColorScale.cs
    type Data1 = (Int, Iterable[(Location, Double)])
    val data1: Data1 = (1900, temps)

    Interaction.generateImageWithColor(1900, 0,0, 0, data1, colors)
    Interaction.generateImageWithColor(1900, 1,0,0, data1, colors)
    Interaction.generateImageWithColor(1900, 1,0,1, data1, colors)
    Interaction.generateImageWithColor(1900, 1,1,0, data1, colors)
    Interaction.generateImageWithColor(1900, 1,1,1, data1, colors)
  }
}