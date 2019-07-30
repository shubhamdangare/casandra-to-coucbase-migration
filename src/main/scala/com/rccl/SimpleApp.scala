package com.rccl

import java.sql.Timestamp

import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonObject
import com.couchbase.spark._
import org.apache.spark.sql._
import org.apache.spark.sql.types.TimestampType

import scala.reflect.ClassTag

case class Profiles(
                     vds_id: String,
                     address_city: String,
                     bool: Boolean,
                     club_loyality: Int,
                     creation_timestamp: Timestamp
                   )

object SimpleApp {

  def main(args: Array[String]) {

    println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")

    val spark = SparkSession
      .builder()
      .config("spark.cassandra.connection.host", "localhost")
      .config("spark.cassandra.connection.port", "9042")
      .config("spark.cassandra.auth.username", "cassandra")
      .config("spark.cassandra.auth.password", "cassandra")
      .config("spark.couchbase.nodes", "172.17.0.2")
      .config("spark.couchbase.username", "Administrator")
      .config("spark.couchbase.password", "password")
      .config("com.couchbase.bucket.demo", "")
      .getOrCreate()

    import com.couchbase.spark._
    import com.couchbase.spark.sql._
    import org.apache.spark.sql.SparkSession

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


    rddToBeWritten.foreach(row => println(row.fieldIndex("creation_timestamp")))
    println(rddToBeWritten.printSchema())
    println(rddToBeWritten.explain)
    println(rddToBeWritten.show)

    import org.apache.spark.sql.SaveMode._
    rddToBeWritten.write.mode(Overwrite).couchbase()

    /**
      * *
      * implicit def kryoEncoder[JsonDocument](implicit ct: ClassTag[JsonDocument]): Encoder[JsonDocument] =
      *Encoders.kryo[JsonDocument](ct)
      * *
      * def cassandraToCouchbaseMapper(dataFrame: DataFrame, tableName: String): Dataset[JsonDocument] = {
      * println("--------$$$$$$$$$$------------------------------------------------------")
      * val dataset = tableName match {
      * case "profiles" => dataFrame
      * .withColumn("creation_timestamp", col("creation_timestamp").cast(TimestampType))
      * *
      * withColumn("last_updated", col("last_updated").cast(TimestampType))
      * .withColumn("last_updated_ds", col("last_updated_ds").cast(TimestampType))
      * .as[Profiles]
      * .map(profiles => {
      * println ("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
      *
      * val profilesJson: JsonObject = JsonObject
      * .empty()
      * .put("vds_id", profiles.vds_id)
      * .put("address_city", profiles.address_city)
      * .put("creation_timestamp", profiles.creation_timestamp)
      * JsonDocument
      * .create(s"${tableName.toUpperCase}||::||${profiles.vds_id}", profilesJson)
      *
      * })
      *
      * }
      *
      * *
      * dataset
      *
      * }
      *
      * *
      *
      * *
      * cassandraToCouchbaseMapper(cassandraRDD, "profiles").rdd.saveToCouchbase("demo")
      * *
      * dataset
      * .foreach(document => println(document.content()))
      * rddToBeWritten
      * .write.format("com.databricks.spark.csv")
      * .option("header", "true").save("/home/knoldus/Desktop/data.csv")
      * *
      * val listColumn = List("vds_id", "address_city", "bool", "club_loyality", "creation_timestamp", "META_ID")
      *
      *
      * val schema = types.StructType(
      * StructField ("vds_id", StringType) ::
      * StructField ("address_city", StringType) ::
      * StructField ("bool", BooleanType) ::
      * StructField ("club_loyality", IntegerType) ::
      * StructField ("creation_timestamp", TimestampType) :: Nil
      * )
      *
      * val rddToBeWritten =
      * cassandraRDD
      * .withColumn("META_ID",
      * concat (
      * col ("vds_id"), lit(" "),
      * col ("address_city")
      * )
      * )
      * val incrementalDataFrame = listColumn
      * .foldLeft(rddToBeWritten) {
      * (df, columnName) => {
      * df
      * .withColumn(columnName, when(
      * col (columnName).isNotNull,
      * col (columnName)
      * ).otherwise(lit(null)))
      *
      * }
      *
      * }
      * .persist()
      * *
      * println (incrementalDataFrame.printSchema())
      * println (incrementalDataFrame.show())
      * *
      * println (rddToBeWritten.printSchema())
      * println (rddToBeWritten.show())
      *
      * *
      * import com.couchbase.spark.sql._
      *
      * rddToBeWritten
      * .toDF(listColumn: _*).write.mode(Overwrite).couchbase()
      * *
      *
      * val sql = spark.sqlContext
      *
      * val demo = sql.read.couchbase(schemaFilter = EqualTo("type", "address_city"))
      * println ("=============================Reading-Now=============================================")
      * println (demo.printSchema())
      * println (demo.show())
      * *
      *
      *
      * val data = spark.sqlContext.read
      * .format("com.databricks.spark.csv")
      * .option("inferSchema", "true")
      * .option("header", "true")
      * .load("/home/knoldus/Desktop/data.csv")
      * *
      * println (data.printSchema())
      * println (data.show())
      * *
      * data
      * .write.mode(Overwrite).couchbase()
      * *
      * println ("=============================Done-Writing=============================================")
      */
  }

}
