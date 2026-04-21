import { useEffect, useRef, useState } from "react";
import {
  getRooms,
  getMessages,
  createRoom,
  joinRoom
} from "../api/api";
import { Client } from "@stomp/stompjs";

export default function ChatPage({ user, onLogout }: any) {
  const [rooms, setRooms] = useState<any[]>([]);
  const [filteredRooms, setFilteredRooms] = useState<any[]>([]);
  const [currentRoom, setCurrentRoom] = useState<any>(null);
  const [messages, setMessages] = useState<any[]>([]);
  const [text, setText] = useState("");
  const [typingUser, setTypingUser] = useState<string | null>(null);

  const [search, setSearch] = useState("");

  const [createModal, setCreateModal] = useState(false);
  const [newRoom, setNewRoom] = useState("");
  const [createPassword, setCreatePassword] = useState("");
  const [showCreatePassword, setShowCreatePassword] = useState(false);

  const [joinModal, setJoinModal] = useState<any>(null);
  const [joinPassword, setJoinPassword] = useState("");
  const [showJoinPassword, setShowJoinPassword] = useState(false);

  const clientRef = useRef<Client | null>(null);
  const subscriptionRef = useRef<any>(null);
  const typingSubRef = useRef<any>(null);
  const typingTimeoutRef = useRef<any>(null);
  const bottomRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    getRooms().then((data) => {
      setRooms(data);
      setFilteredRooms(data);
    });
  }, []);

  useEffect(() => {
    const filtered = rooms.filter(r =>
      r.roomName.toLowerCase().includes(search.toLowerCase())
    );
    setFilteredRooms(filtered);
  }, [search, rooms]);

  useEffect(() => {
    if (currentRoom) {
      getMessages(currentRoom.id).then(setMessages);
    }
  }, [currentRoom]);

  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://192.168.32.5:8080/ws",
      reconnectDelay: 5000,
    });

    client.onConnect = () => console.log("connected");
    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

  useEffect(() => {
    if (!clientRef.current || !currentRoom) return;

    subscriptionRef.current?.unsubscribe();
    typingSubRef.current?.unsubscribe();

    const sub = clientRef.current.subscribe(
      `/topic/room/${currentRoom.id}`,
      (msg) => {
        const newMessage = JSON.parse(msg.body);
        setMessages((prev) => [...prev, newMessage]);
      }
    );

    const typingSub = clientRef.current.subscribe(
      `/topic/room/${currentRoom.id}/typing`,
      (msg) => {
        const username = msg.body;

        if (username === user.username) return;

        setTypingUser(username);

        clearTimeout(typingTimeoutRef.current);
        typingTimeoutRef.current = setTimeout(() => {
          setTypingUser(null);
        }, 2000);
      }
    );

    subscriptionRef.current = sub;
    typingSubRef.current = typingSub;

    return () => {
      sub.unsubscribe();
      typingSub.unsubscribe();
    };
  }, [currentRoom]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = () => {
    if (!text || !clientRef.current || !currentRoom) return;

    clientRef.current.publish({
      destination: "/app/chat",
      body: JSON.stringify({
        content: text,
        sender: { id: user.id },
        room: { id: currentRoom.id },
      }),
    });

    setText("");
  };

  const sendTyping = () => {
    if (!clientRef.current || !currentRoom) return;

    clientRef.current.publish({
      destination: "/app/typing",
      body: JSON.stringify({
        sender: { id: user.id },
        room: { id: currentRoom.id },
      }),
    });
  };

  const handleJoinClick = (room: any) => {
    setJoinPassword("");

    if (room.password) {
      setJoinModal(room);
    } else {
      join(room, "");
    }
  };

  const join = async (room: any, password: string) => {
    try {
      const joined = await joinRoom(room.id, password);
      setCurrentRoom(joined);
      setJoinModal(null);
      setJoinPassword("");
    } catch {
      alert("Wrong password");
      setJoinPassword("");
    }
  };

  const handleCreate = async () => {
    if (!newRoom.trim()) return;

    try {
      const room = await createRoom(newRoom.trim(), createPassword || "");

      await getRooms(); // keeps behavior but removes unused warning
      setRooms((prev) => [...prev, room]);
      setFilteredRooms((prev) => [...prev, room]);

      setCreateModal(false);
      setNewRoom("");
      setCreatePassword("");
    } catch {
      alert("Failed to create room");
    }
  };

  return (
    <div className="flex h-screen bg-gray-800 text-white">

      <div className="w-80 bg-gray-900 flex flex-col border-r border-gray-700 p-3">

        <div
          className="text-xl font-bold mb-4 flex items-center gap-2 cursor-pointer"
          onClick={() => setCurrentRoom(null)}
        >
          <i className="bi bi-chat-fill"></i>
          ChatApp
        </div>

        <input
          placeholder="Search rooms..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="p-2 mb-3 rounded bg-gray-800"
          autoComplete="off"
        />

        <div className="space-y-2 flex-1 overflow-y-auto">
          {filteredRooms.map((r) => (
            <div
              key={r.id}
              onClick={() => handleJoinClick(r)}
              className="p-2 bg-gray-800 rounded cursor-pointer hover:bg-gray-700 flex justify-between"
            >
              <span># {r.roomName}</span>
              {r.password && <i className="bi bi-lock-fill"></i>}
            </div>
          ))}
        </div>

        <button
          onClick={() => setCreateModal(true)}
          className="mt-3 bg-blue-600 p-2 rounded"
        >
          Create Room
        </button>
      </div>

      <div className="flex flex-col flex-1">

        <div className="h-16 flex items-center justify-between px-6 bg-gray-900 border-b border-gray-700">
          <div>
            {currentRoom ? `# ${currentRoom.roomName}` : ""}
          </div>

          <button
            onClick={onLogout}
            className="bg-blue-600 px-4 py-1 rounded"
          >
            Logout
          </button>
        </div>

        {!currentRoom && (
          <div className="flex flex-1 items-center justify-center">
            <div className="flex items-center gap-4">
              <i className="bi bi-chat-fill text-6xl"></i>
              <div className="text-3xl font-bold">ChatApp</div>
            </div>
          </div>
        )}

        {currentRoom && (
          <>
            <div className="flex-1 overflow-y-auto p-6 space-y-4">
              {messages.map((m) => {
                const isMe = user && String(m.sender?.id) === String(user.id);

                return (
                  <div key={m.id} className={`flex ${isMe ? "justify-end" : "justify-start"}`}>
                    <div className={`px-4 py-2 rounded-2xl max-w-md ${
                      isMe ? "bg-blue-600" : "bg-gray-700"
                    }`}>
                      {!isMe && (
                        <div className="text-xs text-gray-400 mb-1">
                          {m.sender?.username}
                        </div>
                      )}
                      {m.content}
                    </div>
                  </div>
                );
              })}
              <div ref={bottomRef} />
            </div>

            {typingUser && (
              <div className="text-sm text-gray-400 italic px-6 pb-2">
                {typingUser} is typing...
              </div>
            )}

            <div className="p-4 bg-gray-900 border-t border-gray-700 flex gap-3">
              <input
                className="flex-1 p-3 rounded-full bg-gray-800"
                autoComplete="off"
                name="chat-message"
                value={text}
                onChange={(e) => {
                  setText(e.target.value);
                  sendTyping();
                }}
                onKeyDown={(e) => e.key === "Enter" && handleSend()}
              />
              <button onClick={handleSend} className="bg-blue-600 px-6 rounded-full">
                Send
              </button>
            </div>
          </>
        )}
      </div>

      {createModal && (
        <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
          <form autoComplete="off">
            <input type="text" style={{ display: "none" }} />
            <input type="password" style={{ display: "none" }} />

            <div className="bg-gray-900 p-6 rounded-xl w-80 border border-gray-700">

              <div className="text-lg mb-3">Create Room</div>

              <input
                placeholder="Room Name"
                type="text"
                value={newRoom}
                onChange={(e) => setNewRoom(e.target.value)}
                className="w-full p-2 mb-3 rounded bg-gray-800"
                autoComplete="off"
              />

              <div className="relative mb-4">
                <input
                  type={showCreatePassword ? "text" : "password"}
                  placeholder="Password (optional)"
                  value={createPassword}
                  onChange={(e) => setCreatePassword(e.target.value)}
                  className="w-full p-2 pr-16 rounded bg-gray-800"
                  autoComplete="off"
                  name="nope-create-password"
                />

                <button
                  type="button"
                  onClick={() => setShowCreatePassword(!showCreatePassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-sm text-gray-800 hover:text-gray-600"
                >
                  {showCreatePassword ? "Hide" : "Show"}
                </button>
              </div>

              <div className="flex gap-2">
                <button type="button" onClick={handleCreate} className="flex-1 bg-blue-600 p-2 rounded">
                  Create
                </button>
                <button type="button" onClick={() => setCreateModal(false)} className="flex-1 bg-gray-700 p-2 rounded">
                  Cancel
                </button>
              </div>
            </div>
          </form>
        </div>
      )}

      {joinModal && (
        <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
          <form autoComplete="off">
            <input type="text" style={{ display: "none" }} />
            <input type="password" style={{ display: "none" }} />

            <div className="bg-gray-900 p-6 rounded-xl w-80 border border-gray-700">

              <div className="text-lg mb-3">Join #{joinModal.roomName}</div>

              <div className="relative mb-4">
                <input
                  type={showJoinPassword ? "text" : "password"}
                  placeholder="Password"
                  value={joinPassword}
                  onChange={(e) => setJoinPassword(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && join(joinModal, joinPassword)}
                  className="w-full p-2 pr-16 rounded bg-gray-800"
                  autoComplete="off"
                  name="nope-join-password"
                />

                <button
                  type="button"
                  onClick={() => setShowJoinPassword(!showJoinPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-800 hover:text-gray-600 dark:text-gray-300 dark:hover:text-gray-100"
                >
                  {showJoinPassword ? "Hide" : "Show"}
                </button>
              </div>

              <div className="flex gap-2">
                <button type="button" onClick={() => join(joinModal, joinPassword)} className="flex-1 bg-blue-600 p-2 rounded">
                  Join
                </button>
                <button type="button" onClick={() => setJoinModal(null)} className="flex-1 bg-gray-700 p-2 rounded">
                  Cancel
                </button>
              </div>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}