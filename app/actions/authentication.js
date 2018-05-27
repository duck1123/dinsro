export const setAuthentication = (authData) => {
  return {
    type: 'AUTHENTICATION_CHANGED',
    email: authData ? authData.email : null,
    token: authData ? authData.token : null,
  };
};

export const removeAuthentication = () => {
  return setAuthentication({ email: null, token: null });
};
