
# sbt-art

Runs [artifact-cli](https://github.com/mogproject/artifact-cli) inside sbt, manages all-in-one jar/zip artifacts in S3.

[![Stories in Ready](https://badge.waffle.io/opt-tech/sbt-art.png?label=ready&title=Ready)](https://waffle.io/opt-tech/sbt-art)
[![Circle CI](https://circleci.com/gh/opt-tech/sbt-art.svg?style=shield)](https://circleci.com/gh/opt-tech/sbt-art)
[![License](https://img.shields.io/badge/license-Apache2-blue.svg)](http://choosealicense.com/licenses/apache-2.0/)


## Installation

### 1. Install artifact-cli

```
pip install artifact-cli
```

*Requires ```Python``` and ```pip```. (may need ```sudo```)*

### 2. Add to your plugins.sbt the line

- project/plugins.sbt

```
addSbtPlugin("jp.ne.opt" % "sbt-art" % "0.1.2")
```

### 3. Write AWS configuration

- project/artifact-cli.conf

```
[default]
aws_access_key_id = XXXXXXXXXXXXXXXXXXXX
aws_secret_access_key = XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
bucket = your-bucket-name
region = your-region (e.g. ap-northeast-1, us-east-1)
```

### 4. Add settings to your project

First, import keys in your ```build.sbt``` or ```project/Build.scala```.

```
import jp.ne.opt.sbtart.SbtArtPlugin.autoImport._
```

#### 4.1 artGroupId

Group ID for artifact-cli. Default is same as your ```organization```.

```
artGroupId in art := "your-group-id"
```

#### 4.2 artTarget

Target file for uploading and showing information.  
If your project is using ```sbt assembly``` or ```play dist``` to publish, you can write the nifty way as follows.

- For [sbt-assembly](https://github.com/sbt/sbt-assembly) project

```
import sbtassembly.AssemblyKeys._

artTarget in art := (assembly in assembly).value
```

> Note:  
> ```artTarget in art := (assemblyOutputPath in assembly).value``` also works.  (without running ```assembly``` task)

- For [play](https://www.playframework.com/) project with universal (zip-file) distribution

```
import com.typesafe.sbt.SbtNativePackager.Universal

artTarget in art := (packageBin in Universal).value
```

#### 4.3 artConfig (optional)

Path to the configuration file for artifact-cli is also customizable. Default is ```project/artifact-cli.conf```.

```
artConfig in art := new File("path/to/your.conf")
```

## Tasks

You can run the following tasks in the sbt project.

| Task | Description | Notes |
|:-----------|:------------|:------------|
| ```art-version```         |Prints the version number of artifact-cli.| |
| ```art-list```            |Lists all the artifacts in the group.| |
| ```art-info [REVISION]``` |Shows the information of the specified artifact.|If ```REVISION``` is not set, prints the latest revision.<br />If ```artTarget``` is not set, this task will be skipped.|
| ```art-upload``` |Uploads the current artifact.|<br />If ```artTarget``` is not set, this task will be skipped.|
