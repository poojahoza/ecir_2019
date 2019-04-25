#!/bin/bash
#@Author- Amith RC

index_path=/home/team1/indexed_file/
qrels_path=/home/team1/query_data/benchmarkY1-test/test.pages.cbor-hierarchical.qrels
article_path=/home/team1/query_data/benchmarkY1-test/test.pages.cbor-article.qrels
embeddings_path=/home/team1/glove_word_embeddings/glove.6B.300d.txt
outlines_path=/home/team1/query_data/benchmarkY1-test/test.pages.cbor-outlines.cbor
entities_path=/home/team1/entity.lucene/
ham_train=hamTrain
spam_train=spamTrain


function usage()
{
	echo "Argument1=Absolute path to the Indexed_directory"
	echo "Argument2=Absolute path to the outline cbor"
	echo ""
	exit
}

function call_maven()
{
echo "Executing the Maven for the dependency"
mvn clean compile
mvn package
sleep 4
}

function change_path_target()
{
	echo "************CS853-Team1***********************************"
	echo ""
	echo "Index Location=$1"
	echo "Outline Cbor location=$2"
	echo ""
	echo "**********************************************************"
	pwdCurrent=$(pwd)
	pwdCurrent=$pwdCurrent/target/cs953-team1-1.0-SNAPSHOT-jar-with-dependencies.jar
	echo "Running the BM25 method for article.."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -bm25
    echo "Running the Document Frequency query expansion.."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -we $embeddings_path -dim=300 --prf-val-term 200 --prf-val-k 200 --prf-val 5 --qe-exp-df --parallel
    echo "Running the Inverse Document Frequency query expansion..."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -we $embeddings_path -dim=300 --prf-val-term 200 --prf-val-k 200 --prf-val 5 --qe-exp-idf --parallel
    echo "Running the Entity Abstract query expansion with DF and IDF ..."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -we $embeddings_path -dim=300 --prf-val-term 200 --prf-val-k 200 --prf-val 5 --qe-exp-entity --parallel
    echo "Running the Relevance model 3 ..."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -we $embeddings_path -dim=300 --prf-val-term 200 --prf-val-k 200 --prf-val 5 --qe-exp-rm3 --parallel
    echo "...."
    echo "Running some methods with Spam filter enabled"

    echo "Running the BM25 method for article with spamilter enabled.."
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -bm25 --spam-filter2 --spam-loc spamTrain --ham-loc hamTrain
    echo "Running the Document Frequency query expansion with spam filer enabled"
    java -jar -Xmx20g $pwdCurrent search article  -i $index_path -q $outlines_path -we $embeddings_path -dim=300 --prf-val-term 200 --prf-val-k 200 --prf-val 5 --qe-exp-df --parallel --spam-filter2 --spam-loc spamTrain --ham-loc hamTrain

    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path --entity-ranklib -f /home/team1/prototype3/pooja_data/output_ranking_feature_vector_section_test_zscored_python.txt -model /home/team1/prototype3/pooja_data/momodel_section_train_prototyp3.txt
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path --entity-centroid -f /home/team1/prototype3/pooja_data/output_ranking_feature_vector_section_test_python.txt -model /home/team1/prototype3/pooja_data/momodel_section_train_prototyp3.txt
}

if [ $# -eq 0 ]
then 
	call_maven 
	change_path_target index_path outlines_path 
elif [ $# -ne 2 ]
then 
	usage
else

	echo " "
	echo "************CS953-Team1**************************"
	echo "Index Location=$1"
	echo "Outline file=$2"
	echo " "
	call_maven
	change_path_target $1 $2
fi
