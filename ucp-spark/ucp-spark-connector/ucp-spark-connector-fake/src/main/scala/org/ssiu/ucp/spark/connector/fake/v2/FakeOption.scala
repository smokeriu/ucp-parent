package org.ssiu.ucp.spark.connector.fake.v2

import org.apache.spark.sql.util.CaseInsensitiveStringMap

object FakeOption {
  // common
  final val INT_MAX = "intMax"
  private final val INT_MAX_DEFAULT = 1000

  final val STR_MAX_LEN = "strMaxLen"
  private final val STR_MAX_LEN_DEFAULT = 20

  // batch
  final val RECORD_PER_BATCH = "maxRecord"
  final val RECORD_PER_BATCH_DEFAULT = 10000

  // stream


  // apply
  def apply(option: CaseInsensitiveStringMap): FakeOption = {
    val intMax = option.getInt(INT_MAX, INT_MAX_DEFAULT)
    val strMaxLen = option.getInt(STR_MAX_LEN, STR_MAX_LEN_DEFAULT)
    val maxRecord = option.getLong(RECORD_PER_BATCH, RECORD_PER_BATCH_DEFAULT)
    FakeOption(intMax, strMaxLen, maxRecord)
  }
}

case class FakeOption private(intMax: Int, strMaxLen: Int, maxRecord: Long)