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

  }
  def read_data(path: String, spark: SparkSession): RDD[(Int, Int)] = {
    spark.read.format("csv")
      // the original data is store in CSV format
      // header: source_node, destination_node
      // here we read the data from CSV and export it as RDD[(Int, Int)],
      // i.e. as RDD of edges
      .option("header", "true")
      // State that the header is present in the file
      .schema(StructType(Array(
      StructField("source_node", IntegerType, false),
      StructField("destination_node", IntegerType, false)
    )))
      // Define schema of the input data
      .load(path)
      // Read the file as DataFrame
      .rdd.map(row => (row.getAs[Int](0), row.getAs[Int](1)))
    // Interpret DF as RDD
  }
  def estimate_gradients_for_edge(
                                   source: Int,
                                   destination: Int,
                                   emb_in: DenseMatrix[Float],
                                   emb_out: DenseMatrix[Float],
                                 ) = {

    val in = emb_in(::, source)
    val out = emb_out(::, destination)


    /*
     * Estimate gradients
     */

    // return a tuple
    // Tuple((Int, DenseVector), (Int, DenseVector))
    // this tuple contains sparse gradients
    // in_grads is vector of gradients for
    // a node with id = source
//    ((source, in_grads), (destination, out_grads))
  }
}
