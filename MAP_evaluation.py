import sys
import ast
import numpy as np
import pandas as pd
test_epin_path = input("\nPlease enter test_epin path: ")
node_n_top_neighbors_path = input("Please enter node_n_top_neighbors path: ")

with open(node_n_top_neighbors_path, 'r') as node_n_top_neighbors_file:
    node_n_top_neighbors_read = node_n_top_neighbors_file.read()[:-1].split('\n')
    node_n_top_neighbors_ = map(
        lambda node_n_top_neighbors:
        node_n_top_neighbors.split(', '),
        node_n_top_neighbors_read
    )
    nodes_top_neighbors = pd.DataFrame(node_n_top_neighbors_, columns=['source_node', 'top_ten_recomendations'])
    nodes_top_neighbors['source_node'] = nodes_top_neighbors['source_node'].astype(int)
    nodes_top_neighbors['top_ten_recomendations'] = nodes_top_neighbors['top_ten_recomendations'].apply(
        lambda top_ten_recomendations:
        list(map(int, top_ten_recomendations.split(' ')))
    )

test_epin = pd.read_csv(test_epin_path)

test_epin_in_node_to_top_ten_format_s_to_d = map(
    lambda group:
    (group[0], group[1]['destination_node'].unique()),
    test_epin.groupby('source_node')
)
test_epin_in_node_to_top_ten_format_d_to_s = map(
    lambda group:
    (group[0], group[1]['source_node'].unique()),
    test_epin.groupby('destination_node')
)

columns = ['source_node', 'top_ten_recomendations']
test_epin_neighbors_s_to_d = pd.DataFrame(test_epin_in_node_to_top_ten_format_s_to_d, columns=columns)
test_epin_neighbors_d_to_s = pd.DataFrame(test_epin_in_node_to_top_ten_format_d_to_s, columns=columns)

test_epin_neighbors = map(
    lambda group:
    (group[0], np.concatenate(group[1]['top_ten_recomendations'].values)),
    pd.concat([test_epin_neighbors_s_to_d, test_epin_neighbors_d_to_s]).groupby('source_node')
)
test_epin_neighbors_df = pd.DataFrame(test_epin_neighbors, columns=columns)

neighbors_merged = pd.merge(nodes_top_neighbors, test_epin_neighbors_df,
        left_on='source_node', right_on='source_node',
         how='inner',  suffixes=('_predicted', '_actual'))
neighbors_merged['GTP'] = neighbors_merged['top_ten_recomendations_actual'].apply(lambda x: min(10, len(x)))
neighbors_merged['average_precision'] = 0
for n in range(1,11,1):
    #column_name  = 'top_'+str(n)+'_common'
    neighbors_merged['average_precision'] += neighbors_merged.apply(
        lambda row:
        len(set(row['top_ten_recomendations_predicted'][:n]).intersection(set(row['top_ten_recomendations_actual']))) / row['GTP']
        if (row['top_ten_recomendations_predicted'][n-1] in row['top_ten_recomendations_actual'])
        else 0,
        axis=1
    )

MAP = neighbors_merged['average_precision'].mean()
# print to console 
sys.stdout.write('\nMAP on test_epin is: {}\n'.format(MAP))