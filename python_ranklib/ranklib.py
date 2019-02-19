#! /usr/bin/evn/python3

import sys
import argparse


from collections import defaultdict
from os import listdir
from os.path import join


def process_run_file(run_file_path):
    print("process run file")
    run_dict = dict()
    counter = 0
    for file in listdir(run_file_path):
        with open(join(run_file_path, file), 'r') as run_file:
            for line in run_file:
                line_split = line.strip('\n').split()
                if line_split[0] in run_dict:
                    if line_split[2] in run_dict[line_split[0]]:
                        score = run_dict[line_split[0]][line_split[2]]
                        score.append(float(line_split[4]))
                        run_dict[line_split[0]][line_split[2]] = score
                    else:
                        score = []
                        if counter != 0:
                            for i in range(counter):
                                score.append(0.0)
                        score.append(float(line_split[4]))
                        run_dict[line_split[0]][line_split[2]] = score
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
    print(qrel_dict)
    return qrel_dict


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide qrel file and the run files directory")
    parser.add_argument('--q', help='qrel file location')
    parser.add_argument('--r', help='run file directory location')
    args = parser.parse_args()
    print(args)
    qrel_result = process_qrel_file(args.q)
    run_result = process_run_file(args.r)
