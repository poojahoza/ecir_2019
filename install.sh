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
sudo mvn clean compile
sudo mvn package
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
        java -jar $pwdCurrent search -i $index_path -q $outlines_path --dice-sim --cosine-sim --jaccard-sim --jaro-sim 
        java -jar $pwdCurrent search -i $index_path -q $outlines_path --rerank -we $embeddings_path -dim=50 -k=300 --bias-fact=20
        java -jar $pwdCurrent search -i $index_path -q $outlines_path --rerank-idf -we $embeddings_path -dim=50 -k=300 --bias-fact=20
        java -jar $pwdCurrent search -i $index_path -q $outlines_path --rerank-df -we $embeddings_path -dim=50 -k=300 --bias-fact=20
        java -jar $pwdCurrent search --entity-degree -i $index_path -q $outlines_path --entity-index $entities_path 
        java -jar $pwdCurrent search --entity-expand -i $index_path -q $outlines_path --entity-index $entities_path 
        #java -jar $pwdCurrent filter -i $index_path $spam_train $ham_train $hamspam_test
        java -jar $pwdCurrent search --entity-sim -i $index_path -q $outlines_path --entity-index $entities_path -we $embeddings_path -dim=50 
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
