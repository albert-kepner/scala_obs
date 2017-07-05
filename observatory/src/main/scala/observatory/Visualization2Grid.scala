package observatory

import com.sksamuel.scrimage.{ Image, Pixel }
import java.nio._
import java.nio.file._
import java.io._

object Visualization2Grid {

  def visualizeGridTest(
    grid: (Int, Int) => Double,
    colors: Iterable[(Double, Color)],
    zoom: Int,
    x: Int,
    y: Int): Image = {
    //    val loc: Location = Interaction.tileLocation(zoom, x, y)
    val m1: String = s"x = $x; y = $y; grid(x,y) = ${grid(x, y)}; zoom = $zoom"
    println(m1);
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
      val pixelLoc: Location = tileTestLocation(twoPower, x * twoPower + col, y * twoPower + row)
      val pixelTemp: Double = {
        val lat = pixelLoc.lat
        val lon = pixelLoc.lon
        val latLo = lat.floor.toInt
        val latHi = lat.ceil.toInt
        val lonLo = lon.floor.toInt
        val lonHi = lon.ceil.toInt
        // println(s" latLo = $latLo; latHi = $latHi; lonLo = $lonLo; lonHi = $lonHi   ")
        val d00 = grid(lonLo, latHi)
        val d01 = grid(lonLo, latLo)
        val d10 = grid(lonHi, latHi)
        val d11 = grid(lonHi, latLo)
        val temp: Double = Visualization2.bilinearInterpolation(lon - lonLo, lat - latLo, d00, d01, d10, d11)
        // println (s" d00, d01, d10, d11 = $d00, $d01, $d10, $d11  temp = $temp ")
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

  def tileTestLocation(twoPower: Int, ix: Int, iy: Int): Location = {
    val y: Double = 1 - iy / (1.0 * twoPower)
    val x: Double = ix / (1.0 * twoPower)
    val loc: Location = Location(y, x)
    // println(s"loc = $loc")
    loc
  }

  def visualizeGridToFile(file: String,
                          grid: (Int, Int) => Double,
                          colors: Iterable[(Double, Color)],
                          zoom: Int,
                          x: Int,
                          y: Int): Unit = {
    val start: Long = System.currentTimeMillis()
    val image: Image = visualizeGridTest(grid, colors, zoom, x, y)

    val filePath: String = s"target/temp1/gridtests/$file.png"
    val dirPath: String = s"target/temp1/gridtests/"
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

    val end: Long = System.currentTimeMillis()
    val elapsed: Float = (end - start) / 1000.0f
    println(s"Elapsed Time = $elapsed")
  }

}
