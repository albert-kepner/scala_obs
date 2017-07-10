package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTestGrid extends FunSuite with Checkers {

  //  test("generateTilesFromGridForYear4 1975") {
  //    Manipulation.generateTilesFromGridForYear4(1975)
  //  }

  //  test("temperature test with grid sparse by 4 degrees") {
  //    Manipulation.generateTilesForAverageYearRange(1975, 1989)
  //  }

  test("deviations from 1990 to 2000") {
    for (year <- (1990 to 2000)) {
      Manipulation.generateTilesForDeviations(year, 1975, 1989)
    }
  }

}
