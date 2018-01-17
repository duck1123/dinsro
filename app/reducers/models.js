export default (state = {}, action) => {
  const {
    id,
    model,
  } = action;

  const modelData = state[model] || {};

  switch (action.type) {
    case 'MODEL_LOADED':
      return {
        ...state,
        [model]: {
          ...modelData,
          [id]: {
            data: action.data,
            loading: false,
            errored: false,
          },
        },
      };
    case 'MODEL_LOADING':
      return {
        ...state,
        [model]: {
          ...modelData,
          [id]: {
            data: {},
            loading: true,
            errored: false,
          },
        },
      };
    case 'MODEL_ERRORED':
      return {
        ...state,
        [model]: {
          ...modelData,
          [id]: {
            data: {},
            loading: false,
            errored: true,
          },
        },
      };
    default:
      return state;
  }
};
