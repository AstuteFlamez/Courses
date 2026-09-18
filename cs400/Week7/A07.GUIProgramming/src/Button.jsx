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
      style={`left: ${props.x}%; top: ${props.y}%;`}
    >
      {props.label}
    </button>
  );
}

export default Button;
