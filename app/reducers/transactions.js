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
    case 'USER_TRANSACTIONS_HAVE_ERRORED':
      return {
        data: [],
      };
    case 'USER_TRANSACTIONS_ARE_LOADING':
      return {
        data: [],
      };
    case 'USER_TRANSACTIONS_FETCH_DATA_SUCCESS':
      return {
        data: action.transactions,
      };
    default:
      return state;
  }
};
