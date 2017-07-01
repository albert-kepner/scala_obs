package observatory

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.Checkers

import scala.collection.concurrent.TrieMap

@RunWith(classOf[JUnitRunner])
class InteractionTilesTest extends FunSuite with Checkers {
    test("generateTilesForYear 1975") {
    val startTime: Long = System.currentTimeMillis()
    Interaction.generateTilesForYear(1975)
    val endTime: Long = System.currentTimeMillis()
    val elapsed: Float = (endTime - startTime) / 1000.0f
  }

}
