package com.example.hrai;

import java.io.*;
import java.net.*;

public class VoskDownloader {
    public static void main(String[] args) {
        System.out.println("Vosk语音识别引擎集成说明");
        System.out.println("========================");

        System.out.println("\n由于Vosk库在Maven中央仓库中不可用，需要手动获取依赖。");

        System.out.println("\n步骤1: 下载Vosk JAR文件");
        System.out.println("----------------------");
        System.out.println("请从以下链接之一下载Vosk JAR文件：");
        System.out.println("1. https://github.com/alphacep/vosk-api/releases");
        System.out.println("2. https://search.maven.org/search?q=vosk");
        System.out.println("3. https://mvnrepository.com/artifact/com.alphacephei/vosk");

        System.out.println("\n下载后将文件保存到项目目录的lib文件夹中：");
        System.out.println("hr_ai_project/lib/vosk-0.3.47.jar");

        System.out.println("\n步骤2: 安装到本地Maven仓库");
        System.out.println("------------------------");
        System.out.println("打开命令行，进入项目目录，执行以下命令：");
        System.out.println("mvn install:install-file -Dfile=lib/vosk-0.3.47.jar -DgroupId=com.alphacephei -DartifactId=vosk -Dversion=0.3.47 -Dpackaging=jar");

        System.out.println("\n步骤3: 重新构建项目");
        System.out.println("------------------");
        System.out.println("执行以下命令重新构建项目：");
        System.out.println("mvn clean install");

        System.out.println("\n步骤4: 启动应用");
        System.out.println("-------------");
        System.out.println("执行以下命令启动应用：");
        System.out.println("mvn spring-boot:run");

        System.out.println("\n注意事项：");
        System.out.println("1. 确保Vosk模型文件已正确放置在:");
        System.out.println("   hr_ai_project/src/main/resources/Models/vosk-model-small-cn-0.22/");
        System.out.println("2. 如果遇到网络问题无法下载JAR文件，可以尝试使用其他版本或从其他来源获取。");
        System.out.println("3. 确保Java和Maven环境正常配置。");

        // 检查lib目录中是否已有vosk JAR文件
        File voskJar = new File("lib/vosk-0.3.47.jar");
        if (voskJar.exists()) {
            System.out.println("\n检测到已存在Vosk JAR文件: " + voskJar.getAbsolutePath());
            System.out.println("可以跳过下载步骤，直接执行安装命令。");
        }
    }
}