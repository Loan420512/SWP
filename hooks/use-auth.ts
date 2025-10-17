"use client"

import { useState, useEffect } from "react"

interface User {
  fullName: string
  email: string
  userName: string
  role?: string
}

export function useAuth() {
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    // Check if user is logged in from localStorage
    const loggedIn = localStorage.getItem("isLoggedIn") === "true"
    const userData = localStorage.getItem("user")

    console.log("[v0] Auth check - isLoggedIn:", loggedIn)
    console.log("[v0] Auth check - userData:", userData)

    setIsLoggedIn(loggedIn)
    if (userData) {
      setUser(JSON.parse(userData))
    }
    setIsLoading(false)
  }, [])

  const login = (userData: User) => {
    localStorage.setItem("isLoggedIn", "true")
    localStorage.setItem("user", JSON.stringify(userData))
    setIsLoggedIn(true)
    setUser(userData)
    console.log("[v0] User logged in:", userData)
  }

  const logout = () => {
    localStorage.removeItem("isLoggedIn")
    localStorage.removeItem("user")
    setIsLoggedIn(false)
    setUser(null)
    console.log("[v0] User logged out")
  }

  return { isLoggedIn, user, login, logout, isLoading }
}
