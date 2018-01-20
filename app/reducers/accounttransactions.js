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
    case 'ACCOUNT_TRANSACTIONS_HAVE_ERRORED':
      return {
        data: [],
      };
    case 'ACCOUNT_TRANSACTIONS_ARE_LOADING':
      return {
        data: [],
      };
    case 'ACCOUNT_TRANSACTIONS_FETCH_DATA_SUCCESS':
      return {
        data: action.data,
      };
    default:
      return state;
  }
};
