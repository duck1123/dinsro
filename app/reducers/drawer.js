export function drawerHasClosed(state = false, action) {
  switch (action.type) {
    case 'CLOSE_DRAWER':
      return false;

    default:
      return state;
  }
}

export function drawerToggled(state = true, action) {
  switch (action.type) {
    case 'TOGGLE_DRAWER':
      return action.open;

    default:
      return state;
  }
}
