Vosk语音识别引擎集成说明
========================

由于Vosk库在Maven中央仓库中不可用，需要手动安装依赖。

步骤1: 下载Vosk JAR文件
----------------------
请从以下链接下载Vosk JAR文件：
https://github.com/alphacep/vosk-api/releases/download/v0.3.47/vosk-0.3.47.jar

将下载的文件保存到项目目录的lib文件夹中：
hr_ai_project/lib/vosk-0.3.47.jar

步骤2: 安装到本地Maven仓库
------------------------
打开命令行，进入项目目录，执行以下命令：

mvn install:install-file -Dfile=lib/vosk-0.3.47.jar -DgroupId=com.alphacephei -DartifactId=vosk -Dversion=0.3.47 -Dpackaging=jar

步骤3: 重新构建项目
------------------
执行以下命令重新构建项目：

mvn clean install

步骤4: 启动应用
-------------
执行以下命令启动应用：

mvn spring-boot:run

注意事项：
1. 确保Vosk模型文件已正确放置在:
   hr_ai_project/src/main/resources/Models/vosk-model-small-cn-0.22/

2. 如果遇到网络问题无法下载JAR文件，可以尝试使用其他版本或从其他来源获取。