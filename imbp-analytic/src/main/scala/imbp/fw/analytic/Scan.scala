package imbp.fw.analytic

import org.apache.spark.sql.SparkSession

/**
  *
  * @author Charles(Li) Cai
  * @date 7/21/2019   
  */
object Scan {

  def main(args: Array[String]): Unit = {


    val spark = SparkSession.builder().appName("aoi").master("local[*]")
      .config("spark.cassandra.connection.host", "localhost")
      .config("spark.cassandra.connection.port", 9042)
      .getOrCreate();

    val df = spark.read.format("org.apache.spark.sql.cassandra")
      .options(Map("keyspace" -> "images", "table" -> "aoi_single_component_image_1"))
      .load();

    df.select("*").where("created_day=20181112")
      .rdd.coalesce(1, true).saveAsTextFile("d:/tmp/20181112.txt")

    spark.close()
    spark.stop()
  }
}
