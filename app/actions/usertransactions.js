export const userTransactionsHaveErrored = (userId, bool = true) => {
  return {
    type: 'USER_TRANSACTIONS_HAVE_ERRORED',
    errored: bool,
    userId,
  };
};

export const userTransactionsAreLoading = (userId, bool = true) => {
  return {
    type: 'USER_TRANSACTIONS_ARE_LOADING',
    isLoading: bool,
    userId,
  };
};

export const userTransactionsFetchDataSuccess = (userId, transactions) => {
  return {
    type: 'USER_TRANSACTIONS_FETCH_DATA_SUCCESS',
    transactions,
    userId,
  };
};

export const fetchUserTransactions = (userId, token) => {
  return (dispatch) => {
    dispatch(userTransactionsAreLoading(userId));

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
      .then(transactions => dispatch(userTransactionsFetchDataSuccess(userId, transactions)))
      .catch(() => dispatch(userTransactionsHaveErrored(userId)));
  };
};
