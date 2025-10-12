import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Globe, Zap } from "lucide-react"

export function Header() {
  return (
  <header className="fixed top-0 left-0 right-0 z-50 border-b border-[#8B5FE8] overflow-hidden">
      <div className="absolute inset-0 bg-[url('/modern-electric-vehicle-charging-station-technolog.jpg')] bg-cover bg-center opacity-20" />
      <div className="absolute inset-0 bg-[#7241CE]/95" />

      {/* Decorative geometric patterns */}
      <div className="absolute inset-0 opacity-10">
        {/* Grid pattern */}
        <div
          className="absolute inset-0"
          style={{
            backgroundImage:
              "linear-gradient(#A2F200 1px, transparent 1px), linear-gradient(90deg, #A2F200 1px, transparent 1px)",
            backgroundSize: "50px 50px",
          }}
        />

        {/* Diagonal lines */}
        <div
          className="absolute inset-0"
          style={{
            backgroundImage:
              "repeating-linear-gradient(45deg, transparent, transparent 35px, #A2F200 35px, #A2F200 36px)",
          }}
        />

        {/* Dots pattern */}
        <div
          className="absolute inset-0"
          style={{
            backgroundImage: "radial-gradient(circle, #A2F200 1px, transparent 1px)",
            backgroundSize: "30px 30px",
          }}
        />
      </div>

      {/* Decorative circles */}
      <div className="absolute -right-20 -top-20 w-40 h-40 rounded-full bg-[#A2F200]/5 blur-3xl" />
      <div className="absolute -left-20 top-0 w-32 h-32 rounded-full bg-[#A2F200]/5 blur-2xl" />
      {/* </CHANGE> */}

      <div className="container mx-auto px-4 lg:px-8 relative z-10">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-full bg-[#A2F200] flex items-center justify-center">
              <Zap className="w-5 h-5 text-black" />
            </div>
            <span className="text-xl font-semibold text-white">EVSwap</span>
          </Link>

          <nav className="hidden md:flex items-center gap-8">
            <Link href="#features" className="text-sm text-white/90 hover:text-[#A2F200] transition-colors">
              Features
            </Link>
            <Link href="#pricing" className="text-sm text-white/90 hover:text-[#A2F200] transition-colors">
              Pricing
            </Link>
            <Link href="#stations" className="text-sm text-white/90 hover:text-[#A2F200] transition-colors">
              Stations
            </Link>
            <Link href="#contact" className="text-sm text-white/90 hover:text-[#A2F200] transition-colors">
              Contact
            </Link>
          </nav>

          <div className="flex items-center gap-4">
            <button className="flex items-center gap-2 text-sm text-white/80 hover:text-white transition-colors">
              <Globe className="w-4 h-4" />
              <span className="hidden sm:inline">EN</span>
            </button>
            <Button variant="default" size="sm" className="bg-[#A2F200] text-black hover:bg-[#8fd600]" asChild>
              <Link href="/signin">Sign In</Link>
            </Button>
          </div>
        </div>
      </div>
    </header>
  )
}
