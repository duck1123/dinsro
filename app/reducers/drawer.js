export function drawerHasClosed(state = false, action) {
  switch (action.type) {
    case 'CLOSE_DRAWER':
      return false;

    default:
      return state;
  }
}

export function drawerHasOpened(state = true, action) {
  switch (action.type) {
    case 'OPEN_DRAWER':
      return true;

    default:
      return state;
  }
}
