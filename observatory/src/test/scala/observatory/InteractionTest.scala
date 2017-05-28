package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTest extends FunSuite with Checkers {

  //  test("tile at 1,1,1") {
  //    val loc: Location = Interaction.tileLocation(1, 1, 1)
  //    println(s"location = $loc")
  //  }
  //  test("tile at 2,2,2") {
  //    val loc: Location = Interaction.tileLocation(2, 2, 2)
  //    println(s"location = $loc")
  //  }
  //  test("tile at zoom 3, 4,4") {
  //    val loc: Location = Interaction.tileLocation(3, 4, 4)
  //    println(s"location = $loc")
  //  }
  //
  test("tile at zoom 4, 8,8") {
    val loc: Location = Interaction.tileLocation(4, 8, 8)
    println(s"location = $loc")
  }

  test("tile at zoom 5,16,16") {
    val loc: Location = Interaction.tileLocation(5, 16, 16)
    println(s"location = $loc")
  }
  test("tile at zoom 5,17,17") {
    val loc: Location = Interaction.tileLocation(5, 17, 17)
    println(s"location = $loc")
  }

  test("tileSubtileLocations 1") {
    Interaction.tileSubtileLocations(5, 16, 16, 1)
    Interaction.tileSubtileLocations(5, 16, 16, 2)
    Interaction.tileSubtileLocations(5, 16, 16, 3)
    Interaction.tileSubtileLocations(5, 16, 16, 4)
    Interaction.tileSubtileLocations(5, 16, 16, 8)
  }

  test("tile Image 1st test") {
    val l1 = Location(0, -9)
    val l2 = Location(0, -7)
    val l3 = Location(5, -1)

    val t1 = (l1, 12.0)
    val t2 = (l2, 22.0)
    val t3 = (l3, 30.0)
    val temps = List(t1, t2, t3)

    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, blue)
    val p2: (Double, Color) = (20, green)
    val p3: (Double, Color) = (30, red)
    val colors = List(p1, p2, p3)

    val image = Interaction.tile(temps, colors, 5, 16, 16)
  }
  test("tile Image world tile 1") {
    val l1 = Location(45, -90)
    val l2 = Location(-45, -90)
    val l3 = Location(-45, 90)

    val t1 = (l1, 10.0)
    val t2 = (l2, 20.0)
    val t3 = (l3, 30.0)
    val temps = List(t1, t2, t3)

    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, blue)
    val p2: (Double, Color) = (20, green)
    val p3: (Double, Color) = (30, red)
    val colors = List(p1, p2, p3)

    val image = Interaction.tile(temps, colors, 0,0,0)
  }
  test("tileDebug") {
    val l1 = Location(45, -90)
    val l2 = Location(-45, -90)
    val l3 = Location(-45, 90)

    val t1 = (l1, 10.0)
    val t2 = (l2, 20.0)
    val t3 = (l3, 30.0)
    val temps = List(t1, t2, t3)

    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, blue)
    val p2: (Double, Color) = (20, green)
    val p3: (Double, Color) = (30, red)
    val colors = List(p1, p2, p3)

    val image = Interaction.tileDebug(temps, colors, 0,0,0)
  }

  test("tile Image world Zoom Level 0 to file") {
    val l1 = Location(45, -90)
    val l2 = Location(-45, -90)
    val l3 = Location(-45, 90)

    val t1 = (l1, 10.0)
    val t2 = (l2, 20.0)
    val t3 = (l3, 30.0)
    val temps = List(t1, t2, t3)

    val red = Color(255, 0, 0)
    val blue = Color(0, 0, 255)
    val green = Color(0, 255, 0)
    val p1: (Double, Color) = (10, blue)
    val p2: (Double, Color) = (20, green)
    val p3: (Double, Color) = (30, red)
    val colors = List(p1, p2, p3)
    type Data1 = (Int, Iterable[(Location, Double)])
    val data1: Data1 = (1900, temps)

    Interaction.generateImageWithColor(1900, 0,0, 0, data1, colors)
  }

}
