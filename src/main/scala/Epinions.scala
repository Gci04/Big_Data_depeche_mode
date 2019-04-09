import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import breeze.linalg.{DenseMatrix, DenseVector}

object Epinions {
  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

//    create spark session
    val spark = SparkSession
      .builder()
      .appName("Epinions.com")
      .config("spark.master", "local")
      .getOrCreate()

//    read train and test data from program argument

    val path_train = args(0)
    val path_test = args(1)

    val data = read_data(path_train, spark)
    val test = read_data(path_test, spark).repartition(10)

    println("---successful read of data---")

    val batch_size = 500
    val batches =  partition(data, batch_size)

    println("---successful partition of data---")

  }

  def read_data(path: String, spark: SparkSession): RDD[(Int, Int)] = {
    spark.read.format("csv")
      .option("header", "true")
      .schema(StructType(Array(
      StructField("source_node", IntegerType, false),
      StructField("destination_node", IntegerType, false))))
      .load(path)
      .rdd.map(row => (row.getAs[Int](0), row.getAs[Int](1)))
  }

  def partition(data: RDD[(Int, Int)], batch_size: Int) = {
    " "
  }

  def estimate_gradients_for_edge(source: Int,destination: Int,emb_in: DenseMatrix[Float],emb_out: DenseMatrix[Float]) = {

    val in = emb_in(::, source)
    val out = emb_out(::, destination)
  }

  def get_top(source: Int, top_k: Int) = {

  }
}
