package observatory

import com.sksamuel.scrimage.{ Image, Pixel }

import java.nio._
import java.nio.file._
import java.io._

/**
 * 3rd milestone: interactive visualization
 */
object Interaction {

  /**
   * @param zoom Zoom level
   * @param x X coordinate
   * @param y Y coordinate
   * @return The latitude and longitude of the top-left corner of the tile, as per http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
   */
  def tileLocation(zoom: Int, x: Int, y: Int): Location = {
    val point: LatLonPoint = Tile(y, x, zoom.toShort).toLatLon
    Location(point.lat, point.lon)
  }

  def tileSubtileLocations(zoom: Int, x: Int, y: Int, addZoomLevel: Int): Unit = {
    val twoPower: Int = 1 << addZoomLevel

    println(s"twoPower = $twoPower")

    val baseLoc = tileLocation(zoom, x, y)
    println(s"baseLoc = $baseLoc")
    val xPlus = tileLocation(zoom, x + 1, y)
    println(s"xPlus = $xPlus")
    val yPlus = tileLocation(zoom, x, y + 1)
    println(s"yPlus = $yPlus")

    for (
      col <- (0 until twoPower);
      row <- (0 until twoPower)
    ) {
      val subtileLoc = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)

      if (col <= 2 && row <= 2) {
        // detail output
        println(s"col = $col, row = $row, subtileLoc = $subtileLoc")
      }
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
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, y: Int, x: Int): Image = {
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1<<addZoomLevel
    val imageHeight: Int = imageWidth

    val pixLen: Integer = imageWidth * imageHeight
    val pixels = new Array[Pixel](pixLen)

    val twoPower: Int = imageWidth
    
    var index: Int = 0

    for (
      col <- (0 until twoPower);
      row <- (0 until twoPower)
    ) {
      val pixelLoc: Location = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
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
  def tileDebug(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Unit = {
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1<<addZoomLevel
    val imageHeight: Int = imageWidth

//    val pixLen: Integer = imageWidth * imageHeight
//    val pixels = new Array[Pixel](pixLen)

    val twoPower: Int = imageWidth
    
//    var index: Int = 0

    for (
      col <- (0 until twoPower by 64);
      row <- (0 until twoPower by 64)
    ) {
      val pixelLoc: Location = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = Visualization.predictTemperature(temperatures, pixelLoc)
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 255)
      val m1: String = s"col(x) = $col, row(y) = $row, pixelLoc = $pixelLoc, pixelTemp = $pixelTemp, pixelColor = $pixelColor"
      println(m1)
//      pixels(index) = pixel
//      index += 1

    }
//    val image: Image = Image(imageWidth, imageHeight, pixels)
//    if (debug || true) {
//      println(s"image: $image")
//    }
//    image
  }
  def tileDebug2(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Unit = {
    val addZoomLevel: Int = 8
    val debug: Boolean = false;

    val imageWidth: Int = 1<<addZoomLevel
    val imageHeight: Int = imageWidth
    val twoPower: Int = imageWidth
    for (
      col <- (0 until twoPower by 8);
      row <- (0 until twoPower by 8)
    ) {
      val pixelLoc: Location = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val debugTemp: Boolean = (row == 192)
      val pixelTemp: Double = Visualization.predictTemperature(temperatures, pixelLoc, debugTemp)
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
      val pixel: Pixel = Pixel(pixelColor.red, pixelColor.green, pixelColor.blue, 255)
      val m1: String = s"col(x) = $col, row(y) = $row, pixelLoc = $pixelLoc, pixelTemp = $pixelTemp, pixelColor = $pixelColor"
      if(row == 192) {
        println(m1)
      }
    }
  }

  /**
   * Generates all the tiles for zoom levels 0 to 3 (included), for all the given years.
   * @param yearlyData Sequence of (year, data), where `data` is some data associated with
   *                   `year`. The type of `data` can be anything.
   * @param generateImage Function that generates an image given a year, a zoom level, the x and
   *                      y coordinates of the tile and the data to build the image from
   */
  def generateTiles[Data](
    yearlyData: Iterable[(Int, Data)],
    generateImage: (Int, Int, Int, Int, Data) => Unit): Unit = {
    yearlyData.foreach {
      _ match {
        case (year, data) => {
          for (zoom <- (0 to 3)) {
            val twoPower: Int = 1 << zoom
            for (
              row <- 0 until twoPower;
              col <- 0 until twoPower
            ) {
              generateImage(year, zoom, row, col, data)
            }
          }

        }
      }

    }
  }

  type Data1 = (Int, Iterable[(Location, Double)])

  def generateImageWithColor(year: Int, zoom: Int, col: Int, row: Int, data: Data1, colors: Iterable[(Double, Color)]): Unit = {
    data match {
      case (year2, temperatures) => {
        val image = tile(temperatures, colors, zoom, col, row)
        writeImageToFile("target/temp1", image, year, zoom, col, row)
      }
    }
  }
  
  def writeImageToFile(base: String, image: Image, year: Int, zoom: Int, x: Int, y: Int) : Unit = {
    val filePath: String  = s"$base/temperatures/$year/$zoom/$x.$y.png"
    val dirPath: String = s"$base/temperatures/$year/$zoom/"
    println(s"filePath = $filePath")
    try {
        val dir:File= new File(dirPath);
        println(s"dirPath = $dirPath")
        val newDirs:Boolean = dir.mkdirs()
        println(s"newDirs = $newDirs")
        image.output(Paths.get(filePath))
    } catch {
        case e: Exception => e.printStackTrace
    } 
    
  }

}
