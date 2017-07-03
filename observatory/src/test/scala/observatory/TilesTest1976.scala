package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class TilesTest1976 extends FunSuite with Checkers {
    test("generateTilesForYear 1976 to 1990") {
    val startTime: Long = System.currentTimeMillis()
    for(year <- (1976 to 1990)) {
      Interaction.generateTilesForYear(year)
    }
    
    val endTime: Long = System.currentTimeMillis()
    val elapsed: Float = (endTime - startTime) / 1000.0f
  }

}
