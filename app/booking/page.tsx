"use client"

import type React from "react"
import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card } from "@/components/ui/card"
import { MapPin, Clock, Star, Filter, Menu, Home, User, History, Zap, LifeBuoy, LogOut } from "lucide-react"
import { useAuth } from "@/hooks/use-auth"
import { useRouter } from "next/navigation"

interface Station {
  id: string
  name: string
  address: string
  available: number
  total: number
  time: string
  distance: string
  price: number
  rating: number
  status: "open" | "maintenance" | "closed"
}

const mockStations: Station[] = [
  {
    id: "1",
    name: "Downtown Hub",
    address: "123 Main St, City Center",
    available: 12,
    total: 20,
    time: "< 5 min",
    distance: "0.8 km",
    price: 25,
    rating: 4.8,
    status: "open",
  },
  {
    id: "2",
    name: "Mall Station",
    address: "456 Shopping Ave",
    available: 8,
    total: 15,
    time: "10-15 min",
    distance: "1.2 km",
    price: 25,
    rating: 4.6,
    status: "open",
  },
  {
    id: "3",
    name: "Airport Terminal",
    address: "789 Airport Rd",
    available: 0,
    total: 25,
    time: "Closed",
    distance: "5.4 km",
    price: 30,
    rating: 4.9,
    status: "maintenance",
  },
]

