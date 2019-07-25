package com.rccl

import java.util.UUID
import org.apache.spark.sql.SparkSession
import com.couchbase.spark._
import com.couchbase.spark.sql._

object SimpleApp {

  def main(args: Array[String]) {

    val spark = SparkSession
      .builder()
      .config("spark.cassandra.connection.host", "localhost")
      .config("spark.cassandra.connection.port", "9042")
      .config("spark.cassandra.auth.username", "cassandra")
      .config("spark.cassandra.auth.password", "cassandra")
      .config("spark.couchbase.nodes", "127.0.0.1")
      .config("spark.couchbase.username", "Administrator")
      .config("spark.couchbase.password", "password")
      .config("com.couchbase.bucket.demo", "")
      .getOrCreate()


    val cassandraRDD = spark
      .read
      .format("org.apache.spark.sql.cassandra")
      .options(Map("table" -> "data", "keyspace" -> "excelsior"))
      .load()
    //.select("country_code", "number")
    //.filter("country_code > 0")

    println("==========================================================================")

    //println(cassandraRDD.printSchema())
    //println(cassandraRDD.explain)
    //println(cassandraRDD.show)

    import org.apache.spark.sql.functions._
    val uuidUDF = udf(UUID.randomUUID().toString)
    val rddToBeWritten = if (cassandraRDD.columns.contains("id")) {
      cassandraRDD.withColumn("META_ID", cassandraRDD("id"))
    } else {
      cassandraRDD.withColumn("META_ID", uuidUDF())
    }

    rddToBeWritten.write.couchbase()

    println("=============================DONE=Writting=============================================")
  }

}
