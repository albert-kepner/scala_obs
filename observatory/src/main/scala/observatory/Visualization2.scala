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
  def bilinearInterpolation(
    x: Double,
    y: Double,
    d00: Double,
    d01: Double,
    d10: Double,
    d11: Double): Double = {
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
    ???
  }

}
