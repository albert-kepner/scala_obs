package observatory

/**
 * 6th (and last) milestone: user interface polishing
 */
object Interaction2 {

  val standardColors: List[(Double, Color)] = List(
    (60, Color(255, 255, 255)),
    (32, Color(255, 0, 0)),
    (12, Color(255, 255, 0)),
    (0, Color(0, 255, 255)),
    (-15, Color(0, 0, 255)),
    (-50, Color(33, 0, 107)),
    (-60, Color(0, 0, 0)))

  val deviationColors: List[(Double, Color)] = List(
    (7, Color(0, 0, 0)),
    (4, Color(255, 0, 0)),
    (2, Color(255, 255, 0)),
    (0, Color(255, 255, 255)),
    (-2, Color(0, 255, 255)),
    (-7, Color(0, 0, 255)))

  /**
   * @return The available layers of the application
   */
  def availableLayers: Seq[Layer] = {

    val temperatures = new Layer(LayerName.Temperatures, standardColors, (1975 to 2015))

    val deviations = new Layer(LayerName.Deviations, deviationColors, (1990 to 2015))
    List(temperatures, deviations)
  }

  /**
   * @param selectedLayer A signal carrying the layer selected by the user
   * @return A signal containing the year bounds corresponding to the selected layer
   */
  def yearBounds(selectedLayer: Signal[Layer]): Signal[Range] = {
    Signal(selectedLayer().bounds)
  }

  /**
   * @param selectedLayer The selected layer
   * @param sliderValue The value of the year slider
   * @return The value of the selected year, so that it never goes out of the layer bounds.
   *         If the value of `sliderValue` is out of the `selectedLayer` bounds,
   *         this method should return the closest value that is included
   *         in the `selectedLayer` bounds.
   */
  def yearSelection(selectedLayer: Signal[Layer], sliderValue: Signal[Int]): Signal[Int] = {
    Signal(
      {
        val bounds = yearBounds(selectedLayer)()
        val low = bounds.min
        val high = bounds.max
        sliderValue() match {
          case v if (v < low)  => low
          case v if (v > high) => high
          case v               => v
        }
      })
  }

  /**
   * @param selectedLayer The selected layer
   * @param selectedYear The selected year
   * @return The URL pattern to retrieve tiles
   */
  def layerUrlPattern(selectedLayer: Signal[Layer], selectedYear: Signal[Int]): Signal[String] = {
    Signal(s"target/${selectedLayer().layerName.id}/${selectedYear()}/{z}/{x}-{y}.png")
  }

  /**
   * @param selectedLayer The selected layer
   * @param selectedYear The selected year
   * @return The caption to show
   */
  def caption(selectedLayer: Signal[Layer], selectedYear: Signal[Int]): Signal[String] = {
    Signal(
      {
        val theLayer: Layer = selectedLayer()
        val year: Int = selectedYear()
        s"${theLayer.layerName.id} (${year})"
      })
  }

}

sealed abstract class LayerName(val id: String)
object LayerName {
  case object Temperatures extends LayerName("temperatures")
  case object Deviations extends LayerName("deviations")
}

/**
 * @param layerName Name of the layer
 * @param colorScale Color scale used by the layer
 * @param bounds Minimum and maximum year supported by the layer
 */
case class Layer(layerName: LayerName, colorScale: Seq[(Double, Color)], bounds: Range)

