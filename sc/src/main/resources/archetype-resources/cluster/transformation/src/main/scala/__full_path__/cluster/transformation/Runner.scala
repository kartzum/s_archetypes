package ${groupId}.cluster.transformation

import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.SparkSession

object Runner {
  def main(args: Array[String]): Unit = {

    val runDate = args(0)
    val dataSource = args(1)

    val spark = SparkSession
      .builder()
      .enableHiveSupport()
      .config("hive.exec.local.scratchdir", "/tmp/hive/scratchdirectory")
      .config("spark.serializer", classOf[KryoSerializer].getName)
      .appName("_")
      .getOrCreate()

    try {
      CalculationService.run(spark, spark.sql(s"select * from $dataSource"))
    }
    finally {
      spark.stop()
    }
  }
}
