name := "Big_Data[depeche_mode]"

version := "0.1"

scalaVersion := "2.12.8"

val sparkVersion = "2.4.0"
val breezeVersion = "0.13.2"

libraryDependencies ++= Seq(
  "org.scalanlp" %% "breeze" % breezeVersion,

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes. 
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % breezeVersion,

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % breezeVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
)