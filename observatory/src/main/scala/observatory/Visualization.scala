package observatory

import com.sksamuel.scrimage.{ Image, Pixel }
import scala.math._

/**
 * 2nd milestone: basic visualization
 */
object Visualization {

  val p: Int = 2 // Inverse Distance weighting exponent setting.

  /**
   * @param temperatures Known temperatures: pairs containing a location and the temperature at this location
   * @param location Location where to predict the temperature
   * @return The predicted temperature at `location`
   */
  def predictTemperature(temperatures: Iterable[(Location, Double)], location: Location, debug: Boolean = false): Double = {
    var sumWi_X_Ui: Double = 0.0
    var sumWi: Double = 0.0
    var matchLocationTemp: Option[Double] = None
    if(debug) println(s"location = $location")
    for ((l1, ui) <- temperatures) {
      val d = greatCircleDistance(l1, location);
      if(debug) println(s"d! = $d")
      if(debug) println(s"      l1 = $l1, ui = $ui, d = $d")
      if (d < 1.0) {
        matchLocationTemp = Some(ui)
      }
      matchLocationTemp match {
        case None => {
          val Wi: Double = W(l1, location, debug)
          if(debug) println(s"Wi! = $Wi")
          sumWi_X_Ui += (Wi * ui)
          sumWi += Wi
        }
        case _ => {}
      }
    }
    val temp: Double =
    matchLocationTemp match {
      case None => {
        sumWi_X_Ui / sumWi
      }

      case _ => { matchLocationTemp.get }
    }
    if(debug) println(s"temp = $temp")
    temp
  }

  def W(l1: Location, l2: Location, debug: Boolean = false): Double = {
    val d = greatCircleDistance(l1, l2, debug);
    val w = distanceWeightW(d)
    w
  }

  def distanceWeightW(distanceKm: Double): Double = {
    var denominator: Double = 1.0
    for (x <- (1 to p)) {
      denominator *= distanceKm
    }
    1.0 / denominator
  }

  /**
   * convert angle in degrees (latitude or longitude) to radians.
   */
  def rad(degree: Double): Double = {
    degree / 180.0 * Pi
  }
  def deg(rad: Double): Double = {
    180.0 * rad / Pi
  }

  def greatCircleDistance(l1: Location, l2: Location, debug: Boolean = false): Double = {
    val lat1 = rad(l1.lat)
    val lon1 = rad(l1.lon)
    val lat2 = rad(l2.lat)
    val lon2 = rad(l2.lon)
    val deltaLon = abs(lon1 - lon2)
    val x: Double = sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(deltaLon)
    val angle = acos(x)
    val radiusKm: Double = 6371
    val distanceKm = radiusKm * angle
    if(debug) {
      println(s"greatCircleDistance: l1 = $l1, l2 = $l2, distanceKm = $distanceKm")
    }
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
    // From: https://en.wikipedia.org/wiki/Linear_interpolation
    def lerp(v0: Double, v1: Double, t: Double): Double = {
      (1.0 - t) * v0 + t * v1
    }
    def lerpInt(v0: Int, v1: Int, t: Double): Int = {
      ((1.0 - t) * v0 + t * v1).round.toInt
    }
    val t: Double = (x - a._1) / (b._1 - a._1)
    val ac: Color = a._2
    val bc: Color = b._2
    Color(lerpInt(ac.red, bc.red, t), lerpInt(ac.green, bc.green, t), lerpInt(ac.blue, bc.blue, t))
  }

  /**
   * @param temperatures Known temperatures
   * @param colors Color scale
   * @return A 360×180 image where each pixel shows the predicted temperature at its location
   */
  def visualize(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)]): Image = {

    val debug: Boolean = false;

    val imageWidth: Int = 360
    val imageHeight: Int = 180

    val startLon: Double = -180
    val startLat: Double = 90

    val endLat: Double = startLat + (imageHeight - 1)
    val endLon: Double = startLon + (imageWidth - 1)

    val pixLen: Integer = imageWidth * imageHeight
    val pixels = new Array[Pixel](pixLen)

    var lon = startLon
    var lat = startLat
    var index: Int = 0
    while (index < pixLen) {
      val pixelLoc: Location = Location(lat, lon)
      val pixelTemp: Double = predictTemperature(temperatures, pixelLoc)
      val pixelColor: Color = interpolateColor(colors, pixelTemp)
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 255)
      pixels(index) = pixel
      index += 1

      if (debug) {
        println(s"lat: $lat, lon: $lon, pixelTemp: $pixelTemp, pixelColor: $pixelColor")
        println(s"pixel: $pixel")
      }

      lon += 1.0
      if (lon > endLon) {
        lon = startLon
        lat += -1.0
      }
    }
    val image: Image = Image(imageWidth, imageHeight, pixels)
    if (debug || true) {
      println(s"image: $image")
    }
    image
  }

}

