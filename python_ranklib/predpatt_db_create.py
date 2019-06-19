#!/usr/bin/env python


import argparse
from predpatt import PredPatt
from nltk.tokenize import sent_tokenize
from pymongo import MongoClient
from trec_car.read_data import iter_paragraphs, ParaText, Paragraph

client = MongoClient(port=27017)
db = client.predpattannotations
collection = db.predpattann
print(collection)


def get_paragraphs(paragraphs_file):
    with open(paragraphs_file, 'rb') as f:
        for p in iter_paragraphs(f):
            # texts = [elem.text if isinstance(elem, ParaText)
            #      else elem.anchor_text
            #      for elem in p.bodies]
            texts = p.get_text()
            print(texts)
            yield p.para_id+'|__|'+texts


def generatePredPattAnnDB(input_file):
	paras = get_paragraphs(input_file)
	for p in paras:
		lines_split = p.split('|__|')
		print("processing ",lines_split[0])
		sentences = sent_tokenize(lines_split[1])
		final_predpatt_json = []
		for sent in sentences:
			p = PredPatt.from_sentence(sent)
			sent_dict = {}
			sent_dict['text'] = sent
			sent_dict['relations'] = []
			for x in p.instances:
				pred_relation_dict = {}
				pred_relation_dict['predicate'] = {'predicate':x, 'predicate_phrase':x.phrase()}
				pred_relation_dict['arguments'] = []
				for arg in x.arguments:
					arg_json = {'argument':arg, 'argument_phrase': arg.phrase()}
					pred_relation_dict['arguments'].append(arg_json)
				sent_dict['relations'].append(pred_relation_dict)
			final_predpatt_json.append(sent_dict)
		para_detail = {
			'para_id': lines_split[0],
			'para_text': lines_split[1],
			'predpatt_relation': final_predpatt_json
		}
		result = collection.insert_one(para_detail)

if __name__ == "__main__":
	parser = argparse.ArgumentParser("Please enter paragraph corpus file location")
	parser.add_argument('--para', help='paragraph corpus file location')
	args = parser.parse_args()
	generatePredPattAnnDB(args.para)