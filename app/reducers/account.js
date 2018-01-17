export default (state = {}, action) => {
  switch (action.type) {
    case 'ACCOUNT_FETCH_DATA_SUCCESS':
      return {
        ...state,
        [action.accountId]: {
          data: action.account,
          loading: false,
          errored: false,
        },
      };
    case 'ACCOUNT_IS_LOADING':
      return {
        ...state,
        [action.accountId]: {
          data: {},
          errored: false,
          loading: true,
        },
      };
    case 'ACCOUNT_HAS_ERRORED':
      return {
        ...state,
        [action.accountId]: {
          data: {},
          errored: true,
          loading: false,
        },
      };
    default:
      return state;
  }
};
