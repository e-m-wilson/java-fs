import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
//import mkcert from 'vite-plugin-mkcert'

// commented out mkcert to force the app to run in http
// this is just because our backend sets SameSite=Lax
// but the backend runs over http NOT https, so it fails 

// https://vite.dev/config/
export default defineConfig({
  server: {
    port: 3000
  },
  plugins: [
    react(),
    //mkcert(),
    babel({ presets: [reactCompilerPreset()] })
  ],
})
