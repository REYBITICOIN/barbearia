/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        barber: {
          gold: '#D4AF37',
          goldDark: '#B38F22',
          dark: '#0A0A0C',
          card: '#121316',
          cardBorder: '#22242B',
          cyan: '#06B6D4'
        }
      },
      fontFamily: {
        sans: ['Plus Jakarta Sans', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
