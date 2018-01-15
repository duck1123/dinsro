export const userTransactionsHaveErrored = (bool) => {
  return {
    type: 'USER_TRANSACTIONS_HAVE_ERRORED',
    errored: bool,
  };
};

export const userTransactionsAreLoading = (bool) => {
  return {
    type: 'USER_TRANSACTIONS_ARE_LOADING',
    isLoading: bool,
  };
};

export const userTransactionsFetchDataSuccess = (transactions) => {
  return {
    type: 'USER_TRANSACTIONS_FETCH_DATA_SUCCESS',
    transactions,
  };
};

export const fetchUserTransactions = (userId, token) => {
  return (dispatch) => {
    dispatch(userTransactionsAreLoading(true));

    fetch(`/api/v1/users/${userId}/transactions`, {
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
      .then(transactions => dispatch(userTransactionsFetchDataSuccess(transactions)))
      .catch(() => dispatch(userTransactionsHaveErrored(true)));
  };
};
