package org.ssiu.ucp.spark.connector.jdbc.v2

import java.sql.{Connection, PreparedStatement}

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.write.{DataWriter, LogicalWriteInfo, PhysicalWriteInfo, WriteBuilder, WriterCommitMessage}
import org.apache.spark.sql.connector.write.streaming.{StreamingDataWriterFactory, StreamingWrite}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap
import org.slf4j.LoggerFactory
import org.ssiu.ucp.spark.connector.jdbc.{JdbcConfig, JdbcUtil}

// batch is implement by spark itself
class JdbcWriteBuilder(info: LogicalWriteInfo) extends WriteBuilder {

  override def buildForStreaming(): StreamingWrite = {
    new JdbcStreamingWrite(info.schema(), info.options())
  }
}

class JdbcStreamingWrite(schema: StructType, option: CaseInsensitiveStringMap) extends StreamingWrite {

  private final val LOG = LoggerFactory.getLogger(classOf[JdbcStreamingWrite])

  override def createStreamingWriterFactory(info: PhysicalWriteInfo): StreamingDataWriterFactory = {
    new JdbcStreamingDataWriterFactory(schema, JdbcConfig(option))
  }

  override def commit(epochId: Long, messages: Array[WriterCommitMessage]): Unit = {
    // TODO: impl it later
    LOG.warn("commit in JdbcStreamingWrite")
  }

  override def abort(epochId: Long, messages: Array[WriterCommitMessage]): Unit = {
    // TODO: impl it later
    LOG.error("abort in JdbcStreamingWrite")
  }
}

class JdbcStreamingDataWriterFactory(schema: StructType, jdbcConfig: JdbcConfig) extends StreamingDataWriterFactory {
  override def createWriter(partitionId: Int, taskId: Long, epochId: Long): DataWriter[InternalRow] = {
    new JdbcDataWriter(partitionId, taskId, schema, jdbcConfig)
  }
}

class JdbcDataWriter(partitionId: Int, taskId: Long, schema: StructType, jdbcConfig: JdbcConfig) extends DataWriter[InternalRow] {

  private final val LOG = LoggerFactory.getLogger(classOf[JdbcDataWriter])

  open(schema, jdbcConfig)

  var conn: Connection = _

  var pstm: PreparedStatement = _

  private def open(schema: StructType, jdbcConfig: JdbcConfig): Unit = {
    conn = jdbcConfig.getConnection
    conn.setAutoCommit(false)
    val insertQuery = JdbcUtil.createInsertQuery(schema, jdbcConfig)
    LOG.info(s"insertQuery: $insertQuery")
    pstm = conn.prepareStatement(insertQuery)
  }

  override def write(record: InternalRow): Unit = {
    JdbcUtil.cacheRow(pstm, schema, record)
  }

  override def commit(): WriterCommitMessage = {
    pstm.executeBatch()
    conn.commit()
    JdbcWriterCommitMessage()
  }

  override def abort(): Unit = {
    // TODO: impl it later
    LOG.error("Abort in JdbcDataWriter")
  }

  override def close(): Unit = {
    // TODO: impl it later
    try {
      conn.commit()
      pstm.close()
      conn.close()
    } catch {
      case e: Exception => LOG.error("Exception in close JdbcDataWriter", e)
    }
  }
}

case class JdbcWriterCommitMessage() extends WriterCommitMessage