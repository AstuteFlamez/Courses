// A presentational COMPONENT, it takes props and returns markup, with no state
// of its own. The parent decides where the button goes (x, y) and what happens
// when it is clicked (onClick).
//
// Do NOT destructure props (e.g. function Button({ x, y })). In Solid props is
// a live, tracked object -- read each field as props.x, props.y, props.label.
function Button(props) {
  return (
    <button
      onClick={props.onClick}
      // TODO(part1): position the button from state. props.x and props.y are
      // percentages (0-100). Give the button an inline `style` that sets its
      // `left` and `top` from those values so each button lands where its data
      // says it should. Until you do this, every button stacks in one corner.
    >
      {props.label}
    </button>
  );
}

export default Button;
