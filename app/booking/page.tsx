"use client"; // Required for Next.js Client Component

import React, { useState } from "react";
import { useRouter } from "next/navigation";
import styled from "styled-components";

// Styled components for CSS
const BookingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  font-family: "Poppins", sans-serif;
`;

const BookingBox = styled.div`
  background: #fff;
  padding: 30px 40px;
  border-radius: 20px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  width: 400px;
  text-align: center;
`;

const FormGroup = styled.div`
  margin-bottom: 15px;
  text-align: left;
`;

const Label = styled.label`
  display: block;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
`;

const Input = styled.input`
  width: 100%;
  padding: 10px 12px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 15px;
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
  border: 1px solid #ccc;
  font-size: 15px;
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
  border: 1px solid #ccc;
  font-size: 15px;
  min-height: 80px;
  resize: vertical;
  transition: border-color 0.2s;

  &:focus {
    border-color: #0077ff;
    outline: none;
  }
`;

const Button = styled.button`
  width: 100%;
  background: #0077ff;
  color: #fff;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
  
  &:hover {
    background: #005fe0;
  }

  &:disabled {
    background: #b0c7f1;
    cursor: not-allowed;
  }
`;

const BookingMessage = styled.p`
  margin-top: 15px;
  font-weight: 500;
  font-size: 15px;
  color: #333;
`;

const Booking = () => {
  const router = useRouter();
  const [station, setStation] = useState<string>("");
  const [date, setDate] = useState<string>("");
  const [note, setNote] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(false);
  const [message, setMessage] = useState<string>("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage("");

    const bookingData = {
      userId: "U001", // Replace with the current user ID
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
        console.log("Booking result:", data);
        setStation("");
        setDate("");
        setNote("");
        // router.push("/thankyou"); // Uncomment if you want to redirect
      } else {
        setMessage("❌ " + (data.message || "Booking failed!"));
      }
    } catch (error) {
      console.error(error);
      setMessage("⚠️ Connection error to the API!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <BookingContainer>
      <BookingBox>
        <h2>Book a Battery Swap</h2>

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
            <Label>Notes</Label>
            <TextArea
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="Add any notes (optional)"
            />
          </FormGroup>

          <Button type="submit" disabled={loading}>
            {loading ? "Submitting..." : "Confirm Booking"}
          </Button>
        </form>

        {message && <BookingMessage>{message}</BookingMessage>}
      </BookingBox>
    </BookingContainer>
  );
};

export default Booking;
