import { createSignal, For, Show, createEffect, onCleanup } from "solid-js";
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
  const [score, setScore] = createSignal(0);

  const [buttons, setButtons] = createSignal(makeButtons(0));

  const [timeLeft, setTimeLeft] = createSignal(ROUND_SECONDS);
  const [running, setRunning] = createSignal(true);

  const [highScore, setHighScore] = createSignal(
    Number(localStorage.getItem("dessertGameScore")) || 0
  );

  const handleDessertClick = () => {
    if (!running()) return;
    const newScore = score() + 1;
    setScore(newScore);
    setButtons(makeButtons(newScore));
  };

  const handleDesertClick = () => {
    if (!running()) return;
    const newScore = score() - 1;
    setScore(newScore);
    setButtons(makeButtons(newScore));
  };

  const playAgain = () => {
    setTimeLeft(ROUND_SECONDS);
    setScore(0);
    setButtons(makeButtons(0));
    setRunning(true);
  };

  createEffect(() => {
    if (!running()) return;

    const intervalId = setInterval(() => {
      setTimeLeft((t) => {
        if (t <= 1) {
          setRunning(false);
          return 0;
        }
        return t - 1;
      });
    }, 1000);

    onCleanup(() => clearInterval(intervalId));
  });

  createEffect(() => {
    if (score() > highScore()) {
      setHighScore(score());
    }
  });

  createEffect(() => {
    localStorage.setItem("dessertGameScore", String(highScore()));
  });

  return (
    <div class="game">
      <h1>Dessert in the Desert</h1>

      <div class="hud">
        <span id="score">Score: {score()}</span>
        <span id="timer" class={timeLeft() <= 5 ? "low" : ""}>
          Time: {timeLeft()}
        </span>
      </div>

      <div class="board">
        <For each={buttons()}>
          {(b) => (
            <Button
              label={b.label}
              x={b.x}
              y={b.y}
              onClick={b.isTarget ? handleDessertClick : handleDesertClick}
            />
          )}
        </For>

        <Show when={!running()}>
          <div class="gameover">
            <h2>Time's Up!</h2>
            <p>Final score: {score()}</p>
            <p>High score: {highScore()}</p>
            <button onClick={playAgain}>Play Again</button>
          </div>
        </Show>
      </div>
    </div>
  );
}

export default App;
