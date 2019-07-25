package com.rccl


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
      .options(Map("table" -> "profile", "keyspace" -> "excelsior"))
      .load()

    println(cassandraRDD.printSchema())
    println(cassandraRDD.explain)
    println(cassandraRDD.show)

    import org.apache.spark.sql.functions.{col, _}

    val rddToBeWritten = cassandraRDD.withColumn("META_ID", concat(col("vds_id"), lit(" "), col("address_city")))

    rddToBeWritten.write.couchbase()
    println("=============================DONE=Writting=============================================")
  }

}
