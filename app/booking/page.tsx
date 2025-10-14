"use client"; // Required for Next.js Client Component

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import styled from "styled-components";

// 🌈 Styled Components
const PageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f5f2ff;
  font-family: "Inter", sans-serif;
`;

const LogoSection = styled.div`
  text-align: center;
  margin-bottom: 25px;
`;

const LogoCircle = styled.div`
  background-color: #c0ff4a;
  color: #000;
  font-size: 38px;
  font-weight: bold;
  border-radius: 50%;
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: auto;
`;

const BrandName = styled.h1`
  font-size: 26px;
  font-weight: 700;
  margin-top: 10px;
  margin-bottom: 5px;
`;

const Subtitle = styled.p`
  font-size: 14px;
  color: #555;
`;

const FormBox = styled.div`
  background: #fff;
  padding: 35px;
  border-radius: 18px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
  width: 400px;
  text-align: center;
`;

const Title = styled.h2`
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 4px;
`;

const SubText = styled.p`
  font-size: 13px;
  color: #666;
  margin-bottom: 20px;
`;

const FormGroup = styled.div`
  text-align: left;
  margin-bottom: 15px;
`;

const Label = styled.label`
  display: block;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
`;

const Input = styled.input`
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0077ff;
    outline: none;
  }
`;

const Select = styled.select`
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0077ff;
    outline: none;
  }
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  font-size: 14px;
  resize: none;
  height: 70px;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0077ff;
    outline: none;
  }
`;

const Button = styled.button`
  width: 100%;
  background-color: #0057ff;
  color: #fff;
  border: none;
  padding: 12px;
  font-size: 15px;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s ease;

  &:hover {
    background-color: #0040cc;
  }

  &:disabled {
    background-color: #a9c4ff;
    cursor: not-allowed;
  }
`;

const Message = styled.p`
  margin-top: 15px;
  font-weight: 500;
  color: #333;
`;


// 🌟 Booking Component
const Booking = () => {
  const router = useRouter();
  const [station, setStation] = useState("");
  const [date, setDate] = useState("");
  const [note, setNote] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    const bookingData = {
      userId: "U001", // Replace with the logged-in user ID
      stationId: station,
      bookingTime: date,
      note: note,
    };

    try {
      const res = await fetch("http://localhost:5000/api/bookings", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(bookingData),
      });

      const data = await res.json();

      if (res.ok) {
        setMessage("✅ Booking successful!");
        setStation("");
        setDate("");
        setNote("");
      } else {
        setMessage("❌ " + (data.message || "Booking failed!"));
      }
    } catch (err) {
      console.error(err);
      setMessage("⚠️ Could not connect to the API!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <PageContainer>
      <LogoSection>
        <LogoCircle>⚡</LogoCircle>
        <BrandName>EVSwap</BrandName>
        <Subtitle>Battery Swap Station Management</Subtitle>
      </LogoSection>

      <FormBox>
        <Title>Booking a Battery Swap</Title>
        <SubText>Choose a station and time for your booking</SubText>

        <form onSubmit={handleSubmit}>
          <FormGroup>
            <Label>Select Battery Swap Station</Label>
            <Select
              value={station}
              onChange={(e) => setStation(e.target.value)}
              required
            >
              <option value="">-- Select station --</option>
              <option value="1">City Center Station</option>
              <option value="2">Shopping Mall Station</option>
              <option value="3">Airport Terminal Station</option>
            </Select>
          </FormGroup>

          <FormGroup>
            <Label>Select Date & Time</Label>
            <Input
              type="datetime-local"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </FormGroup>

          <FormGroup>
            <Label>Notes (optional)</Label>
            <TextArea
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="Add any notes..."
            />
          </FormGroup>

          <Button type="submit" disabled={loading}>
            {loading ? "Submitting..." : "Confirm Booking"}
          </Button>
        </form>

        {message && <Message>{message}</Message>}
      </FormBox>
    </PageContainer>
  );
};

export default Booking;
