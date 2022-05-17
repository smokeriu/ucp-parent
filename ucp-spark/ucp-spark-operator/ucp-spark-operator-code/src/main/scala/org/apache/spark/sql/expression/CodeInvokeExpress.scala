package org.apache.spark.sql.expression

import org.apache.spark.annotation.{Experimental, Unstable}
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.expressions.Expression
import org.apache.spark.sql.catalyst.expressions.codegen.{CodeAndComment, CodeGenerator, CodegenFallback}
import org.apache.spark.sql.catalyst.parser.CatalystSqlParser
import org.apache.spark.sql.types._
import org.apache.spark.unsafe.types.UTF8String

/**
 * Provide CodeInvokeExpress to invoke method by reflect.
 *
 * User should provide code , method info, args.
 *
 * We need use some Spark private method. so move the package to [[org.apache.spark.sql.expression]]
 *
 * @note The inputsType(children) needs to be consistent with Real types in code
 * @param code       invoke code
 * @param methodName invoke method name
 * @param children   args
 * @author ssiu
 */
@Unstable
@Experimental
case class CodeInvokeExpress(code: String,
                             methodName: String,
                             returnType: String,
                             children: Seq[Expression])
  extends CodegenFallback with Logging {

  override def nullable: Boolean = true

  @Unstable
  override def eval(input: InternalRow): Any = {
    var i = 0
    while (i < argExpr.length) {
      buffer(i) = argExpr(i).eval(input)
      // If we support timestamps, dates, decimals, arrays, or maps in the future,
      // proper conversion needs to happen here too.
      if (buffer(i).isInstanceOf[UTF8String]) {
        buffer(i) = buffer(i).toString
      }
      i += 1
    }
    val res = obj._1.generate(buffer)
    CodeInvokeExpress.returnTypeConvert(res, returnDataType)
  }

  override def dataType: DataType = returnDataType

  private lazy val returnDataType: DataType = CatalystSqlParser.parseDataType(returnType)

  /** A temporary buffer used to hold intermediate results returned by children. */
  @transient private lazy val buffer = new Array[Any](argExpr.length)

  @transient private lazy val argExpr: Array[Expression] = children.toArray

  /**
   * Use [[org.apache.spark.sql.catalyst.expressions.codegen.CodeGenerator]] to simplify our work and reduce dependencies introduced
   */
  @transient private lazy val obj = {
    // TODO: how to use comment?
    val inputTypes = argExpr.map(expression => expression.dataType)
    val generatorCode = CodeInvokeExpress.generateCode(code, methodName, inputTypes)
    val codeAndComment = new CodeAndComment(generatorCode, Map.empty[String, String])
    logDebug(s"Get instance of code $generatorCode")
    // If it has already been compiled on the executor, this is taken directly from the cache
    CodeGenerator.compile(codeAndComment)
  }


}

object CodeInvokeExpress extends Logging {
  /**
   * Now. only allow java method.
   */
  private def generateCode(code: String, methodName: String, inputTypes: Seq[DataType]) = {
    s"""public Object generate(Object[] references) {
       |  return $methodName(${ObjectToType(inputTypes)});
       |}
       |$code
       |""".stripMargin
  }

  /**
   * Now. only support basic type.
   *
   * TODO: timestamp..
   */
  private def ObjectToType(inputTypes: Seq[DataType]) = {
    inputTypes.indices.map(idx => {
      val convert = inputTypes(idx) match {
        case IntegerType => "(Integer)"
        case StringType => "(String)"
        case DoubleType => "(Double)"
        case LongType => "(Long)"
        case _ => "(String)"
      }
      s"$convert references[$idx]"
    }).mkString(",")
  }

  /**
   * Some type should convert to a spark wrapper type.
   *
   * eg: String should convert to [[org.apache.spark.unsafe.types.UTF8String]]
   *
   * @param value          return value
   * @param returnDataType return type
   * @return (convert) res.
   */
  private def returnTypeConvert(value: Any, returnDataType: DataType): Any = {
    returnDataType match {
      case StringType =>
        val str = value.asInstanceOf[String]
        UTF8String.fromString(str)
      // TODO: More types will be added as they are tested
      case _ => value
    }
  }

}