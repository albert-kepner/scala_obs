package observatory

import com.sksamuel.scrimage.{ Image, Pixel }

/**
 * 2nd milestone: basic visualization
 */
object Visualization {

  /**
   * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
   * @param location Location where to predict the temperature
   * @return The predicted temperature at `location`
   */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location): Double = {
    ???
  }
  type ColorPair= (Double, Color)
  /**
   * @param points Pairs containing a value and its associated color
   * @param value The value to interpolate
   * @return The color that corresponds to `value`, according to the color scale defined by `points`
   */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    var minTemp:Double = Long.MaxValue
    var maxTempBelow:Double = Long.MinValue
    var minTempAbove:Double = Long.MaxValue
    var maxTemp:Double = Long.MinValue

    var minColor: Option[ColorPair] = None
    var maxColorBelow: Option[ColorPair] = None
    var minColorAbove: Option[ColorPair] = None
    var maxColor: Option[ColorPair] = None

    for ((temp, color) <- points) {
      if (temp < minTemp) {
        minTemp = temp
        minColor = Some((temp,color))
      }
      if (temp > maxTemp) {
        maxTemp = temp
        maxColor = Some((temp,color))
      }
      if (temp >= maxTempBelow && temp <= value) {
        maxTempBelow = temp
        maxColorBelow = Some((temp,color))
      }
      if (temp <= minTempAbove && temp >= value) {
        minTempAbove = temp
        minColorAbove = Some((temp,color))
      }
    }
    value match {
      case _ if(minColorAbove == None) => maxColor.get._2 // value above max known temp
      case _ if(maxColorBelow == None) => minColor.get._2 // value below min known temp
      case _ if(maxColorBelow == minColorAbove) => maxColorBelow.get._2
      case _ => interpolateColorPair(maxColorBelow.get, minColorAbove.get, value)
    }
  }
  
  def interpolateColorPair(a: ColorPair, b: ColorPair, x: Double) : Color = {
    val t: Double = (x - a._1) / (b._1 - a._1)
    val ac:Color = a._2
    val bc:Color = b._2
    Color(lerpInt(ac.red, bc.red, t), lerpInt(ac.green, bc.green, t), lerpInt(ac.blue, bc.blue, t) )
  }
  // From: https://en.wikipedia.org/wiki/Linear_interpolation
  def lerp(v0: Double, v1: Double, t: Double) : Double = {
    (1.0 - t) * v0 + t * v1
  }
  def lerpInt(v0: Int, v1: Int, t: Double) : Int = {
    ((1.0 - t) * v0 + t * v1).round.toInt
  }

  /**
   * @param temperatures Known temperatures
   * @param colors Color scale
   * @return A 360×180 image where each pixel shows the predicted temperature at its location
   */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {
    ???
  }

}

