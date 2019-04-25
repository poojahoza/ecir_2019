#!/bin/bash
#@Author- Amith RC

index_path=/home/team1/indexed_file/
qrels_path=/home/team1/benchmarkY1Test-manual-qrels/manual.benchmarkY1test.cbor.hierarchical.qrels
embeddings_path=/home/team1/glove_word_embeddings/glove.6B.50d.txt
outlines_path=/home/team1/query_data/benchmarkY1-test/test.pages.cbor-outlines.cbor
entities_path=/home/team1/entity.lucene/
ham_train=/home/team1/hamTrain
spam_train=/home/team1/spamTrain
hamspam_test=/home/team1/hamSpamTest

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
    java -jar -Xmx20g $pwdCurrent ranker --model-file /home/team1/prototype2/amith_data/model.txt --run-file /home/team1/prototype2/amith_data/output_mrf_ranking.txt
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path --dice-sim --cosine-sim --jaccard-sim --jaro-sim --leven-sim --parallel
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path -we $embeddings_path -dim=50 -k=300 --bias-fact=20 --entity-doc-sim --parallel
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path -we $embeddings_path -dim=50 -k=100 --bias-fact=20 -qe-type entityText --qe-reranking --parallel
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path -we $embeddings_path -dim=50 -k=100 --bias-fact=20 -qe-type entityID  --qe-reranking --parallel
    java -jar -Xmx20g $pwdCurrent search section  -i $index_path -q $outlines_path -we $embeddings_path -dim=50 -k=100 --bias-fact=20 -qe-type entityTextID --qe-reranking --parallel
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
