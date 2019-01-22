package ${groupId}.cluster.transformation

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg._
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH

object CalculationService {
  def run(spark: SparkSession, points: DataFrame): DataFrame = {
    points.createOrReplaceTempView("points")

    val groupedDF_ = spark.sql("select t.id, collect_list(t.data) as data from points t group by t.id")

    val v: (Iterable[Any]) => DenseVector = (v) => {
      val t_ = v.toList.head.asInstanceOf[Iterable[Row]]
      val t = t_.flatMap(r => Array(r.getDouble(0) / 0.05, r.getDouble(1) / 0.05)).toArray
      val r = Vectors.dense(t)
      r.toDense
    }

    val vUdf = udf(v, SQLDataTypes.VectorType)

    val preparedDF_ = groupedDF_.select("*").withColumn("data_", vUdf(col("data")))

    val assembler = new VectorAssembler()
      .setInputCols(Array("data_"))
      .setOutputCol("features")

    val preparedDF = assembler.transform(preparedDF_)

    val brp = new BucketedRandomProjectionLSH()
      .setBucketLength(2.0)
      .setNumHashTables(3)
      .setInputCol("features")
      .setOutputCol("hashes")

    val model = brp.fit(preparedDF)

    val r = model.approxSimilarityJoin(preparedDF, preparedDF, 1.5, "distance")
      .select(
        col("datasetA.id").alias("idA"),
        col("datasetB.id").alias("idB"),
        col("distance")
      )

    r.select("idA", "idB", "distance").filter("idA < idB")
  }
}
