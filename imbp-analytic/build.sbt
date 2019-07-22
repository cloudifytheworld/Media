name := "imbp-analytic"

version := "0.1"

scalaVersion := "2.11.8"

resolvers += Resolver.mavenLocal
//resolvers += "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.3"
libraryDependencies += "datastax" % "spark-cassandra-connector" % "2.4.0-s_2.11"