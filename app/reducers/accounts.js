export default (state = {
  data: [],
  loading: false,
  errored: false,
}, action) => {
  switch (action.type) {
    case 'ACCOUNTS_FETCH_DATA_SUCCESS':
      return {
        loading: false,
        errored: false,
        data: action.accounts,
      };

    case 'ACCOUNTS_ARE_LOADING':
      return { data: [], loading: true, errored: false };

    case 'ACCOUNTS_HAVE_ERRORED':
      return { data: [], loading: false, errored: true };

    default:
      return state;
  }
};
