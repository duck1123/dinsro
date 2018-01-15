export const usersHaveErrored = (bool) => {
  return {
    type: 'USERS_HAVE_ERRORED',
    hasErrored: bool,
  };
};

export const usersAreLoading = (bool) => {
  return {
    type: 'USERS_ARE_LOADING',
    isLoading: bool,
  };
};

export const usersFetchDataSuccess = (users) => {
  return {
    type: 'USERS_FETCH_DATA_SUCCESS',
    users,
  };
};

export const usersFetchData = (token) => {
  return (dispatch) => {
    dispatch(usersAreLoading(true));

    fetch('/api/v1/users', {
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
      .then(users => dispatch(usersFetchDataSuccess(users)))
      .catch(() => dispatch(usersHaveErrored(true)));
  };
};
