export const userAccountsHaveErrored = (userId, bool = true) => {
  return {
    type: 'USER_ACCOUNTS_HAVE_ERRORED',
    errored: bool,
    userId,
  };
};

export const userAccountsAreLoading = (userId, bool = true) => {
  return {
    type: 'USER_ACCOUNTS_ARE_LOADING',
    isLoading: bool,
    userId,
  };
};

export const userAccountsFetchDataSuccess = (userId, accounts) => {
  return {
    type: 'USER_ACCOUNTS_FETCH_DATA_SUCCESS',
    accounts,
    userId,
  };
};

export const fetchUserAccounts = (userId, token) => {
  return (dispatch) => {
    dispatch(userAccountsAreLoading(userId));

    fetch(`/api/v1/users/${userId}/accounts`, {
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
      .then(accounts => dispatch(userAccountsFetchDataSuccess(userId, accounts)))
      .catch(() => dispatch(userAccountsHaveErrored(userId)));
  };
};
