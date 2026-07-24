import { defineConfig } from "vite";
import solid from "vite-plugin-solid";

// This game is entirely client-side: all of its state (score, timer, button
// positions) lives in the browser. Unlike the book-ratings site from lecture,
// there is no back-end web server to talk to, so there is no proxy to
// configure here -- just the Solid plugin.
export default defineConfig({
  plugins: [solid()],
});
