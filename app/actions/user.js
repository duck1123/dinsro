export const userHasErrored = (userId, bool) => {
  return {
    type: 'USER_HAS_ERRORED',
    userId,
    hasErrored: bool,
  };
};

export const userIsLoading = (userId, bool) => {
  return {
    type: 'USER_IS_LOADING',
    isLoading: bool,
    userId,
  };
};

export const userFetchDataSuccess = (userId, user) => {
  return {
    type: 'USER_FETCH_DATA_SUCCESS',
    userId,
    user,
  };
};

export const userFetchData = (userId, token) => {
  return (dispatch) => {
    dispatch(userIsLoading(userId, true));

    fetch(`/api/v1/users/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }).then((response) => {
      if (!response.ok) {
        throw Error(response.statusText);
      }

      return response;
    }).then(response => response.json())
      .then(user => dispatch(userFetchDataSuccess(userId, user)))
      .catch(() => dispatch(userHasErrored(userId, true)));
  };
};
