export const accountHasErrored = (id, hasErrored) => {
  return {
    type: 'ACCOUNT_HAS_ERRORED',
    id,
    hasErrored,
  };
};

export const accountIsLoading = (id, isLoading) => {
  return {
    type: 'ACCOUNT_IS_LOADING',
    isLoading,
    id,
  };
};

export const accountFetchDataSuccess = (id, data) => {
  return {
    type: 'ACCOUNT_FETCH_DATA_SUCCESS',
    id,
    data,
  };
};

export const fetchAccount = (id, token) => {
  return (dispatch) => {
    dispatch(accountIsLoading(id, true));

    fetch(`/api/v1/account/${id}`, {
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
