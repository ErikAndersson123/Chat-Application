import { render, screen, fireEvent } from "@testing-library/react";
import { test, expect } from "vitest";
import "@testing-library/jest-dom/vitest";
import LoginPage from "../pages/LoginPage";

test("renders login button", () => {
  render(<LoginPage onLogin={() => {}} />);
  expect(screen.getByText(/login/i)).toBeInTheDocument();
});

test("updates input fields", () => {
  render(<LoginPage onLogin={() => {}} />);

  const username = screen.getByPlaceholderText(/username/i);
  const password = screen.getByPlaceholderText(/password/i);

  fireEvent.change(username, { target: { value: "test" } });
  fireEvent.change(password, { target: { value: "pass" } });

  expect(username).toHaveValue("test");
  expect(password).toHaveValue("pass");
});

test("toggles password visibility", () => {
  render(<LoginPage onLogin={() => {}} />);

  const button = screen.getByText(/show/i);
  fireEvent.click(button);

  expect(screen.getByPlaceholderText(/password/i)).toHaveAttribute("type", "text");
});

test("switches between login and register", () => {
  render(<LoginPage onLogin={() => {}} />);

  const toggle = screen.getByText(/register/i);
  fireEvent.click(toggle);

  expect(screen.getByText(/create account/i)).toBeInTheDocument();
});