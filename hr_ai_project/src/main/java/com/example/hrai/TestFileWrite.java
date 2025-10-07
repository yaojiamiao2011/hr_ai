package com.example.hrai;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileWrite {
    public static void main(String[] args) {
        try {
            // 测试文件写入
            String txtOutputPath = "hr_ai_project/src/main/resources/txt";
            Path txtDir = Path.of(txtOutputPath);

            System.out.println("目录路径: " + txtDir.toAbsolutePath());
            System.out.println("目录是否存在: " + Files.exists(txtDir));

            if (!Files.exists(txtDir)) {
                Files.createDirectories(txtDir);
                System.out.println("创建目录成功");
            }

            // 创建测试文件
            String txtFileName = "test-file.txt";
            Path txtFilePath = txtDir.resolve(txtFileName);

            System.out.println("文件路径: " + txtFilePath.toAbsolutePath());

            // 写入文件
            String content = "这是一个测试文件内容。\n测试文件写入功能是否正常工作。";
            Files.write(txtFilePath, content.getBytes("UTF-8"));

            System.out.println("文件写入成功");
            System.out.println("文件是否存在: " + Files.exists(txtFilePath));

            // 读取文件内容验证
            String readContent = new String(Files.readAllBytes(txtFilePath), "UTF-8");
            System.out.println("读取的文件内容: " + readContent);

        } catch (Exception e) {
            System.err.println("文件写入测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}