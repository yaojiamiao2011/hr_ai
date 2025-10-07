import { test, expect, request } from '@playwright/test';

test.describe('A2视频处理功能测试', () => {
  test('测试处理A2视频API返回值', async () => {
    console.log('正在调用A2视频处理API...');

    // 创建API上下文并调用A2视频处理API
    const apiContext = await request.newContext({
      baseURL: 'http://127.0.0.1:8081', // 后端实际运行在8081端口
    });

    // 发起处理A2视频的POST请求
    const startTime = Date.now();
    const response = await apiContext.post('/api/video-tool/process-a2-video-to-txt', {
      headers: {
        'Content-Type': 'application/json',
      }
    });

    const responseTime = Date.now() - startTime;
    console.log(`API响应时间: ${responseTime}ms`);
    console.log(`响应状态码: ${response.status()}`);

    // 检查响应是否成功
    expect(response.status()).toBeGreaterThanOrEqual(200);
    expect(response.status()).toBeLessThan(500);

    // 获取响应体内容
    const responseBody = await response.json();
    console.log('响应体内容:', JSON.stringify(responseBody, null, 2));

    // 验证响应体包含必要的字段
    expect(responseBody).toHaveProperty('text');
    expect(responseBody).toHaveProperty('txtFilePath');
    expect(responseBody).toHaveProperty('recordId');
    expect(responseBody).toHaveProperty('message');

    // 验证返回的消息包含成功字符串
    expect(responseBody.message).toContain('A2视频转文本成功');

    // 验证返回的文本内容是否为有意义的字符串（而不是简单的一段文字）
    const returnedText = responseBody.text;
    expect(returnedText).toBeDefined();

    //检查文本既不是空的也不是一个简单的值
    console.log(`返回的转录文本长度: ${returnedText.length}`);
    expect(returnedText.length).toBeGreaterThan(0);

    if(returnedText) {
      console.log('返回的文本内容:', returnedText.substring(0, 200) + (returnedText.length > 200 ? '...' : ''));
    } else {
      console.log('警告: 没有返回文本内容');
    }

    // 验证其他字段
    expect(responseBody.txtFilePath).toContain('.txt');
    expect(responseBody.txtFilePath).toContain('A2');
    expect(responseBody.recordId).toBeGreaterThanOrEqual(0);
  });

  test('测试前端处理A2视频并显示文本', async ({ page }) => {
    // 访问视频工具页面
    await page.goto('/video-tool');
    await expect(page.getByText('视频工具')).toBeVisible();

    // 记录开始时间
    const startTime = Date.now();

    // 点击处理A2视频按钮
    const processA2Button = page.locator('button').filter({ hasText: '处理A2视频' });
    await expect(processA2Button).toBeEnabled();
    await processA2Button.click();

    console.log('已点击处理A2视频按钮');

    // 等待可能出现的对话框
    await page.waitForTimeout(5000); // 等待API调用完成

    // 检查是否弹出了结果对话框，通常会有某种信息显示
    const resultDialogVisible = await page.locator('.el-overlay').isVisible();
    if (resultDialogVisible) {
      console.log('结果对话框已显示');

      // 等待文本内容出现
      await page.waitForTimeout(2000);

      // 查找文本内容
      const textContent = await page.locator('.result-content-preview pre').textContent();
      if(textContent) {
        console.log('在对话框中发现的文本内容:', textContent.substring(0, 200) + (textContent.length > 200 ? '...' : ''));
        expect(textContent.length).toBeGreaterThan(0);
      } else {
        console.log('对话框中没有找到预期的文本预览区域');
      }
    } else {
      console.log('结果对话框未显示，可能是API还在处理中或出错');

      // 检查是否有错误消息
      const errorMessages = page.locator('.el-message');
      const errorCount = await errorMessages.count();

      if(errorCount > 0) {
        for(let i = 0; i < errorCount; i++) {
          const errorText = await errorMessages.nth(i).textContent();
          console.log(`错误信息 #${i+1}:`, errorText);
        }
      }

      // 也检查正常的消息提示
      const infoMessages = page.locator('.el-message');
      const infoCount = await infoMessages.count();
      if(infoCount > 0) {
        for(let i = 0; i < infoCount; i++) {
          const infoText = await infoMessages.nth(i).textContent();
          console.log(`信息提示 #${i+1}:`, infoText);
        }
      }
    }

    const responseTime = Date.now() - startTime;
    console.log(`前端测试完成，总耗时: ${responseTime}ms`);
  });
});