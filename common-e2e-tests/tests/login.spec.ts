import test, { expect } from "@playwright/test";


test('login', async ({page}) => {
    await page.goto("/")
    await expect(page.getByTestId('login-email')).toBeVisible();
    await page.getByTestId('login-email').fill("admin@example.com")
    await page.getByTestId('login-password').fill("admin")
    await page.getByTestId('login-submit').click()
    await expect(page.getByTestId('nav-logout')).toBeVisible()
})