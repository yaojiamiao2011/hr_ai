#!/bin/bash

# API测试脚本
# 后台服务启动后运行此脚本来验证API是否正常工作

echo "开始测试后台API..."

# 测试1: 获取所有AI模型配置
echo "测试1: 获取所有AI模型配置"
curl -X GET "http://localhost:8090/api/ai-config" -H "Content-Type: application/json"
echo -e "\n"

# 测试2: 获取激活的AI模型配置
echo "测试2: 获取激活的AI模型配置"
curl -X GET "http://localhost:8090/api/ai-config/active" -H "Content-Type: application/json"
echo -e "\n"

# 测试3: 创建一个新的AI模型配置
echo "测试3: 创建一个新的AI模型配置"
curl -X POST "http://localhost:8090/api/ai-config" -H "Content-Type: application/json" -d '{
  "modelName": "test-model",
  "provider": "test-provider",
  "apiKey": "test-api-key",
  "modelEndpoint": "http://test-endpoint.com",
  "isActive": true
}'
echo -e "\n"

# 测试4: 获取所有AI模型配置(验证创建是否成功)
echo "测试4: 验证新配置是否创建成功"
curl -X GET "http://localhost:8090/api/ai-config" -H "Content-Type: application/json"
echo -e "\n"

echo "API测试完成"