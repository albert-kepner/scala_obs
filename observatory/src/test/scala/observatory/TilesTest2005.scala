package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class TilesTest2005 extends FunSuite with Checkers {
    test("generateTilesForYear 2005 to 2015") {
    val startTime: Long = System.currentTimeMillis()
    for(year <- (2005 to 2015)) {
      Interaction.generateTilesForYear(year)
    }
    
    val endTime: Long = System.currentTimeMillis()
    val elapsed: Float = (endTime - startTime) / 1000.0f
  }

}
