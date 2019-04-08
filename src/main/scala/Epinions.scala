import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating}

object Epinions {
  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

  }
}
