export const modelHasErrored = (model, id, errored) => {
  return {
    type: 'MODEL_ERRORED',
    model,
    id,
    errored,
  };
};

export const modelIsLoading = (model, id, loading) => {
  return {
    type: 'MODEL_LOADING',
    model,
    id,
    loading,
  };
};

export const modelLoaded = (model, id, data) => {
  return {
    type: 'MODEL_LOADED',
    model,
    id,
    data,
  };
};

const getPath = (model, id) => {
  switch (model) {
    case 'account':
      return `/api/v1/accounts/${id}`;
    default:
      return 'NULL';
  }
}

export const fetchModel = (model, id, token) => {
  return (dispatch) => {
    dispatch(modelIsLoading(model, id, true));

    fetch(getPath(model, id), {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((response) => {
      if (!response.ok) {
        throw Error(response.statusText);
      }

      return response;
    }).then(response => response.json())
      .then(data => dispatch(modelLoaded(model, id, data)))
      .catch(() => dispatch(modelHasErrored(model, id, true)));
  };
};
