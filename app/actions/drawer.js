export const closeDrawerAction = () => {
  return {
    type: 'CLOSE_DRAWER',
  };
};

export const openDrawerAction = (action) => {
  return {
    type: 'OPEN_DRAWER',
    action,
  };
};
