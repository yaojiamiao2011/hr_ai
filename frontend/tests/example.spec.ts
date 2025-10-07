import { test, expect } from '@playwright/test';

test('has title', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/HR AI 智能人力资源系统/);
});

test('has main app element', async ({ page }) => {
  await page.goto('/');
  await expect(page.locator('#app')).toBeVisible();
});