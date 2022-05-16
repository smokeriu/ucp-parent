package org.ssiu.ucp.spark.connector.fake.v2

import org.apache.spark.sql.util.CaseInsensitiveStringMap

object FakeOption {
  // common
  final val NUMBER_MAX = "numberMax"
  final val NUMBER_MAX_DEFAULT = 1000

  final val STR_MAX_LEN = "strMaxLen"
  final val STR_MAX_LEN_DEFAULT = 20

  // batch
  final val SPEED = "maxRecord"
  final val SPEED_DEFAULT = 10000

  // stream


  // apply
  def apply(option: CaseInsensitiveStringMap): FakeOption = {
    val intMax = option.getInt(NUMBER_MAX, NUMBER_MAX_DEFAULT)
    val strMaxLen = option.getInt(STR_MAX_LEN, STR_MAX_LEN_DEFAULT)
    val maxRecord = option.getLong(SPEED, SPEED_DEFAULT)
    FakeOption(intMax, strMaxLen, maxRecord)
  }
}

case class FakeOption private(intMax: Int, strMaxLen: Int, recordPer: Long)