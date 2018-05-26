const defaultState = {
  data: [
    { id: 1 },
    { id: 2 },
    { id: 3 },
    { id: 4 },
  ],
};

export default (state = defaultState, action) => {
  switch (action.type) {
    case 'USER_ACCOUNTS_HAVE_ERRORED':
      return {
        data: [],
      };
    case 'USER_ACCOUNTS_ARE_LOADING':
      return {
        data: [],
      };
    case 'USER_ACCOUNTS_FETCH_DATA_SUCCESS':
      return {
        data: action.accounts,
      };
    default:
      return state;
  }
};
