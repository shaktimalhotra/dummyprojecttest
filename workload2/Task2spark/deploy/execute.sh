#!/bin/bash 

echo "cleanup "
rm -r output 

echo "Exceuting in yarn cluster "
## to execute in yarn 
spark-submit  --deploy-mode client  --executor-cores 4  --num-executors 8  /Users/shaktimalhotra/eclipse-workspace/Task3.2-spark/target/Assignment1-Correlation.jar   /Users/shaktimalhotra/eclipse-workspace/Task3.2-spark/in/ALLvideos.csv output yarn
 
      
     

