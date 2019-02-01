#!/bin/bash
#@Author- Amith RC


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
	sudo java -jar $pwdCurrent search $1 $2 --section
}

if [ $# -eq 0 ]
then 
	call_maven 
	change_path_target /home/team1/indexed_file /home/team1/query_data/benchmarkY1/benchmarkY1-train/train.pages.cbor-outlines.cbor
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
