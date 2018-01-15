export default (state = {}, action) => {
  switch (action.type) {
    case 'USER_FETCH_DATA_SUCCESS':
      return {
        ...state,
        [action.userId]: {
          data: action.user,
          loading: false,
          errored: false,
        },
      };
    case 'USER_IS_LOADING':
      return {
        ...state,
        [action.userId]: {
          data: {},
          errored: false,
          loading: true,
        },
      };
    case 'USER_HAS_ERRORED':
      return {
        ...state,
        [action.userId]: {
          data: {},
          errored: true,
          loading: false,
        },
      };
    default:
      return state;
  }
};
