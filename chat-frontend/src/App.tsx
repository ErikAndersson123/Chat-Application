import { useEffect, useState } from "react";
import LoginPage from "./pages/LoginPage";
import ChatPage from "./pages/ChatPage";

function App() {
  const [user, setUser] = useState<any | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const saved = localStorage.getItem("chat_user");
    if (saved) {
      setUser(JSON.parse(saved));
    }
    setLoading(false);
  }, []);

  const handleLogin = (u: any) => {
    localStorage.setItem("chat_user", JSON.stringify(u));
    setUser(u);
  };

  const handleLogout = () => {
    localStorage.removeItem("chat_user");
    setUser(null);
  };

  if (loading) return null;

  return (
    <div className="min-h-screen">
      {!user ? (
        <LoginPage onLogin={handleLogin} />
      ) : (
        <ChatPage user={user} onLogout={handleLogout} />
      )}
    </div>
  );
}

export default App;