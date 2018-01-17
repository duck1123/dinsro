export const collectionHasErrored = (model, errored) => {
  return {
    type: 'COLLECTION_ERRORED',
    model,
    errored,
  };
};

export const modelHasErrored = (model, id, errored) => {
  return {
    type: 'MODEL_ERRORED',
    model,
    id,
    errored,
  };
};

export const collectionIsLoading = (model, loading) => {
  return {
    type: 'COLLECTION_LOADING',
    model,
    loading,
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

export const collectionLoaded = (model, data) => {
  return {
    type: 'COLLECTION_LOADED',
    model,
    data,
  };
};

const getPath = (model, id) => {
  switch (model) {
    case 'account':
      return `/api/v1/accounts/${id}`;
    case 'transaction':
      return `/api/v1/transactions/${id}`;
    case 'user':
      return `/api/v1/users/${id}`;
    default:
      return 'NULL';
  }
}

const getCollectionPath = (model) => {
  switch (model) {
    case 'accounts':
      return '/api/v1/accounts';
    case 'user':
      return '/api/v1/users';
    default:
      return 'NULL';
  }
}

export const fetchCollection = (model, token) => {
  return (dispatch) => {
    dispatch(collectionIsLoading(model, true));

    fetch(getCollectionPath(model), {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((response) => {
      if (!response.ok) {
        throw Error(response.statusText);
      }

      return response;
    }).then(response => response.json())
      .then(data => dispatch(collectionLoaded(model, data)))
      .catch(() => dispatch(collectionHasErrored(model, true)));
  };
};

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
