/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts}",
    "node_modules/flowbite/**/*.js"
  ],
  theme: {
    extend: {
      container: {
        center: true,
        padding: '2rem'
      }
    },
  },
  plugins: [
    require('flowbite/plugin')
  ],
}

