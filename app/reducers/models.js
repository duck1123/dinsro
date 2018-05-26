export default (state = {}, action) => {
  const {
    id,
    model,
  } = action;

  const modelData = state[model] || {};

  switch (action.type) {
    case 'COLLECTION_LOADED':
      return {
        ...state,
        [model]: {
          data: action.data,
          loading: false,
          errored: false,
        },
      };
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
    case 'SUBCOLLECTION_LOADED':
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
    case 'COLLECTION_LOADING':
      return {
        ...state,
        [model]: {
          data: [],
          loading: true,
          errored: false,
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
    case 'SUBCOLLECTION_LOADING':
      return {
        ...state,
        [model]: {
          ...modelData,
          [id]: {
            data: [],
            loading: true,
            errored: false,
          },
        },
      };
    case 'COLLECTION_ERRORED':
      return {
        ...state,
        [model]: {
          data: [],
          loading: false,
          errored: true,
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
    case 'SUBCOLLECTION_ERRORED':
      return {
        ...state,
        [model]: {
          ...modelData,
          [id]: {
            data: [],
            loading: false,
            errored: true,
          },
        },
      };
    default:
      return state;
  }
};
