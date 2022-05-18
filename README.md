# ucp

Use config to build a spark/flink job.

We provide an abstraction called Element and Plugin.

Element is used to record the properties of each node in the fictitious graph, and Plugin generates the Spark/Flink executable based on the information of Element.


# version require

- Spark: 3.1.x
- Flink: Coming 0.3.
- Java: 8+
- Scala: 2.12.x

# Quick start

Preparation
> export SPARK_HOME 
> 
> export HADOOP_CONF_DIR

```shell
cd $UCP_HOME
bin/ucp-example
```


