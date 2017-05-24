package observatory
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

import org.apache.spark.rdd.RDD
import org.apache.log4j.{Level, Logger}


object Spark {

  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  println("Starting Spark...")

  val conf: SparkConf = new SparkConf().setMaster("local").setAppName("Observatory");
  val sc: SparkContext = new SparkContext(conf)
  
  

}
