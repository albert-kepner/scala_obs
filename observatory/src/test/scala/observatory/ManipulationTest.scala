package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTest extends FunSuite with Checkers {

  test("test avg next") {
    val l1 = Location(45, -90)
    val l2 = Location(-45, -90)
    val l3 = Location(-45, 90)

    val t1 = (l1, -60.0)
    val t2 = (l2, 0.0)
    val t3 = (l3, 60.0)
    val t4 = (l1, 120.0)
    val temps = List(t1, t2, t3)
    val temps2 = List(t2, t3, t4)

    val g: Grid = Manipulation.makeDataGrid(temps)
    val g2: Grid = Manipulation.makeDataGrid(temps2)

//    for (
//      lat <- (Grid.maxLat to Grid.minLat by -15);
//      lon <- (Grid.minLon to Grid.maxLon by 15)
//    ) {
//      println(s"lat = $lat, lon = $lon, g = ${g.get(lat, lon)}")
//    }

    val tempsList = List(temps, temps2)
    val avg = Manipulation.average(tempsList)

    for (
      lat <- (Grid.maxLat to Grid.minLat by -15);
      lon <- (Grid.minLon to Grid.maxLon by 15)
    ) {
      println(s"lat = $lat, lon = $lon, g = ${g.get(lat, lon)}")
      println(s"lat = $lat, lon = $lon, g2 = ${g2.get(lat, lon)}")
      println(s"lat = $lat, lon = $lon, avg = ${avg(lat, lon)}")
    }

  }
}