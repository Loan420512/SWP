"use client";

import React, { useState } from "react";
import styled from "styled-components";

const Page = styled.div`
  min-height: 100vh;
  background-color: #f5f2ff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: "Inter", sans-serif;
  padding: 40px;
`;

const Box = styled.div`
  width: 420px;
  background: #fff;
  padding: 34px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  text-align: center;
`;

const Logo = styled.div`
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: #c0ff4a;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  font-size: 34px;
`;

const Title = styled.h2`
  margin: 6px 0 2px;
  font-size: 20px;
  font-weight: 700;
`;

const Subtitle = styled.p`
  color: #666;
  font-size: 13px;
  margin-bottom: 18px;
`;

const FormGroup = styled.div`
  text-align: left;
  margin-bottom: 14px;
`;

const Label = styled.label`
  display: block;
  font-size: 13px;
  margin-bottom: 6px;
  font-weight: 600;
`;

const Input = styled.input`
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  &:focus {
    outline: none;
    border-color: #0077ff;
  }
`;

const Button = styled.button<{ disabled?: boolean }>`
  width: 100%;
  padding: 12px;
  border-radius: 8px;
  border: none;
  background: ${(p) => (p.disabled ? "#a9c4ff" : "#0057ff")};
  color: white;
  font-weight: 600;
  cursor: ${(p) => (p.disabled ? "not-allowed" : "pointer")};
  margin-top: 6px;
`;

const Msg = styled.p`
  margin-top: 12px;
  color: #333;
  font-weight: 500;
`;

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [info, setInfo] = useState<string | null>(null);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setInfo(null);
    try {
      const res = await fetch("/api/auth/forgot", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
      });
      const data = await res.json();
      if (res.ok) {
        setInfo(
          "✅ " + (data.message || "A confirmation email has been sent. Please check your inbox.")
        );
      } else {
        setInfo("❌ " + (data.message || "Unable to send email."));
      }
    } catch (err) {
      console.error(err);
      setInfo("⚠️ Connection error to the server.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Page>
      <Box>
        <Logo>⚡</Logo>
        <Title>Forgot Password</Title>
        <Subtitle>Enter your email to receive a password reset link</Subtitle>

        <form onSubmit={submit}>
          <FormGroup>
            <Label>Email</Label>
            <Input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              placeholder="you@example.com"
            />
          </FormGroup>

          <Button type="submit" disabled={loading}>
            {loading ? "Sending..." : "Send Reset Link"}
          </Button>
        </form>

        {info && <Msg>{info}</Msg>}
      </Box>
    </Page>
  );
}
