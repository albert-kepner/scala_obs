package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

@RunWith(classOf[JUnitRunner])
class ManipulationTestGrid2 extends FunSuite with Checkers {

  //  test("generateTilesFromGridForYear4 1975") {
  //    Manipulation.generateTilesFromGridForYear4(1975)
  //  }

  //  test("temperature test with grid sparse by 4 degrees") {
  //    Manipulation.generateTilesForAverageYearRange(1975, 1989)
  //  }

  test("deviations from 2001 to 2015") {

    Manipulation.generateTilesForDeviations(2001, 2015, 1975, 1989)

  }

}
