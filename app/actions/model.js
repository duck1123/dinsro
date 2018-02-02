// Errored

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

export const subcollectionHasErrored = (model, id, errored) => {
  return {
    type: 'SUBCOLLECTION_ERRORED',
    id,
    model,
    errored,
  };
};

// Loading

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

export const subcollectionIsLoading = (model, id, loading) => {
  return {
    type: 'SUBCOLLECTION_LOADING',
    id,
    model,
    loading,
  };
};

// Loaded

export const collectionLoaded = (model, data) => {
  return {
    type: 'COLLECTION_LOADED',
    model,
    data,
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

export const subcollectionLoaded = (model, id, data) => {
  return {
    data,
    id,
    model,
    type: 'SUBCOLLECTION_LOADED',
  };
};

// Paths

const getCollectionPath = (model) => {
  switch (model) {
    case 'accounts':
      return '/api/v1/accounts';
    case 'transactions':
      return '/api/v1/transactions';
    case 'users':
      return '/api/v1/users';
    default:
      throw new Error(`No path defined for collection: ${model}`);
  }
};

const getModelPath = (model, id) => {
  switch (model) {
    case 'account':
      return `/api/v1/accounts/${id}`;
    case 'transaction':
      return `/api/v1/transactions/${id}`;
    case 'user':
      return `/api/v1/users/${id}`;
    default:
      throw new Error(`No path defined for model: ${model}(${id})`);
  }
};

const getSubcollectionPath = (model, id) => {
  switch (model) {
    case 'accountTransactions':
      return `/api/v1/accounts/${id}/transactions`;
    case 'userAccounts':
      return `/api/v1/users/${id}/accounts`;
    case 'userTransactions':
      return `/api/v1/users/${id}/transactions`;
    default:
      throw new Error(`No path defined for subcollection: ${model}(${id})`);
  }
};

// Fetch

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

    fetch(getModelPath(model, id), {
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

export const fetchSubcollection = (model, id, token) => {
  return (dispatch) => {
    dispatch(subcollectionIsLoading(model, id, true));

    fetch(getSubcollectionPath(model, id), {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((response) => {
      if (!response.ok) {
        throw Error(response.statusText);
      }

      return response;
    }).then(response => response.json())
      .then(data => dispatch(subcollectionLoaded(model, id, data)))
      .catch(() => dispatch(subcollectionHasErrored(model, id, true)));
  };
};
