export default (state = {}, action) => {
  const { id } = action;

  switch (action.type) {
    case 'MODEL_LOADED':
      return {
        ...state,
        [id]: {
          data: action.data,
          loading: false,
          errored: false,
        },
      };
    case 'MODEL_LOADING':
      return {
        ...state,
        [id]: {
          data: {},
          errored: false,
          loading: true,
        },
      };
    case 'MODEL_ERRORED':
      return {
        ...state,
        [id]: {
          data: {},
          errored: true,
          loading: false,
        },
      };
    default:
      return state;
  }
};
