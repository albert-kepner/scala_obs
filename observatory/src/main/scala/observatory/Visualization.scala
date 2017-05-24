package observatory

import com.sksamuel.scrimage.{ Image, Pixel }
import scala.math._

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
  
  /**
   * convert angle in degrees (latitude or longitude) to radians.
   */
  def rad(degree: Double) : Double = {
    degree / 180.0 * Pi
  }
  def deg(rad: Double) : Double = {
    180.0 * rad / Pi
  }

  def greatCircleDistance(l1: Location, l2: Location): Double = {
    val lat1 = rad(l1.lat)
    val lon1 = rad(l1.lon)
    val lat2 = rad(l2.lat)
    val lon2 = rad(l2.lon)
    val deltaLon = abs(lon1 - lon2)    
    val x: Double = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(deltaLon)
    val angle = acos(x)
    val radiusKm:Double = 6371
    val distanceKm = radiusKm * angle
    distanceKm
  }
  type ColorPair = (Double, Color)
  /**
   * @param points Pairs containing a value and its associated color
   * @param value The value to interpolate
   * @return The color that corresponds to `value`, according to the color scale defined by `points`
   */
  def interpolateColor(points: Iterable[(Double, Color)], value: Double): Color = {
    var minTemp: Double = Long.MaxValue
    var maxTempBelow: Double = Long.MinValue
    var minTempAbove: Double = Long.MaxValue
    var maxTemp: Double = Long.MinValue

    var minColor: Option[ColorPair] = None
    var maxColorBelow: Option[ColorPair] = None
    var minColorAbove: Option[ColorPair] = None
    var maxColor: Option[ColorPair] = None

    for ((temp, color) <- points) {
      if (temp < minTemp) {
        minTemp = temp
        minColor = Some((temp, color))
      }
      if (temp > maxTemp) {
        maxTemp = temp
        maxColor = Some((temp, color))
      }
      if (temp >= maxTempBelow && temp <= value) {
        maxTempBelow = temp
        maxColorBelow = Some((temp, color))
      }
      if (temp <= minTempAbove && temp >= value) {
        minTempAbove = temp
        minColorAbove = Some((temp, color))
      }
    }
    value match {
      case _ if (minColorAbove == None)          => maxColor.get._2 // value above max known temp
      case _ if (maxColorBelow == None)          => minColor.get._2 // value below min known temp
      case _ if (maxColorBelow == minColorAbove) => maxColorBelow.get._2
      case _                                     => interpolateColorPair(maxColorBelow.get, minColorAbove.get, value)
    }
  }

  def interpolateColorPair(a: ColorPair, b: ColorPair, x: Double): Color = {
    val t: Double = (x - a._1) / (b._1 - a._1)
    val ac: Color = a._2
    val bc: Color = b._2
    Color(lerpInt(ac.red, bc.red, t), lerpInt(ac.green, bc.green, t), lerpInt(ac.blue, bc.blue, t))
  }
  // From: https://en.wikipedia.org/wiki/Linear_interpolation
  def lerp(v0: Double, v1: Double, t: Double): Double = {
    (1.0 - t) * v0 + t * v1
  }
  def lerpInt(v0: Int, v1: Int, t: Double): Int = {
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

