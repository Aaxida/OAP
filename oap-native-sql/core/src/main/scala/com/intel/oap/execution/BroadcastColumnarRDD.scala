/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.oap.execution

import com.intel.oap.expression.ConverterUtils
import com.intel.oap.vectorized.CloseableColumnBatchIterator
import org.apache.spark._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.execution.metric.SQLMetric
import org.apache.spark.sql.internal.SQLConf
import org.apache.spark.sql.vectorized.ColumnarBatch

private final case class BroadcastColumnarRDDPartition(index: Int) extends Partition

case class BroadcastColumnarRDD(
    @transient private val sc: SparkContext,
    metrics: Map[String, SQLMetric],
    numPartitioning: Int,
    inputByteBuf: broadcast.Broadcast[Array[Array[Byte]]])
    extends RDD[ColumnarBatch](sc, Nil) {

  override protected def getPartitions: Array[Partition] = {
    (0 until numPartitioning).map { index => new BroadcastColumnarRDDPartition(index) }.toArray
  }
  override def compute(split: Partition, context: TaskContext): Iterator[ColumnarBatch] = {
    new CloseableColumnBatchIterator(
      ConverterUtils.convertFromNetty(List[Attribute](), inputByteBuf.value))
  }
}
