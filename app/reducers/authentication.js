export default function authentication(state = { email: null, token: null }, action) {
  switch (action.type) {
    case 'AUTHENTICATION_CHANGED':
      return {
        email: action.email,
        token: action.token,
      };

    default:
      return state;
  }
}
