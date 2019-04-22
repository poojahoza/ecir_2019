#! /usr/bin/evn/python3

import sys
import argparse


from collections import defaultdict
from os import listdir
from os.path import join

import numpy as np
from scipy import stats

def calculate_zscore(feature_dict, number_features):
    """Calculates the zscore of every feature
    
    Args:
        feature_dict (Dictionary): the dictionary with every feature value
    """
    for q in feature_dict:
        for f in feature_dict[q]:
            print(q, f, len(feature_dict[q][f]), feature_dict[q][f][0], feature_dict[q][f][1])

    # print(list(map(lambda x:x, [feature_dict[q][f][0] for q in feature_dict for f in feature_dict[q]] )))
    # print(stats.zscore(np.asarray(list(map(lambda x: x[0], [feature_dict[q][f] for q in feature_dict for f in feature_dict[q]])))).tolist())
    #print(map(lambda x:x[0], feature_dict[q][id] for id in feature_dict[q] for q in feature_dict))
    # print(lambda x:print(x[1]), [feature_dict[q][f] for q in feature_dict for f in feature_dict[q]])
    # for i in range(1, 2):
    #     print("calculating z score for feature number : ", i)
    #     zscored_feature = stats.zscore(np.asarray(list(map(lambda x: x[i], [feature_dict[q][f] for q in feature_dict for f in feature_dict[q]])))).tolist()



def process_run_file(run_file_path):
    """
    process_run_file accepts a directory path and iterates through every file present in the
    directory. The format of the run_file should be :
                            query_id para_id/entity_id f1_value fx_value
    The output dict would look as follows:
    {
        {query_id1:{entity_id1: [f1_val, f2_val, f3_val]},
            {entity_id2: [f1_val, f2_val, f3_val]}
        },
        {query_id2: {entity_id1: [f1_val, f2_val, f3_val]},
            {entity_id2: [f1_val, f2_val, f3_val]}}
    }

    @params:
    run_file_path: the directory path to the run files
    run_dict: the output dictionary
    """
    print("process run file")
    run_dict = dict()
    counter = 0
    for file in listdir(run_file_path):
        with open(join(run_file_path, file), 'r') as run_file:
            for line in run_file:
                line_split = line.strip('\n').split()
                if len(line_split) < 3:
                    print(
                        "The correct format of run file is query_id entity_id|para_id fx_value....")
                    exit(-1)
                if line_split[0] in run_dict:  # if query_id is present in the dictionary
                    # if entity_id|para_id is present in the dictionary
                    if line_split[1] in run_dict[line_split[0]]:
                        score = run_dict[line_split[0]][line_split[2]]
                        # add every feature value in array[] as value to entity
                        for l in range(2, len(line_split)):
                            score.append(float(line_split[l]))
                        run_dict[line_split[0]][line_split[2]] = score
                    else:
                        score = []
                        if counter != 0:
                            for i in range(counter):
                                score.append(0.0)
                        for l in range(2, len(line_split)):
                            score.append(float(line_split[l]))
                        run_dict[line_split[0]][line_split[1]] = score
                else:
                    run_dict[line_split[0]] = dict()
            counter += 1
    # print(run_dict)
    return run_dict


def process_qrel_file(qrel_file_path):
    print("process qrel file")
    qrel_dict = defaultdict(list)
    with open(qrel_file_path, 'r') as qrel_file:
        for line in qrel_file:
            line_split = line.strip('\n').split()
            qrel_dict[line_split[0]].append(line_split[2])
    #print(qrel_dict)
    return qrel_dict


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        "Please provide qrel file and the run files directory")
    parser.add_argument('--q', help='qrel file location')
    parser.add_argument('--r', help='run file directory location')
    parser.add_argument('--n', type=int, help='total number of features')
    parser.add_argument(
        '-z', "--zscore", help='normalize the feature vectors with zscore', action="store_true")
    args = parser.parse_args()
    print(args)
    qrel_result = process_qrel_file(args.q)
    run_result = process_run_file(args.r)
    number_features = 1
    if args.n:
        number_features = args.n
    print(number_features)
    if args.zscore:
        calculate_zscore(run_result, number_features)
