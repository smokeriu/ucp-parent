package org.apache.spark.sql.expression

import org.apache.spark.annotation.Unstable
import org.apache.spark.sql.Column
import org.apache.spark.sql.catalyst.expressions.Expression

/**
 * Use to convert between [[]] and [[]]
 */
@Unstable
object CodeInvokeColumn {
  def codeInvoke(code: String, methodName: String, returnType: String, inputs: Column*): Column = {
    val args: Seq[Expression] = inputs.map(col => col.expr)
    org.apache.spark.sql.Column(CodeInvokeExpress(code, methodName, returnType, args))
  }

  def apply(code: String, methodName: String, returnType: String, inputs: Column*): Column = {
    codeInvoke(code, methodName, returnType, inputs: _*)
  }
}
