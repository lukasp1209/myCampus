import test, { expect } from "@playwright/test";
import Utility from "../support/Utility";
import { addAccount } from "../support/accountManagment";

import { Page } from 'playwright';

async function getPasswordFromMessage(page: Page): Promise<string> {
    // Locate the element with test-id and get the paragraph text
    const message = await page.locator('[data-testid="register-info-message"] p').first().textContent();
    if (!message) {
        throw new Error('Password message not found');
    }
    // Extract the password after "Password: "
    const passwordMatch = message.match(/Password:\s*(.+)/);

    if (!passwordMatch || !passwordMatch[1]) {
        throw new Error('Password not found in message');
    }
    return passwordMatch[1].trim();
}

test('register student', async ({ page }) => {
    // Login
    await page.goto("/")
    await expect(page.getByTestId('login-email')).toBeVisible();
    await page.getByTestId('login-email').fill("admin@example.com")
    await page.getByTestId('login-password').fill("admin")
    await page.getByTestId('login-submit').click()
    await expect(page.getByTestId('nav-logout')).toBeVisible()

    // Navigate to User Managment
    await page.getByTestId('nav-user-management').click()
    await expect(page.getByTestId('user-management-create')).toBeVisible()

    // Navigate to register user form
    await page.getByTestId('user-management-create').click()
    await expect(page.getByTestId('register-first-name')).toBeVisible()
    let lastName = "Name" + Utility.uniqueNumber()
    let email = "student" + Utility.uniqueNumber() + "@e2e-test.com"
    // Fill user registration from
    await page.getByTestId('register-first-name').fill("Student")
    await page.getByTestId('register-last-name').fill(lastName)
    await page.getByTestId('register-birthdate').fill("1998-10-23")
    await page.getByTestId('register-email').fill(email)
    await page.getByTestId('register-country').fill("Germany")
    await page.getByTestId('register-city').fill("Frankfurt am Main")
    await page.getByTestId('register-zip').fill("60433")
    await page.getByTestId('register-street').fill("Muster Strasse")
    await page.getByTestId('register-house-number').fill("13")
    await page.getByTestId('register-role').selectOption({ label: "ROLE_STUDENT" })

    await page.getByTestId('register-submit').click()

    const password = await getPasswordFromMessage(page);
    console.log(password);
    addAccount('student', email, password, lastName);
})

test('register professor', async ({ page }) => {
    // Login
    await page.goto("/")
    await expect(page.getByTestId('login-email')).toBeVisible();
    await page.getByTestId('login-email').fill("admin@example.com")
    await page.getByTestId('login-password').fill("admin")
    await page.getByTestId('login-submit').click()
    await expect(page.getByTestId('nav-logout')).toBeVisible()

    // Navigate to User Managment
    await page.getByTestId('nav-user-management').click()
    await expect(page.getByTestId('user-management-create')).toBeVisible()

    // Navigate to register user form
    await page.getByTestId('user-management-create').click()
    await expect(page.getByTestId('register-first-name')).toBeVisible()
    let lastName = "Name" + Utility.uniqueNumber()
    let email = "professor" + Utility.uniqueNumber() + "@e2e-test.com"
    // Fill user registration from
    await page.getByTestId('register-first-name').fill("Professor")
    await page.getByTestId('register-last-name').fill(lastName)
    await page.getByTestId('register-birthdate').fill("1975-10-23")
    await page.getByTestId('register-email').fill(email)
    await page.getByTestId('register-country').fill("Germany")
    await page.getByTestId('register-city').fill("Frankfurt am Main")
    await page.getByTestId('register-zip').fill("60433")
    await page.getByTestId('register-street').fill("Muster Strasse")
    await page.getByTestId('register-house-number').fill("13")
    await page.getByTestId('register-role').selectOption({ label: "ROLE_PROFESOR" })

    await page.getByTestId('register-submit').click()

    const password = await getPasswordFromMessage(page);
    console.log(password);
    addAccount('professor', email, password, lastName);
})