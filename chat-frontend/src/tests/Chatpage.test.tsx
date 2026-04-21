import { render, screen, fireEvent } from "@testing-library/react";
import { test, expect } from "vitest";
import "@testing-library/jest-dom/vitest";
import ChatPage from "../pages/ChatPage";

const user = { id: 1, username: "test" };

test("renders main layout", () => {
  render(<ChatPage user={user} onLogout={() => {}} />);
  expect(screen.getAllByText(/chatapp/i).length).toBeGreaterThan(0);
});

test("updates search input", () => {
  render(<ChatPage user={user} onLogout={() => {}} />);

  const input = screen.getByPlaceholderText(/search rooms/i);
  fireEvent.change(input, { target: { value: "room" } });

  expect(input).toHaveValue("room");
});

test("updates message input", () => {
  render(<ChatPage user={user} onLogout={() => {}} />);

  const input = screen.getByRole("textbox");
  fireEvent.change(input, { target: { value: "hello" } });

  expect(input).toHaveValue("hello");
});