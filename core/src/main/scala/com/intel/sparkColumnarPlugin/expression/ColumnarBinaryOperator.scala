package com.intel.sparkColumnarPlugin.expression

import com.google.common.collect.Lists

import org.apache.arrow.gandiva.evaluator._
import org.apache.arrow.gandiva.exceptions.GandivaException
import org.apache.arrow.gandiva.expression._
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.expressions._
import org.apache.spark.sql.types._

import scala.collection.mutable.ListBuffer
/**
 * A version of add that supports columnar processing for longs.
 */
class ColumnarAnd(left: Expression, right: Expression, original: Expression)
  extends And(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeAnd(Lists.newArrayList(left_node, right_node))
    (funcNode, resultType)
  }
}

class ColumnarOr(left: Expression, right: Expression, original: Expression)
  extends Or(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeOr(Lists.newArrayList(left_node, right_node))
    (funcNode, resultType)
  }
}

class ColumnarEqualTo(left: Expression, right: Expression, original: Expression)
  extends EqualTo(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "equal", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

class ColumnarEqualNull(left: Expression, right: Expression, original: Expression)
  extends EqualNullSafe(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "equal", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

class ColumnarLessThan(left: Expression, right: Expression, original: Expression)
  extends LessThan(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "less_than", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

class ColumnarLessThanOrEqual(left: Expression, right: Expression, original: Expression)
  extends LessThanOrEqual(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "less_than_or_equal_to", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

class ColumnarGreaterThan(left: Expression, right: Expression, original: Expression)
  extends GreaterThan(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "greater_than", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

class ColumnarGreaterThanOrEqual(left: Expression, right: Expression, original: Expression)
  extends GreaterThanOrEqual(left: Expression, right: Expression) with ColumnarExpression with Logging {
  override def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    val (left_node, left_type): (TreeNode, ArrowType) = left.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)
    val (right_node, right_type): (TreeNode, ArrowType) = right.asInstanceOf[ColumnarExpression].doColumnarCodeGen(fieldTypes)

    val resultType = new ArrowType.Bool()
    val funcNode = TreeBuilder.makeFunction(
      "greater_than_or_equal_to", Lists.newArrayList(left_node, right_node), resultType)
    (funcNode, resultType)
  }
}

object ColumnarBinaryOperator {

  def create(left: Expression, right: Expression, original: Expression): Expression = original match {
    case a: And =>
      new ColumnarAnd(left, right, a)
    case o: Or =>
      new ColumnarOr(left, right, o)
    case e: EqualTo =>
      new ColumnarEqualTo(left, right, e)
    case e: EqualNullSafe =>
      new ColumnarEqualNull(left, right, e)
    case l: LessThan =>
      new ColumnarLessThan(left, right, l)
    case l: LessThanOrEqual =>
      new ColumnarLessThanOrEqual(left, right, l)
    case g: GreaterThan =>
      new ColumnarGreaterThan(left, right, g)
    case g: GreaterThanOrEqual =>
      new ColumnarGreaterThanOrEqual(left, right, g)
    case other =>
      throw new UnsupportedOperationException(s"not currently supported: $other.")
  }
}
