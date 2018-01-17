export const accountsHaveErrored = (bool = true) => {
  return {
    type: 'ACCOUNTS_HAVE_ERRORED',
    errored: bool,
  };
};

export const accountsAreLoading = (bool = true) => {
  return {
    type: 'ACCOUNTS_ARE_LOADING',
    isLoading: bool,
  };
};

export const accountsFetchDataSuccess = (accounts) => {
  return {
    type: 'ACCOUNTS_FETCH_DATA_SUCCESS',
    accounts,
  };
};

export const fetchAccounts = (token) => {
  return (dispatch) => {
    dispatch(accountsAreLoading());

    fetch('/api/v1/accounts', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw Error(response.statusText);
        }

        return response;
      })
      .then(response => response.json())
      .then(accounts => dispatch(accountsFetchDataSuccess(accounts)))
      .catch(() => dispatch(accountsHaveErrored()));
  };
};
