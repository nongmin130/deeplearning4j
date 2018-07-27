---
title: DataVec Operations
short_title: Operations
description: Implementations for advanced transformation.
category: DataVec
weight: 3
---

## Usage

Operations, such as a `Function`, help execute transforms and load data into DataVec. The concept of operations is low-level, meaning that most of the time you will not need to worry about them.

## Loading data into Spark

If you're using Apache Spark, functions will iterate over the dataset and load it into a Spark `RDD` and convert the raw data format into a `Writable`.

```java
import org.datavec.api.writable.Writable;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.spark.transform.misc.StringToWritablesFunction;

SparkConf conf = new SparkConf();
JavaSparkContext sc = new JavaSparkContext(conf)

String customerInfoPath = new ClassPathResource("CustomerInfo.csv").getFile().getPath();
JavaRDD<List<Writable>> customerInfo = sc.textFile(customerInfoPath).map(new StringToWritablesFunction(rr));
```

The above code loads a CSV file into a 2D java RDD. Once your RDD is loaded, you can transform it, perform joins and use reducers to wrangle the data any way you want.

## Available ops

{{autogenerated}}