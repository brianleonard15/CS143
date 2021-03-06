package org.apache.spark.sql.execution;
/**
 * :: DeveloperApi ::
 */
public  class Sample extends org.apache.spark.sql.execution.SparkPlan implements org.apache.spark.sql.execution.UnaryNode, scala.Product, scala.Serializable {
  public  double fraction () { throw new RuntimeException(); }
  public  boolean withReplacement () { throw new RuntimeException(); }
  public  long seed () { throw new RuntimeException(); }
  public  org.apache.spark.sql.execution.SparkPlan child () { throw new RuntimeException(); }
  // not preceding
  public   Sample (double fraction, boolean withReplacement, long seed, org.apache.spark.sql.execution.SparkPlan child) { throw new RuntimeException(); }
  public  scala.collection.Seq<org.apache.spark.sql.catalyst.expressions.Attribute> output () { throw new RuntimeException(); }
  public  org.apache.spark.rdd.RDD<org.apache.spark.sql.catalyst.expressions.Row> execute () { throw new RuntimeException(); }
}
