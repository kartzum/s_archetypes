# s_archetypes
Contains archetypes for spark and so on

## sc

This on contains archetype for generate spark + scala + maven project for running on cluster.

### How to create project

archetypeVersion - version of archetype
groupId - group Id
artifactId - artifact Id
version - version of project
full_path - path to sources

```
mvn archetype:generate -DarchetypeGroupId=com.m.s.archetypes -DarchetypeArtifactId=sc -DarchetypeVersion=0.0.1-SNAPSHOT -DgroupId=com.m.bd_2_1 -DartifactId=bd_2_1 -Dversion=0.0.1-SNAPSHOT -Dfull_path="com/m/bd_2_1/" -DinteractiveMode=false
```

### How to run

Build project.

```
mvn clean install
```

Prepare configuration for running on cluster.

See:

/assembly/flows/scripts/spaces/local/run.sh

```
if [ -z ${M_ENV_CONF_SOURCE} ];
then
 source "_"
else
 M_ENV_CONF_SOURCE_INNER=${M_ENV_CONF_SOURCE}
 source ${M_ENV_CONF_SOURCE_INNER}
fi
```

For example:

```
if [ -z ${M_ENV_CONF_SOURCE} ];
then
 source "some_root_path/some_config/config.sh"
else
 M_ENV_CONF_SOURCE_INNER=${M_ENV_CONF_SOURCE}
 source ${M_ENV_CONF_SOURCE_INNER}
fi
```

config.sh

```
export HADOOP_USER_CLASSPATH_FIRST=true
export HADOOP_CONF_DIR=$M_HADOOP_CONF_DIR
export HADOOP_COMMON_HOME=$M_HADOOP_COMMON_HOME
export HADOOP_MAPRED_HOME=$M_HADOOP_MAPRED_HOME
export HADOOP_HOME=$M_HADOOP_HOME
export SQOOP_HOME=$M_SQOOP_HOME
export SPARK_HOME=$M_SPARK_HOME
export HIVE=$M_HIVE
export HBASE=$M_HBASE
export JAVA_HOME=$M_JAVA_HOME
export SPARK_CONF_DIR=$M_SPARK_CONF_DIR

export PATH=$HADOOP_COMMON_HOME/bin/:$SQOOP_HOME/bin/:$SPARK_HOME/bin/:$HIVE/bin:$HBASE/bin:$PATH
```

Also (in HADOOP_CONF_DIR files from cluster):
core-site.xml
hdfs-site.xml
hive-site.xml
mapred-site.xml
ssl-client.xml
yarn-site.xml

Run (from target of assembly).

run.sh
