import { createSignal, For, createEffect, onCleanup } from "solid-js";
import Button from "./Button";

// ---------------------------------------------------------------------------
// Game tuning knobs and board setup -- these are provided for you.
// ---------------------------------------------------------------------------
const ROUND_SECONDS = 20; // how long one round lasts (used in Part 2)
const BASE_DECOYS = 3;    // "Desert" decoys on the board at score 0
const MAX_DECOYS = 12;    // difficulty stops climbing here

// A random percentage in [10, 90] -- keeps buttons off the very edges.
const randomPercent = () => Math.random() * 80 + 10;

// How many decoys to show at a given score. The board gets busier as you
// score, which is what makes the game harder.
const decoyCount = (score) =>
  Math.min(MAX_DECOYS, BASE_DECOYS + Math.max(0, score));

// Build the whole board as plain DATA: one target dessert plus some decoys,
// each with a fresh random position. <For> turns this array into buttons.
function makeButtons(score) {
  const target = { id: 0, label: "Dessert", isTarget: true, x: randomPercent(), y: randomPercent() };
  const decoys = [];
  for (let i = 0; i < decoyCount(score); i++) {
    decoys.push({ id: i + 1, label: "Desert", isTarget: false, x: randomPercent(), y: randomPercent() });
  }
  return [target, ...decoys];
}

function App() {
  // -------------------------------------------------------------------------
  // TODO(part1): Make the score reactive.
  // Right now `score` is a plain number, so the display never changes. Replace
  // it with a SIGNAL so that reading it re-renders the score automatically:
  //     const [score, setScore] = createSignal(0);
  // Then read it as score() and update it with setScore(...) below.
  // -------------------------------------------------------------------------
  const score = 0;

  // The board's buttons, as reactive data. <For> renders one <Button> each.
  const [buttons, setButtons] = createSignal(makeButtons(0));

  // -------------------------------------------------------------------------
  // TODO(part1): Write the two click handlers.
  //   handleDessertClick(): the player found the dessert.
  //       - increase the score by 1
  //       - rebuild the board with setButtons(makeButtons(newScore))
  //         (a new score means an extra decoy and freshly shuffled positions)
  //   handleDesertClick(): the player hit a desert decoy.
  //       - decrease the score by 1, but never below 0
  //       - rebuild the board the same way
  // -------------------------------------------------------------------------

  // -------------------------------------------------------------------------
  // TODO(part2): Add the countdown timer and Game Over screen.
  //   1. Add signals for the time left and whether the round is running:
  //        const [timeLeft, setTimeLeft] = createSignal(ROUND_SECONDS);
  //        const [running, setRunning] = createSignal(true);
  //   2. Use createEffect + setInterval to subtract 1 from timeLeft every
  //      second while running is true, and onCleanup to clearInterval. When
  //      timeLeft reaches 0, set running to false.
  //   3. Show the timer in the HUD and a <Show> "Game Over" card on the board.
  //   4. A "Play again" button should reset the score, timer, and board.
  //   (Optional stretch: track a high score and persist it with localStorage.)
  // -------------------------------------------------------------------------

  return (
    <div class="game">
      <h1>Dessert in the Desert</h1>

      <div class="hud">
        {/* TODO(part1): show the reactive score. */}
        <span id="score">Score: {score}</span>
        {/* TODO(part2): show the countdown timer in a <span id="timer">. */}
      </div>

      <div class="board">
        <For each={buttons()}>
          {(b) => (
            <Button
              label={b.label}
              x={b.x}
              y={b.y}
              // TODO(part1): call the correct handler depending on b.isTarget
              // (the dessert scores a point; a desert costs one).
              onClick={() => {}}
            />
          )}
        </For>

        {/* TODO(part2): add a <Show when={!running()}> block here containing a
            "Game Over" card with the final score and a "Play again" button. */}
      </div>
    </div>
  );
}

export default App;
