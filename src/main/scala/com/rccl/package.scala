package org.apache.spark.sql

/**
  * Catalyst is a library for manipulating relational query plans.  All classes in catalyst are
  * considered an internal API to Spark SQL and are subject to change between minor releases.
  */
package object catalyst {

  /**
    * A JVM-global lock that should be used to prevent thread safety issues when using things in
    * scala.reflect.*.  Note that Scala Reflection API is made thread-safe in 2.11, but not yet for
    * 2.10.* builds.  See SI-6240 for more details.
    */
  protected[sql] object ScalaReflectionLock

}