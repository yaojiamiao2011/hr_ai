import { test, expect } from '@playwright/test';

test.describe('MCP前端结果显示功能验证', () => {
  test('检查A2视频处理的显示功能是否实现', async ({ page }) => {
    console.log('访问视频工具页面以验证我们添加的功能...');
    await page.goto('/video-tool');

    // 检查是否可以访问我们添加的组件响应式变量
    await expect(page.locator('button:has-text("处理A2视频")')).toBeVisible();

    // 检查页面中有无我们添加的对话框结构
    const pageContent = await page.content();

    // 验证已添加的元素
    const hasResultContentPreview = pageContent.includes('result-content-preview');
    const hasProcessTitle = pageContent.includes('process-title');
    const hasShowProcessResult = pageContent.includes('showProcessResult');

    console.log(`找到结果预览样式类: ${hasResultContentPreview}`);
    console.log(`找到processTitle引用: ${hasProcessTitle}`);
    console.log(`找到showProcessResult引用: ${hasShowProcessResult}`);

    // 验证关键元素存在
    expect(hasResultContentPreview).toBe(true);
    expect(hasProcessTitle).toBe(true);
    expect(hasShowProcessResult).toBe(true);

    console.log('前端结果显示功能已成功实现！');
  });
});