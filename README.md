# Big_Data_depeche_mode [node2vec model]
Epinions.com was an online marketplace, where users posted customer reviews for the goods. When reading each other’s comments they were able to “trust” each other. It could be represented as a social graph, where the node is a user and oriented edge is a “trust”. In this project we would like to create a recommendation system on this graph. For each node, we will recommend 10 other users to “trust”.

node2vec model could be used to estimate the probability of any edge in the graph. As recommendations for the node, we will take 10 most probable edges, not presented in the graph. One of the methods to estimate ranged recommendation is Mean Average Precision (MAP).

# Task :

1. Implement and train a simplified node2vec model
2. For each node in the graph recommend top 10 edges.
3. Measure recommendations quality using MAP
4. Make a report

* Implementation in Spark using Scala.

# Evaluation
For every node in the graph, rate possible neighbours
Sort candidates by predicted rating in the descending order
Filter out nodes with existing edges from the recommendaiton
Get top 10 recommendations and evaluate them using MAP

# Team Members 
