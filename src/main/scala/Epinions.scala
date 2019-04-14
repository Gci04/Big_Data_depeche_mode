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

import scala.util.Random
import scala.util.control.Breaks._


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
    val alpha = args(3).toFloat
    val top_k = 10


    val data = read_data(path_train, spark)
//    val test = read_data(path_test, spark).repartition(10)

    println("**** successful read of data ****")

    val batch_size = 500
//    val batches =  create_batches(data, batch_size)
    val batches =  create_batches(sprk_contenxt.parallelize(data.take(10000)), batch_size)

    println("**** successful partition of data ****")


    //create and initialize embedding matrices
    val n_nodes = 40334
    val embedding_size = 50
    var incoming_embedding = embeddingMatrix(embedding_size,n_nodes)
    var outgoing_embedding = embeddingMatrix(embedding_size,n_nodes)

    var neg_samples = 20
    //Start train
    for (i <- 1 to iterations){
      println(s"Epoch ${i}")
      for (batch <- batches){
        var emb_in_broadcast = sprk_contenxt.broadcast(incoming_embedding)
        var emb_out_broadcast = sprk_contenxt.broadcast(outgoing_embedding)

        //TODO: get and print loss

        val gr = batch.repartition(4).map(x => gradients(x._1,neg_samples,x._2,emb_in_broadcast.value,emb_out_broadcast.value,n_nodes))

        //update gradients just like gradient descent
        incoming_embedding -= gradients_updates(gr.flatMap(x => x._1),n_nodes,embedding_size) * alpha
        outgoing_embedding -= gradients_updates(gr.flatMap(x => x._2),n_nodes,embedding_size) * alpha
      }

    }

    println("**** successful Gradient descent ****")

    def get_top_neighbours(start_node: Int) = {
      var pred = Array[(Int,Float)]()

      for (end_node <- 0 to n_nodes-1){
        //no self-loops are allowed
        breakable{
          if(end_node == start_node){
            break
          }else{
            //predict and add to result
            pred :+= (end_node, incoming_embedding(::, start_node).t * outgoing_embedding(::, end_node))
          }
        }
      }
      //take the top neighbours 10 for each node and sort by values in descending order
      val result = pred.sortBy(_._2).takeRight(top_k).reverse

      //return start node and its 10 neighbours
      (result.map(_._1), result.map(_._2))
    }
    //Get result and save it to txt file . Note : takes forever to finish dump to file
    val results = sprk_contenxt.parallelize(0 to n_nodes-1).map( x => (x, get_top_neighbours(x)))
    results.map(x =>(x._1,x._2._1.mkString(" "))).map(x => s"${x._1}, ${x._2}").saveAsTextFile("results.txt")
    results.map(x => (x._1, x._2._2.mkString(" "))).map(x => s"${x._1}, ${x._2}").saveAsTextFile("results_ratings.csv")

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
    val generator = new UniformGenerator()

    var data = Array.fill(vocab_len * embeding_size)(generator.nextValue().toFloat)
    new DenseMatrix(embeding_size, vocab_len, data)
  }

  def create_batches(data: RDD[(Int, Int)], batch_size: Int) = {
    // switch positions to have index as key RDD[(key, (start,destination))]. Random split and RDD.sample can also be used
    val indexed = data.zipWithIndex().map(r => ((r._2 / batch_size).toInt, r._1))
    val tot_batches = (indexed.count()/batch_size).toInt
    //do not return all batches all at once but yield or generate each batch per iteration in the gradient descent when needed
    for (i <- 0 until tot_batches)
      yield indexed.filter(_._1 == i).map(_._2)
  }
  def empty_grad_matrix(vocab_len:Int, embeding_size:Int) =  {
    val data = Array.fill(vocab_len * embeding_size)(0.0f)
    new DenseMatrix(embeding_size, vocab_len, data)
  }
  def gradients_updates(gr: RDD[(Int, DenseVector[Float])],vocab_len:Int, embeding_size:Int) = {
    val g_temp = get_gradients(gr)
    var res = empty_grad_matrix(vocab_len,embeding_size)

    for (k <- g_temp.keys) {
      res(::, k) := g_temp(k)
    }
    res
  }
  def gradients(source: Int,neg_samples : Int,destination: Int,emb_in: DenseMatrix[Float],emb_out: DenseMatrix[Float],n_nodes : Int) = {
    val (grad_in,grad_out) = estimate_gradients_for_edge(source,destination,emb_in,emb_out)
    //separate in_grads from out_grads
    var in_grad = Array(grad_in)
    var out_grad = Array(grad_out)

    //generate negative samples
    val gen = new Random()
    for (i <- 0 to neg_samples){
      var (neg_in,neg_out) = estimate_gradients_for_neg_s(source,gen.nextInt(n_nodes),emb_in,emb_out)
      in_grad :+= neg_in
      out_grad :+= neg_out
    }

    (in_grad, out_grad)
  }
  def get_gradients(gr: RDD[(Int, DenseVector[Float])]) = {
    val n =  gr.count().toFloat
    gr.reduceByKey(_+_).map(x => (x._1, x._2 / n))
      .collectAsMap()
  }
  def estimate_gradients_for_edge(source: Int,destination: Int,emb_in: DenseMatrix[Float],emb_out: DenseMatrix[Float]) = {

    val in = emb_in(::, source)
    val out = emb_out(::, destination)
    val tag = 1.0f

    val act = sigma(in.t * out * tag)
    val grad_in =  -tag * out * (1 - act)
    val grad_out =  -tag * in * (1 - act)

    ((source, grad_in), (destination, grad_out))
  }
  def estimate_gradients_for_neg_s(source: Int,destination: Int,emb_in: DenseMatrix[Float],emb_out: DenseMatrix[Float]) = {

    val in = emb_in(::, source)
    val out = emb_out(::, destination)
    val tag = -1.0f

    val act = sigma(in.t * out * tag).toFloat
    val grad_in =  out * (1 - act)
    val grad_out =  in * (1 - act)

    ((source, grad_in), (destination, grad_out))
  }
  def sigma(x:Float) = {
    1 / (1 + Math.exp(-x)).toFloat
  }
  def loss (x:Float) = {
    -log(sigma(x)).toFloat
  }
//  def get_loss()
}
