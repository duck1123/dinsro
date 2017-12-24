export function drawerHasClosed(state = false, action) {
  switch (action.type) {
    case 'CLOSE_DRAWER':
      return action.open;

    default:
      return state;
  }
}

export function drawerHasOpened(state = true, action) {
  switch (action.type) {
    case 'CLOSE_OPENED':
      return action.open;

    default:
      return state;
  }
}
