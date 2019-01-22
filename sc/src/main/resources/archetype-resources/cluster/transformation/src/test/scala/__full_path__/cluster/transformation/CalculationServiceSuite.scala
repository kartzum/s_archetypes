package ${groupId}.cluster.transformation

import org.scalatest.{Matchers, Outcome}
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types._

class CalculationServiceSuite extends org.scalatest.fixture.FunSuite with Matchers {

  test("test1") {
    spark =>
      val schema = StructType(
        List(
          StructField("id", StringType),
          StructField("data", ArrayType(StructType(List(
            StructField("lat", DoubleType),
            StructField("lon", DoubleType),
            StructField("t", StringType)
          ))))
        )
      )

      val l1 = Seq(
        Row("1", Seq(
          Row(55.797801, 37.521511, "1"),
          Row(55.797801, 37.521511, "2")
        )),
        Row("2", Seq(
          Row(55.803159, 37.517723, "1"),
          Row(55.803314, 37.520968, "2")
        ))
      )

      val points = spark.createDataFrame(spark.sparkContext.parallelize(l1), schema)

      val r = CalculationService.run(spark, points)

      assert(r != null)
  }

  override protected def withFixture(test: OneArgTest): Outcome = {
    val spark: SparkSession =
      SparkSession
        .builder()
        .config("spark.serializer", classOf[KryoSerializer].getName)
        .master("local[4]")
        .getOrCreate()

    try withFixture(test.toNoArgTest(spark))
    finally spark.stop()
  }

  override type FixtureParam = SparkSession

}


