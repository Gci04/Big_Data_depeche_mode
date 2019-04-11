import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StructField, StructType}
import breeze.linalg.{DenseMatrix, DenseVector}
import breeze.numerics.log
import org.apache.spark.mllib.random.UniformGenerator

object Epinions {
  def main(args: Array[String]) {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

//    create spark session
    val spark = SparkSession
      .builder()
      .appName("Epinions.com")
      .config("spark.master", "local")
      .getOrCreate()

    val sprk_contenxt: SparkContext = spark.sparkContext

//    read train and test data from program argument

    val path_train = args(0)
    val path_test = args(1)
    // number of epochs
    //val iterations = 10
    val iterations = args(2).toInt
    //learning rate
    val alpha = args(3).toDouble


    val data = read_data(path_train, spark)
    val test = read_data(path_test, spark).repartition(10)

    println("---successful read of data---")

    val batch_size = 500
    val batches =  create_batches(data, batch_size)

    println("---successful partition of data---")


    //create and initialize embedding matrices
    val n_nodes = 40334
    val embedding_size = 50
    var incoming_embedding =embeddingMatrix(embedding_size,n_nodes)
    var outgoing_embeding = embeddingMatrix(embedding_size,n_nodes)

    var neg_samples = 20
    //Start train
    for (i <- 1 to iterations){
      for (batch in batches){
        var emb_in_broadcast = sprk_contenxt.broadcast(incoming_embedding)
        var emb_out_broadcast = sprk_contenxt.broadcast(outgoing_embeding)

        //calculate gradients
        for (k <- in_grads_local.keys) {
          /*
           * Update weights in column k of emb_in
           */
        }
      }
      //update gradients just like gradient descent
    }

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
  def embeddingMatrix(embeding_size: Int, vocab_len: Int ) = {
    var generator = new UniformGenerator()

    var data = Array.fill(vocab_len * embeding_size)(generator.nextValue().toFloat)
    new DenseMatrix(embeding_size, vocab_len, data)
  }

  def create_batches(data: RDD[(Int, Int)], batch_size: Int) = {
    " "
  }


  def estimate_gradients_for_edge(source: Int,destination: Int,emb_in: DenseMatrix[Float],emb_out: DenseMatrix[Float]) = {


    val in = emb_in(::, source)
    val out = emb_out(::, destination)

    ((source, in_grads), (destination, out_grads))
  }

  def get_top(source: Int, top_k: Int) = {

  }
}

object Network{
  def gradients(): Unit ={

  }
  def gradients_updates(): Unit ={

  }

  def sigma(x:Double): Double ={
    1. / (1. + Math.exp(-x))
  }
  def loss (x:Double) = {
    -log(sigma(x))
  }

}