import { test, expect, vi } from "vitest";
import { login } from "../api/api";

globalThis.fetch = vi.fn(() =>
  Promise.resolve({
    ok: true,
    json: () => Promise.resolve({ username: "test" })
  })
) as any;

test("login returns user data", async () => {
  const result = await login("test", "pass");
  expect(result.username).toBe("test");
});