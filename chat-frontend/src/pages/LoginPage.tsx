import { useState } from "react";
import { login, register } from "../api/api";

export default function LoginPage({ onLogin }: any) {
  const [isRegister, setIsRegister] = useState(false);
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async () => {
    if (!username || !password) return;

    try {
      const user = isRegister
        ? await register(username, password)
        : await login(username, password);

      onLogin(user);
    } catch {
      setError("Invalid credentials");
    }
  };

  return (
    <div className="h-screen flex items-center justify-center bg-gray-900 text-white">

      <div className="bg-gray-800 p-10 rounded-2xl shadow-xl w-full max-w-md border border-gray-700">

        <div className="flex items-center justify-center gap-3 mb-6">
          <i className="bi bi-chat-fill text-3xl"></i>
          <h1 className="text-3xl font-bold">ChatApp</h1>
        </div>

        {error && (
          <div className="bg-red-500/20 text-red-400 p-2 rounded mb-4 text-sm text-center">
            {error}
          </div>
        )}

        <input
          className="w-full p-3 mb-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-300 placeholder-gray-500 placeholder:text-gray-500"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          autoComplete="off"
        />

        <div className="relative mb-4">
          <input
            type={showPassword ? "text" : "password"}
            className="w-full p-3 pr-16 rounded-lg bg-gray-900 border border-gray-700 text-gray-300 placeholder-gray-500 placeholder:text-gray-500 caret-gray-300"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSubmit()}
            autoComplete="new-password"
          />

          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-300 hover:text-gray-100"
          >
            {showPassword ? "Hide" : "Show"}
          </button>
        </div>

        <button
          onClick={handleSubmit}
          className="w-full bg-blue-600 py-3 rounded-lg font-semibold hover:bg-blue-700"
        >
          {isRegister ? "Create Account" : "Login"}
        </button>

        <p className="text-gray-400 text-sm text-center mt-6">
          {isRegister ? "Already have an account?" : "Don't have an account?"}
          <span
            onClick={() => {
              setIsRegister(!isRegister);
              setError("");
            }}
            className="text-blue-400 ml-2 cursor-pointer hover:underline"
          >
            {isRegister ? "Login" : "Register"}
          </span>
        </p>
      </div>
    </div>
  );
}