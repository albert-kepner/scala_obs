package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class TilesTest1991 extends FunSuite with Checkers {
    test("generateTilesForYear 1991 to 2004") {
    val startTime: Long = System.currentTimeMillis()
    for(year <- (1991 to 2004)) {
      Interaction.generateTilesForYear(year)
    }
    
    val endTime: Long = System.currentTimeMillis()
    val elapsed: Float = (endTime - startTime) / 1000.0f
  }

}