export default function FindStationsPage() {
  const { isLoggedIn, user, isLoading, logout } = useAuth()
  const router = useRouter()
  const [searchQuery, setSearchQuery] = useState("")
  const [sidebarOpen, setSidebarOpen] = useState(true)
  const [showUserMenu, setShowUserMenu] = useState(false)

  const handleLogout = () => {
    logout()
  }

  useEffect(() => {
    if (!isLoading && !isLoggedIn) {
      router.push("/signin")
    }
  }, [isLoggedIn, isLoading, router])

  const handleBackHome = () => {
    router.push("/")
  }

  const filteredStations = mockStations.filter(
    (station) =>
      station.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      station.address.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
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
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <div
        className={`${sidebarOpen ? "w-64" : "w-20"} bg-white border-r border-gray-200 transition-all duration-300 flex flex-col`}
      >
        <div className="p-4 border-b border-gray-200">
          <div className="flex items-center justify-between">
            {sidebarOpen && <h1 className="text-xl font-bold text-[#7241CE]">EV Driver</h1>}
            <button onClick={() => setSidebarOpen(!sidebarOpen)} className="p-2 hover:bg-gray-100 rounded-lg">
              <Menu className="w-5 h-5" />
            </button>
          </div>
        </div>

        <nav className="flex-1 p-4 space-y-2">
          <NavItem icon={<MapPin className="w-5 h-5" />} label="Find Stations" active sidebarOpen={sidebarOpen} />
          <NavItem icon={<Zap className="w-5 h-5" />} label="Swap" sidebarOpen={sidebarOpen} />
          <NavItem icon={<History className="w-5 h-5" />} label="History" sidebarOpen={sidebarOpen} />
          <NavItem icon={<User className="w-5 h-5" />} label="Profile" sidebarOpen={sidebarOpen} />
          <NavItem icon={<LifeBuoy className="w-5 h-5" />} label="Support" sidebarOpen={sidebarOpen} />
        </nav>

        {sidebarOpen && (
          <div className="p-4 border-t border-gray-200">
            <button
              onClick={handleBackHome}
              className="w-full flex items-center gap-2 px-4 py-2 text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
            >
              <Home className="w-5 h-5" />
              <span>Back to Home</span>
            </button>
          </div>
        )}
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <div className="relative border-b border-[#8B5FE8] bg-[#7241CE]/95">
          <div className="absolute inset-0 opacity-10">
            <div
              className="absolute inset-0"
              style={{
                backgroundImage:
                  "linear-gradient(#A2F200 1px, transparent 1px), linear-gradient(90deg, #A2F200 1px, transparent 1px)",
                backgroundSize: "50px 50px",
              }}
            />
            <div
              className="absolute inset-0"
              style={{
                backgroundImage:
                  "repeating-linear-gradient(45deg, transparent, transparent 35px, #A2F200 35px, #A2F200 36px)",
              }}
            />
            <div
              className="absolute inset-0"
              style={{
                backgroundImage: "radial-gradient(circle, #A2F200 1px, transparent 1px)",
                backgroundSize: "30px 30px",
              }}
            />
          </div>

          <div className="absolute -right-20 -top-20 w-40 h-40 rounded-full bg-[#A2F200]/5 blur-3xl" />
          <div className="absolute -left-20 top-0 w-32 h-32 rounded-full bg-[#A2F200]/5 blur-2xl" />

          <div className="px-8 py-6 flex items-center justify-between relative z-10">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-[#A2F200] flex items-center justify-center">
                <Zap className="w-6 h-6 text-black" />
              </div>
              <h2 className="text-2xl font-bold text-white">EVSwap</h2>
            </div>

            <div className="flex items-center gap-4">
              <div className="relative">
                <button
                  onClick={() => setShowUserMenu(!showUserMenu)}
                  className="w-10 h-10 rounded-full bg-[#A2F200] text-black flex items-center justify-center font-bold hover:bg-[#8fd600] transition-colors"
                >
                  {user?.fullName?.charAt(0).toUpperCase() || "U"}
                </button>
                {showUserMenu && (
                  <div className="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
                    <div className="p-4 border-b border-gray-200">
                      <p className="font-semibold text-gray-900">{user?.fullName || "User"}</p>
                      <p className="text-sm text-gray-500">{user?.email || "user@example.com"}</p>
                    </div>
                    <button
                      onClick={handleLogout}
                      className="w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 transition-colors flex items-center gap-2"
                    >
                      <LogOut className="w-4 h-4" />
                      Logout
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-auto">
          <div className="grid grid-cols-4 gap-6 p-8 h-full">
            {/* Map Section */}
            <div className="col-span-2">
              <Card className="h-80 bg-gradient-to-br from-[#E8F5E9] to-[#F1F8E9] border-0 shadow-lg flex flex-col items-center justify-center relative overflow-hidden">
                <div className="absolute top-20 left-20 w-3 h-3 rounded-full bg-[#A2F200]"></div>
                <div className="absolute bottom-32 right-16 w-3 h-3 rounded-full bg-red-500"></div>
                <div className="absolute top-1/3 right-1/4 w-3 h-3 rounded-full bg-[#A2F200]"></div>

                <div className="text-center">
                  <div className="w-16 h-16 rounded-full border-4 border-[#7241CE] flex items-center justify-center mx-auto mb-4">
                    <MapPin className="w-8 h-8 text-[#7241CE]" />
                  </div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">Nearby Stations</h3>
                  <p className="text-sm text-gray-600 mb-6">Find and reserve battery swap stations</p>
                  <Button className="bg-[#A2F200] text-black hover:bg-[#8fd600]">
                    <MapPin className="w-4 h-4 mr-2" />
                    Use My Location
                  </Button>
                </div>
                <p className="absolute bottom-4 text-xs text-gray-500">Interactive map with real-time availability</p>
              </Card>
            </div>

            {/* Stations List */}
            <div className="col-span-2 flex flex-col">
              <div className="mb-4">
                <div className="flex items-center gap-2 mb-3">
                  <Button variant="outline" size="sm" className="gap-2 bg-transparent">
                    <Filter className="w-4 h-4" />
                    Filter
                  </Button>
                </div>
                <Input
                  placeholder="Search stations..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full"
                />
              </div>

              <div className="flex-1 overflow-auto space-y-3">
                <h3 className="font-semibold text-gray-900 text-sm">Available Stations</h3>
                {filteredStations.map((station) => (
                  <Card key={station.id} className="p-4 hover:shadow-md transition-shadow">
                    <div className="flex items-start justify-between mb-2">
                      <div>
                        <h4 className="font-semibold text-gray-900 text-sm">{station.name}</h4>
                        <p className="text-xs text-gray-500">{station.address}</p>
                      </div>
                      {station.status === "open" && (
                        <span className="px-2 py-1 bg-[#A2F200] text-black text-xs rounded font-medium">open</span>
                      )}
                      {station.status === "maintenance" && (
                        <span className="px-2 py-1 bg-red-500 text-white text-xs rounded font-medium">maintenance</span>
                      )}
                    </div>

                    <div className="space-y-2 mb-3">
                      <div className="flex items-center gap-2 text-xs">
                        <Zap className="w-3 h-3 text-[#A2F200]" />
                        <span className="text-gray-700">
                          {station.available}/{station.total} available
                        </span>
                      </div>
                      <div className="flex items-center gap-2 text-xs">
                        <Clock className="w-3 h-3 text-gray-400" />
                        <span className="text-gray-600">{station.time}</span>
                      </div>
                      <div className="flex items-center gap-2 text-xs">
                        <MapPin className="w-3 h-3 text-gray-400" />
                        <span className="text-gray-600">{station.distance}</span>
                      </div>
                    </div>

                    <div className="flex items-center justify-between mb-3">
                      <span className="font-semibold text-gray-900">${station.price}/swap</span>
                      <div className="flex items-center gap-1">
                        <Star className="w-3 h-3 fill-yellow-400 text-yellow-400" />
                        <span className="text-xs text-gray-600">{station.rating}</span>
                      </div>
                    </div>

                    <Button className="w-full bg-[#A2F200] text-black hover:bg-[#8fd600] h-8 text-xs">Reserve</Button>
                  </Card>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

function NavItem({
  icon,
  label,
  active,
  sidebarOpen,
}: {
  icon: React.ReactNode
  label: string
  active?: boolean
  sidebarOpen: boolean
}) {
  return (
    <button
      className={`w-full flex items-center gap-3 px-4 py-2 rounded-lg transition-colors ${
        active ? "bg-[#7241CE]/20 text-[#7241CE] border border-[#7241CE]" : "text-gray-700 hover:bg-gray-100"
      }`}
    >
      {icon}
      {sidebarOpen && <span className="text-sm font-medium">{label}</span>}
    </button>
  )
}
