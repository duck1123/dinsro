export const accountTransactionsHaveErrored = (id, bool = true) => {
  return {
    type: 'USER_TRANSACTIONS_HAVE_ERRORED',
    errored: bool,
    id,
  };
};

export const accountTransactionsAreLoading = (id, bool = true) => {
  return {
    type: 'ACCOUNT_TRANSACTIONS_ARE_LOADING',
    isLoading: bool,
    id,
  };
};

export const accountTransactionsFetchDataSuccess = (id, data) => {
  return {
    type: 'ACCOUNT_TRANSACTIONS_FETCH_DATA_SUCCESS',
    data,
    id,
  };
};

export const fetchAccountTransactions = (id, token) => {
  return (dispatch) => {
    dispatch(accountTransactionsAreLoading(id));

    fetch(`/api/v1/account/${id}/transactions`, {
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
      .then(data => dispatch(accountTransactionsFetchDataSuccess(id, data)))
      .catch(() => dispatch(accountTransactionsHaveErrored(id)));
  };
};
