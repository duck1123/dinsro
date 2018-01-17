export const accountHasErrored = (id, errored) => {
  return {
    type: 'MODEL_ERRORED',
    model: 'account',
    id,
    errored,
  };
};

export const accountIsLoading = (id, loading) => {
  return {
    type: 'MODEL_LOADING',
    model: 'account',
    id,
    loading,
  };
};

export const accountFetchDataSuccess = (id, data) => {
  return {
    type: 'MODEL_LOADED',
    model: 'account',
    id,
    data,
  };
};

export const fetchAccount = (id, token) => {
  return (dispatch) => {
    dispatch(accountIsLoading(id, true));

    fetch(`/api/v1/accounts/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((response) => {
      if (!response.ok) {
        throw Error(response.statusText);
      }

      return response;
    }).then(response => response.json())
      .then(data => dispatch(accountFetchDataSuccess(id, data)))
      .catch(() => dispatch(accountHasErrored(id, true)));
  };
};
