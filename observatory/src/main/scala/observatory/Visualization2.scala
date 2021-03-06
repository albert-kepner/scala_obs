package observatory

import com.sksamuel.scrimage.{ Image, Pixel }

/**
 * 5th milestone: value-added information visualization
 */
object Visualization2 {

  /**
   * @param x X coordinate between 0 and 1
   * @param y Y coordinate between 0 and 1
   * @param d00 Top-left value
   * @param d01 Bottom-left value
   * @param d10 Top-right value
   * @param d11 Bottom-right value
   * @return A guess of the value at (x, y) based on the four known values, using bilinear interpolation
   *         See https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
   */
  def bilinearInterpolationOrig(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double): Double = {
    //    if (x == 0.0 && y == 0.0) {
    //      println(s"x: $x y: $y d00: $d00 d01: $d01 d10: $d10 d11: $d11")
    //    }
    def f(a: Int, b: Int): Double = {
      (a, b) match {
        case (0, 0) => d00
        case (0, 1) => d01
        case (1, 0) => d10
        case (1, 1) => d11
      }
    }
    val a_00 = f(0, 0)
    val a_10 = f(1, 0) - f(0, 0)
    val a_01 = f(0, 1) - f(0, 0)
    val a_11 = f(1, 1) + f(0, 0) - (f(1, 0) + f(0, 1))

    val result = a_00 + a_10 * x + a_01 * y + a_11 * x * y
    result
  }
  /**
   * Rewrite...
   * Based on bilinear Interpolation wiki article at: https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
   */
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double): Double = {
    val result: Double = d00 * (1.0 - x) * (1.0 - y) +
      d10 * x * (1.0 - y) +
      d01 * (1.0 - x) * y +
      d11 * x * y
    result
  }

  /**
   * Rewrite...(Interpolate within 4 x 4 grid
   * Based on bilinear Interpolation wiki article at: https://en.wikipedia.org/wiki/Bilinear_interpolation#Unit_Square
   */
  def bilinearInterpolation4(
    x4: Double,
    y4: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double): Double = {
    val x = x4/4.0
    val y = y4/4.0
    val result: Double = d00 * (1.0 - x) * (1.0 - y) +
      d10 * x * (1.0 - y) +
      d01 * (1.0 - x) * y +
      d11 * x * y
    result
  }

  /**
   * @param grid Grid to visualize
   * @param colors Color scale to use
   * @param zoom Zoom level of the tile to visualize
   * @param x X value of the tile to visualize
   * @param y Y value of the tile to visualize
   * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
   */
  def visualizeGrid(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int): Image = {
    val m1: String = s"x = $x; y = $y; grid(x,y) = ${grid(x, y)}; zoom = $zoom"
    println(m1);
    showColors(colors)
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1 << addZoomLevel
    val imageHeight: Int = imageWidth

    val pixLen: Integer = imageWidth * imageHeight
    val pixels = new Array[Pixel](pixLen)

    val twoPower: Int = imageWidth

    var index: Int = 0

    for (
      row <- (0 until twoPower);
      col <- (0 until twoPower)
    ) {
      val pixelLoc: Location = Interaction.tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = {
        val lat = pixelLoc.lat
        val lon = pixelLoc.lon
        val latLo = lat.floor.toInt
        val latHi = lat.ceil.toInt
        val lonLo = lon.floor.toInt
        val lonHi = lon.ceil.toInt
//        val d00 = grid(lonLo, latHi)
//        val d01 = grid(lonLo, latLo)
//        val d10 = grid(lonHi, latHi)
//        val d11 = grid(lonHi, latLo)
        val d00 = grid(latHi, lonLo)
        val d01 = grid(latLo, lonLo)
        val d10 = grid(latHi, lonHi)
        val d11 = grid(latLo, lonHi)
        val temp: Double = bilinearInterpolation(lon - lonLo, latHi - lat, d00, d01, d10, d11)
        temp
      }
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
      if (row == 0 && col == 0) {
        println(m1);
        println(s"pixelLoc = $pixelLoc; pixelTemp = $pixelTemp; pixelColor = $pixelColor")
      }
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 127)
      pixels(index) = pixel
      index += 1

    }
    val image: Image = Image(imageWidth, imageHeight, pixels)
    if (debug || true) {
      println(s"image: $image")
    }
    image
  }
  /**
   * @param grid Grid to visualize (sparse grid with data points every 4 degrees)
   * @param colors Color scale to use
   * @param zoom Zoom level of the tile to visualize
   * @param x X value of the tile to visualize
   * @param y Y value of the tile to visualize
   * @return The image of the tile at (x, y, zoom) showing the grid using the given color scale
   */
  def visualizeGrid4(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int): Image = {
    val m1: String = s"x = $x; y = $y; grid(x,y) = ${grid(x, y)}; zoom = $zoom"
    println(m1);
    showColors(colors)
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1 << addZoomLevel
    val imageHeight: Int = imageWidth

    val pixLen: Integer = imageWidth * imageHeight
    val pixels = new Array[Pixel](pixLen)

    val twoPower: Int = imageWidth

    var index: Int = 0

    for (
      row <- (0 until twoPower);
      col <- (0 until twoPower)
    ) {
      val pixelLoc: Location = Interaction.tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = {
        val lat = pixelLoc.lat
        val lon = pixelLoc.lon
        val latLo = latLo4(lat)
        val latHi = latHi4(lat)
        val lonLo = lonLo4(lon)
        val lonHi = lonHi4(lon)
        val d00 = grid(latHi, lonLo)
        val d01 = grid(latLo, lonLo)
        val d10 = grid(latHi, lonHi)
        val d11 = grid(latLo, lonHi)
        val temp: Double = bilinearInterpolation4(lon - lonLo, latHi - lat, d00, d01, d10, d11)
        temp
      }
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
      if (row == 0 && col == 0) {
        println(m1);
        println(s"pixelLoc = $pixelLoc; pixelTemp = $pixelTemp; pixelColor = $pixelColor")
      }
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 127)
      pixels(index) = pixel
      index += 1

    }
    val image: Image = Image(imageWidth, imageHeight, pixels)
    if (debug || true) {
      println(s"image: $image")
    }
    image
  }
  def latHi4(lat: Double) : Int = {
    val lat1 = (lat/4.0).ceil.toInt * 4
    if(lat1 > 88) {
      88
    } else {
      lat1
    }
  }
  def latLo4(lat: Double) : Int = {
    val lat1 = (lat/4.0).floor.toInt * 4
    if(lat1 < -88) {
      -88
    } else {
      lat1
    }
  }
  def lonHi4(lon: Double) : Int = {
    val lon1 = (lon/4.0).ceil.toInt * 4
    if(lon1 > 180) {
      180
    } else {
      lon1
    }
  }
  def lonLo4(lon: Double) : Int = {
    val lon1 = (lon/4.0).floor.toInt * 4
    if(lon1 < -180) {
      -180
    } else {
      lon1
    }
  }
  def showColors(colors: Iterable[(Double, Color)]): Unit = {
    for ((t, c) <- colors) {
      println(s"Temp: $t; Color: $c")
    }
  }
  /**
   * @param temperatures Known temperatures
   * @param colors Color scale
   * @param zoom Zoom level
   * @param x X coordinate
   * @param y Y coordinate
   * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
   */
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1 << addZoomLevel
    val imageHeight: Int = imageWidth

    val pixLen: Integer = imageWidth * imageHeight
    val pixels = new Array[Pixel](pixLen)

    val twoPower: Int = imageWidth

    var index: Int = 0

    for (
      col <- (0 until twoPower);
      row <- (0 until twoPower)
    ) {
      val pixelLoc: Location = Interaction.tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = Visualization.predictTemperature(temperatures, pixelLoc)
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 255)
      pixels(index) = pixel
      index += 1

    }
    val image: Image = Image(imageWidth, imageHeight, pixels)
    if (debug || true) {
      println(s"image: $image")
    }
    image
  }

}
