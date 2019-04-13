//------------------------------------------------------------------------------------------------------------------------
// SORT EDGES BY RATING
// function for sorting edges by precomputed rating
def sort_neighbours_by_rating (neighbors_ratings : Array[((Int,Int),Double)]) : Array[((Int,Int),Double)]={
  // sort by the second element and reverse to let it be in the descending order
  var sorted_by_rating_neighbors = neighbors_ratings.sortBy(_._2).reverse
  return (sorted_by_rating_neighbors)
}
// usage example
// var neighbors_ratings : Array[((Int,Int),Double)] = Array(
//   ((0,1),15.3),
//   ((1,2),0.6),
//   ((1,3),-2.0),
//   ((2,3),8.9))
// var sorted_by_rating_neighbors = sort_neighbours_by_rating(neighbors_ratings)
// println(sorted_by_rating_neighbors.deep.mkString("\n"))

//------------------------------------------------------------------------------------------------------------------------
// FILTER EXISTING EDGES
// function for filtering processing_edges from already existed eedges
def filter_existing_edges (existing_edges : Array[(Int,Int)], processing_edges_ratings : Array[((Int,Int),Double)]) : Array[((Int,Int),Double)] = {
  // initializing of result
  var result_array : Array[((Int,Int),Double)] = Array()
  // iterating over processing_edges
  for (edge_n_rating <- processing_edges_ratings) {
    // check that edge did not exist early
    if (!existing_edges.contains(edge_n_rating._1)) {
      // append edge to result
      result_array = result_array:+((edge_n_rating))
    }
  }

   return (result_array)
}
// usage example
// var existing_edges = Array((0,1),(0,2),(1,4),(2,3))
// var processing_edges_ratings : Array[((Int,Int),Double)] = Array(
//   ((0,1),12.6),
//   ((1,2),0.0),
//   ((1,3),-3.43),
//   ((2,3),23.1),
//   ((3,4),0.001))
// var processing_edges_ratings_filtered = filter_existing_edges(
//   existing_edges, processing_edges_ratings)
// println(processing_edges_ratings_filtered.deep.mkString("\n"))

//------------------------------------------------------------------------------------------------------------------------
// GET TOP 10 RATED NEIGHBORS
// function for extracted 10 recomandations for each node
def get_top_ten_neighbors (edges_ratings : Array[((Int,Int),Double)]) : Array[((Int,Int),Double)] = {
  // initializing of the result
  var top_ten_overall : Array[((Int,Int),Double)] = Array()
  // concat all sourceand destination edges together
  var set_of_all_nodes = edges_ratings.map(_._1._1) ++ edges_ratings.map(_._1._2)
  // iterating over set of unique nodes
  for (node <- set_of_all_nodes.distinct) {
    // println(node)
    // initializing top 10 for node
    var top_ten_for_node : Array[((Int,Int),Double)] = Array()
    // initializing array of all edges for node
    var edges_ratings_for_node : Array[((Int,Int),Double)] = Array()
    // iterating over edges and ratings pairs
    for (edge_n_rating <- edges_ratings) {
      // check that node is present in the edge
      if (edge_n_rating._1._1==node || edge_n_rating._1._2==node) {
        // println(edge_n_rating)
        // add such edge for the node
        edges_ratings_for_node = edges_ratings_for_node:+((edge_n_rating))
      }
    }
    // select top 10 for a node
    top_ten_for_node = sort_neighbours_by_rating(edges_ratings_for_node).take(10)
    // println("Top 10:")
    // println(top_ten_for_node.deep.mkString("\n"))
    // update result with node's top 10
    top_ten_overall = top_ten_overall ++ top_ten_for_node
  }
  return (top_ten_overall.distinct)
}
// usage
// var edges_rating = Array(
//   ((0,1),12.6),
//   ((1,2),-7.0),
//   ((1,3),6.0),
//   ((1,4),9.0),
//   ((1,5),7.0),
//   ((1,6),5.0),
//   ((1,7),3.0),
//   ((1,8),3.0),
//   ((1,9),2.0),
//   ((1,10),1.0),
//   ((1,11),0.0),
//   ((9,8),0.0),
//   ((3,6),4.0))
// var top_ten_overall = get_top_ten_neighbors(edges_rating)
// println(top_ten_overall.deep.mkString("\n")) 

//------------------------------------------------------------------------------------------------------------------------
// PERFORMING DEFINED STEPS IN A SEQUENCE
