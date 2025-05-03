import test, { expect } from "@playwright/test";
import Utility from "../support/Utility";
import { getLastAddedAccount } from "../support/accountManagment"
let courseName = "E2E Course" + Utility.uniqueNumber()

test('create course', async ({ page }) => {

    let professor = getLastAddedAccount("professor")?.lastname || ""
    let student = getLastAddedAccount("student")?.lastname || ""
    // Login
    await page.goto("/")
    await expect(page.getByTestId('login-email')).toBeVisible();
    await page.getByTestId('login-email').fill("admin@example.com")
    await page.getByTestId('login-password').fill("admin")
    await page.getByTestId('login-submit').click()
    await expect(page.getByTestId('nav-logout')).toBeVisible()
    // Navigate to Course managment page
    await page.getByTestId('nav-course-management').click()
    await expect(page.getByTestId('course-management-add')).toBeVisible()
    // navigate to create new course page
    await page.getByTestId('course-management-add').click()
    await expect(page.getByTestId('course-name-input')).toBeVisible()


    // fill out the add new course form
    await page.getByTestId('course-name-input').fill(courseName)
    // add professor
    await page.getByTestId('professor-search-input').fill(professor)
    await expect(page.getByTestId('professor-search-results')).toBeVisible()
    await page.getByText(professor).click()
    await expect(page.getByTestId('professor-cards-container')).toBeVisible()
    await expect(page.getByTestId('professor-cards-container')).toContainText(professor)
    // add student
    await page.getByTestId('student-search-input').fill(student)
    await expect(page.getByTestId('student-search-results')).toBeVisible()
    await page.getByText(student).last().click()
    await expect(page.getByTestId('search-added-item').last()).toBeVisible()
    await expect(page.getByTestId('search-added-item').last()).toContainText(student)

    await page.getByTestId('course-description-input').fill("This is the description for course: " + courseName + ". We will be learning about E2E Testing :)")
    await page.getByTestId('course-submit').click()
    await expect(page.getByTestId('course-management-add')).toBeVisible()


})

test('edit course', async ({ page }) => {
    // Login
    await page.goto("/")
    await expect(page.getByTestId('login-email')).toBeVisible();
    await page.getByTestId('login-email').fill("admin@example.com")
    await page.getByTestId('login-password').fill("admin")
    await page.getByTestId('login-submit').click()
    await expect(page.getByTestId('nav-logout')).toBeVisible()

    // Navigate to Course managment page
    await page.getByTestId('nav-course-management').click()
    await expect(page.getByTestId('course-management-add')).toBeVisible()


    await page.getByTestId('course-edit').first().click()
    await expect(page.getByTestId('student-search-input')).toBeVisible()

    await page.getByRole('textbox', { name: 'Description' }).clear()
    await page.getByRole('textbox', { name: 'Description' }).fill("This is the updated description for course: " + courseName + ". We will be learning about E2E Testing :)")

    await page.getByRole('button', { name: ' Save Course' }).click()

    await expect(page.getByTestId('course-management-add')).toBeVisible()
})