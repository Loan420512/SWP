"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ArrowLeft, MapPin, Calendar, Clock, Battery, Zap } from "lucide-react"
import Link from "next/link"
import { useAuth } from "@/hooks/use-auth"
import { useRouter } from "next/navigation"

export default function BookingPage() {
  const { isLoggedIn, user, isLoading } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push("/signin")
    }
  }, [isLoggedIn, isLoading, router])

  const [bookingData, setBookingData] = useState({
    station: "",
    date: "",
    time: "",
    vehicleModel: "",
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setBookingData((prev) => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    alert("Booking submitted successfully!")
    console.log("Booking data:", bookingData)
  }

  if (isLoading) {
    return (
      <div className="min-h-screen bg-purple-50 flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-[#7241CE] border-t-transparent rounded-full animate-spin mx-auto mb-4"></div>
          <p className="text-gray-600">Loading...</p>
        </div>
      </div>
    )
  }

  if (!isLoggedIn) {
    return null
  }

  return (
    <div className="min-h-screen bg-purple-50">
      <header className="bg-[#7241CE] text-white py-6 shadow-lg">
        <div className="container mx-auto px-4">
          <div className="flex items-center gap-4">
            <Link href="/" className="flex items-center gap-2 hover:opacity-80 transition-opacity">
              <ArrowLeft className="w-5 h-5" />
              <span>Back to Home</span>
            </Link>
          </div>
          <h1 className="text-3xl font-bold mt-4">Book Your Battery Swap</h1>
          <p className="text-white/80 mt-2">Schedule your next battery swap in just a few clicks</p>
        </div>
      </header>

      <div className="container mx-auto px-4 py-12">
        <div className="grid lg:grid-cols-2 gap-8 max-w-6xl mx-auto">
          <Card className="shadow-xl">
            <CardHeader>
              <CardTitle className="text-2xl text-[#7241CE]">Swap Details</CardTitle>
              <CardDescription>Fill in your booking information</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label htmlFor="station" className="block text-sm font-medium text-gray-900 mb-2">
                    <MapPin className="w-4 h-4 inline mr-2" />
                    Select Station
                  </label>
                  <select
                    id="station"
                    name="station"
                    className="w-full h-12 px-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-[#7241CE] focus:border-transparent"
                    required
                    onChange={handleChange}
                    value={bookingData.station}
                  >
                    <option value="">Choose a station</option>
                    <option value="downtown">Downtown Station</option>
                    <option value="airport">Airport Station</option>
                    <option value="mall">Shopping Mall Station</option>
                    <option value="highway">Highway Station</option>
                  </select>
                </div>

                <div>
                  <label htmlFor="date" className="block text-sm font-medium text-gray-900 mb-2">
                    <Calendar className="w-4 h-4 inline mr-2" />
                    Date
                  </label>
                  <Input
                    id="date"
                    name="date"
                    type="date"
                    className="h-12"
                    required
                    onChange={handleChange}
                    value={bookingData.date}
                  />
                </div>

                <div>
                  <label htmlFor="time" className="block text-sm font-medium text-gray-900 mb-2">
                    <Clock className="w-4 h-4 inline mr-2" />
                    Time
                  </label>
                  <Input
                    id="time"
                    name="time"
                    type="time"
                    className="h-12"
                    required
                    onChange={handleChange}
                    value={bookingData.time}
                  />
                </div>

                <div>
                  <label htmlFor="vehicleModel" className="block text-sm font-medium text-gray-900 mb-2">
                    <Battery className="w-4 h-4 inline mr-2" />
                    Vehicle Model
                  </label>
                  <Input
                    id="vehicleModel"
                    name="vehicleModel"
                    type="text"
                    placeholder="e.g., Tesla Model 3"
                    className="h-12"
                    required
                    onChange={handleChange}
                    value={bookingData.vehicleModel}
                  />
                </div>

                <Button type="submit" className="w-full h-12 text-base bg-[#A2F200] text-black hover:bg-[#8fd600]">
                  <Zap className="w-5 h-5 mr-2" />
                  Confirm Booking
                </Button>
              </form>
            </CardContent>
          </Card>

          <div className="space-y-6">
            <Card className="bg-gradient-to-br from-[#7241CE] to-[#5f37b0] text-white shadow-xl">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Zap className="w-6 h-6 text-[#A2F200]" />
                  Fast Service
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-white/90">Complete battery swap in under 3 minutes. No waiting, no hassle.</p>
              </CardContent>
            </Card>

            <Card className="bg-gradient-to-br from-[#A2F200] to-[#8fd600] shadow-xl">
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <MapPin className="w-6 h-6" />
                  Convenient Locations
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-900">Find stations near you with our extensive network across the city.</p>
              </CardContent>
            </Card>

            <Card className="bg-white shadow-xl border-2 border-[#7241CE]">
              <CardHeader>
                <CardTitle className="flex items-center gap-2 text-[#7241CE]">
                  <Battery className="w-6 h-6" />
                  Quality Guaranteed
                </CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-gray-700">All batteries are tested and certified for optimal performance.</p>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  )
}
