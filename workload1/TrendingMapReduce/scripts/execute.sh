#!/bin/bash

cp MANIFEST.MF ../target/classes
cd ../target/classes


hdfs dfs -rm -r /user/shaktimalhotra/data2
hdfs dfs -rm -r /user/shaktimalhotra/output


jar -cvfm Assignement-1-0.0.1-SNAPSHOT.jar MANIFEST.MF mapreduce/


hadoop jar Assignement-1-0.0.1-SNAPSHOT.jar   /input/ALLvideos.csv /user/shaktimalhotra/output

echo "displaying output 1"
hdfs dfs -get /user/shaktimalhotra/output
echo "printing output"


hadoop fs -getmerge  /user/shaktimalhotra/output/part* finaloutput.csv
#hadoop fs -getmerge  /user/shaktimalhotra/hadoopinitoutput/part* A1-3.1-output.csv

echo "merging done"


