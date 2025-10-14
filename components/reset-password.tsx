// app/reset-password/page.tsx (client)
"use client";
import React, { useState } from "react";
import { useSearchParams } from "next/navigation";

export default function ResetPassword() {
  const params = useSearchParams();
  const token = params.get("token") || "";
  const [pw, setPw] = useState("");
  const [msg, setMsg] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await fetch("/api/auth/reset", {
        method: "POST",
        headers: { "Content-Type":"application/json" },
        body: JSON.stringify({ token, newPassword: pw })
      });
      const data = await res.json();
      if (res.ok) setMsg("✅ Password updated. You can now login.");
      else setMsg("❌ " + (data.message || "Failed"));
    } catch (err) {
      setMsg("⚠️ Lỗi kết nối");
    }
  };

  return (
    <div style={{minHeight:"100vh",display:"flex",alignItems:"center",justifyContent:"center",background:"#f5f2ff",padding:24}}>
      <div style={{width:420,background:"#fff",padding:32,borderRadius:12,boxShadow:"0 8px 24px rgba(0,0,0,0.06)"}}>
        <h3>Đặt lại mật khẩu</h3>
        <form onSubmit={submit}>
          <div style={{margin:"12px 0"}}>
            <input type="password" placeholder="Mật khẩu mới" value={pw} onChange={e=>setPw(e.target.value)} required style={{width:"100%",padding:10,borderRadius:8,border:"1px solid #ddd"}}/>
          </div>
          <button style={{width:"100%",padding:10,borderRadius:8,background:"#0057ff",color:"#fff",border:"none"}}>Đặt lại</button>
        </form>
        {msg && <p style={{marginTop:12}}>{msg}</p>}
      </div>
    </div>
  );
}
