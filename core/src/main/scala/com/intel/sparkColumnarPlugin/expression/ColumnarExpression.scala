package com.intel.sparkColumnarPlugin.expression

import org.apache.arrow.gandiva.evaluator._
import org.apache.arrow.gandiva.exceptions.GandivaException
import org.apache.arrow.gandiva.expression._
import org.apache.arrow.vector.types.pojo.ArrowType
import org.apache.arrow.vector.types.pojo.Field

import scala.collection.mutable.ListBuffer

trait ColumnarExpression {

  def doColumnarCodeGen(fieldTypes: List[Field]): (TreeNode, ArrowType) = {
    throw new UnsupportedOperationException(s"Not support doColumnarCodeGen.")
  }
}

