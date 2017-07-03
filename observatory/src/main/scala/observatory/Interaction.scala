package observatory

import com.sksamuel.scrimage.{ Image, Pixel, ScaleMethod }

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
    val point: LatLonPoint = Tile(x, y, zoom.toShort).toLatLon
    Location(point.lat, point.lon)
  }

  //  def tileSubtileLocations(zoom: Int, x: Int, y: Int, addZoomLevel: Int): Unit = {
  //    val twoPower: Int = 1 << addZoomLevel
  //
  //    println(s"twoPower = $twoPower")
  //
  //    val baseLoc = tileLocation(zoom, x, y)
  //    println(s"baseLoc = $baseLoc")
  //    val xPlus = tileLocation(zoom, x + 1, y)
  //    println(s"xPlus = $xPlus")
  //    val yPlus = tileLocation(zoom, x, y + 1)
  //    println(s"yPlus = $yPlus")
  //
  //    for (
  //      col <- (0 until twoPower);
  //      row <- (0 until twoPower)
  //    ) {
  //      val subtileLoc = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
  //
  //      if (col <= 2 && row <= 2) {
  //        // detail output
  //        println(s"col = $col, row = $row, subtileLoc = $subtileLoc")
  //      }
  //    }
  //  }
  /**
   * @param temperatures Known temperatures
   * @param colors Color scale
   * @param zoom Zoom level
   * @param x X coordinate
   * @param y Y coordinate
   * @return A 256×256 image showing the contents of the tile defined by `x`, `y` and `zooms`
   */
  def tileZoomLevel(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int, addZoomLevel: Int): Image = {
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
      val pixelLoc: Location = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = Visualization.predictTemperature(temperatures, pixelLoc)
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
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
  def tile(temperatures: Iterable[(Location, Double)], colors: Iterable[(Double, Color)], zoom: Int, x: Int, y: Int): Image = {
    val debug: Boolean = false;
    val addZoomLevel: Int = 8
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
      val pixelLoc: Location = tileLocation(zoom + addZoomLevel, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = Visualization.predictTemperature(temperatures, pixelLoc)
      val pixelColor: Color = Visualization.interpolateColor(colors, pixelTemp)
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
   * Generate avg temp image tiles at 0-3 zoom levels for the given year.
   */
  def generateTilesForYear(year: Integer): Unit = {
    val stationsFile = "/stations.csv"
    val temperaturesFile = s"/$year.csv"
    val allTempsForYear = Extraction.locateTemperatures(year, stationsFile, temperaturesFile)
    val avgTempsForYear: Iterable[(Location, Double)] = Extraction.locationYearlyAverageRecords(allTempsForYear)
    val dataOneYear: (Int, Iterable[(Location, Double)]) = (year, avgTempsForYear)
    val yearlyData = List(dataOneYear)
    generateTilesData1(yearlyData, generateImageStandardTempColor)
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
              generateImage(year, zoom, col, row, data)
            }
          }

        }
      }

    }
  }

  type Data1 = (Int, Iterable[(Location, Double)])

  def generateTilesData1(
    yearlyData: Iterable[(Int, Iterable[(Location, Double)])],
    generateImage: (Int, Int, Int, Int, Iterable[(Location, Double)]) => Unit): Unit = {
    yearlyData.foreach {
      _ match {
        case (year, data) => {
          for (zoom <- (0 to 3)) {
            val twoPower: Int = 1 << zoom
            for (
              row <- 0 until twoPower;
              col <- 0 until twoPower
            ) {
              generateImage(year, zoom, col, row, data)
            }
          }

        }
      }

    }
  }
  def generateImageWithColor(year: Int, zoom: Int, col: Int, row: Int, data: Data1, colors: Iterable[(Double, Color)]): Unit = {
    data match {
      case (year2, temperatures) => {
        val image = tileZoomLevel(temperatures, colors, zoom, col, row, 8)
        writeImageToFile("target", image, year, zoom, col, row)
      }
    }
  }
  def generateImageStandardTempColor(year: Int, zoom: Int, col: Int, row: Int, temperatures: Iterable[(Location, Double)]): Unit = {
    val msg = s"make tile for year: $year, zoom: $zoom, col: $col, row: $row"
    println(msg)
    val image = tileZoomLevel(temperatures, Visualization.standardColors, zoom, col, row, 6)
    val ref = new Object()
    val image2 = image.scaleTo(256,256,ScaleMethod.FastScale)
    println("ready to write image to file at time = "+ new java.util.Date())
    writeImageToFile("target", image2, year, zoom, col, row)
  }

  def writeImageToFile(base: String, image: Image, year: Int, zoom: Int, x: Int, y: Int): Unit = {
    val filePath: String = s"$base/temperatures/$year/$zoom/${x}-${y}.png"
    val dirPath: String = s"$base/temperatures/$year/$zoom/"
    println(s"filePath = $filePath")
    try {
      val dir: File = new File(dirPath);
      println(s"dirPath = $dirPath")
      val newDirs: Boolean = dir.mkdirs()
      println(s"newDirs = $newDirs")
      image.output(Paths.get(filePath))
    } catch {
      case e: Exception => e.printStackTrace
    }

  }

}
