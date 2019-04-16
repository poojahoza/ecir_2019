'''
@Author Amith RC
@Created March 29th 2019
@Purpose: Takes the feature file and perform the Zscale normalize
'''

import ranklib as rlib
import argparse
import sys


def read_feature_file(filepath):
    fetdict = dict()
    number_of_fet = 0
    with open(filepath, 'r') as f:
        for line in f:
            qid, pid = rlib.get_qid_pid(line)
            score = []
            match = rlib.fetextract.findall(line)
            count = 0
            if match is not None:
                for val in match:
                    count = count + 1
                    score.append(float(val.split(":")[1]))
            number_of_fet = count

            if qid in fetdict:
                inner_dict_extract = fetdict.get(qid)
                inner_dict_extract[pid] = score
            else:
                inner_dict = dict()
                inner_dict[pid] = score
                fetdict[qid] = inner_dict

    return fetdict, number_of_fet


if __name__ == '__main__':
    parser = argparse.ArgumentParser("Takes unnormalized feature file and performs the normalization")
    parser.add_argument("-q", "--qrelpath", help="Path to the Qrel file", required=True)
    parser.add_argument("-f", "--fetpath", help="Path to the Feature file", required=True)
    parser.add_argument("-v", "--verbose", help="Display information on the stdout", action="store_true")
    parser.add_argument("-s", "--suffix", help="Pass a filename suffix")
    parser.add_argument("-r", "--ranklib", help="Path to the RankLib jar")
    parser.add_argument("-n", "--normalizer", help="Perform Z score normalize on the data", action="store_true")

    args = parser.parse_args(args=None if sys.argv[1:] else ['--help'])

    qrel = None
    fet = None
    number_of_fet = 0

    fname = ""
    if args.suffix:
        fname = args.suffix + ".txt"
    else:
        fname = "normalizedfeaturefile.txt"

    if args.qrelpath:
        qrel = rlib.readQrel(args.qrelpath)

    if args.fetpath:
        fet, number_of_fet = read_feature_file(args.fetpath)

    if args.normalizer:
        rowlist = rlib.convert_dict_to_list(fet, qrel)
        df = rlib.create_data_frame(rowlist, number_of_fet)
        rlib.normalize_data_frame(df, number_of_fet)
        if (args.verbose):
            print(df.head())
        rlib.write_feature_file_normalized(df, number_of_fet, fname)

    if args.ranklib:
        rlib.run_rank_lib(args.ranklib, fname)
        out = rlib.get_combined_run_dict("model.txt", fname)
        rlib.create_combined_run_file(out)
