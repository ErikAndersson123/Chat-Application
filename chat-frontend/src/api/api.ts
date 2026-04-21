const API_BASE = "https://chat-application-oyyq.onrender.com/api"

// Send login request
export async function login(username: string, password: string) {
  const res = await fetch(`${API_BASE}/auth/login?username=${username}&password=${password}`, {
    method: "POST"
  });
  if (!res.ok) throw new Error();
  return res.json();
}

// Register a new user
export async function register(username: string, password: string) {
  const res = await fetch(`${API_BASE}/auth/register?username=${username}&password=${password}`, {
    method: "POST"
  });
  if (!res.ok) throw new Error();
  return res.json();
}

// Fetch all chat rooms
export async function getRooms() {
  return fetch(`${API_BASE}/rooms`).then(r => r.json());
}

// Create a new room
export async function createRoom(name: string, password?: string) {
  return fetch(`${API_BASE}/rooms/create?name=${name}&password=${password || ""}`, {
    method: "POST"
  }).then(r => r.json());
}

// Join a room
export async function joinRoom(id: number, password?: string) {
  const res = await fetch(`${API_BASE}/rooms/join?id=${id}&password=${password || ""}`, {
    method: "POST"
  });

  if (!res.ok) throw new Error("Wrong password");
  return res.json();
}

// Get messages in a room
export async function getMessages(roomId: number) {
  return fetch(`${API_BASE}/messages/room/${roomId}`).then(r => r.json());
}