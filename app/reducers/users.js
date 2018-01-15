export default (state = {
  data: [],
  loading: false,
  errored: false,
}, action) => {
  switch (action.type) {
    case 'USERS_FETCH_DATA_SUCCESS':
      return {
        loading: false,
        errored: false,
        data: action.users,
      };

    case 'USERS_ARE_LOADING':
      return { data: [], loading: true, errored: false };

    case 'USERS_HAVE_ERRORED':
      return { data: [], loading: false, errored: true };

    default:
      return state;
  }
};
