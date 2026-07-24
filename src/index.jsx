/* @refresh reload */
import { render } from "solid-js/web";
import App from "./App";
import "./index.css";

// render(component, container) mounts the app. Note we pass a *function* that
// returns <App/> -- Solid calls it once to build the reactive graph.
render(() => <App />, document.getElementById("root"));
