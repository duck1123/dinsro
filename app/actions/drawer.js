export const closeDrawerAction = () => {
  return {
    type: 'CLOSE_DRAWER',
    open: false,
  };
};

export const openDrawerAction = () => {
  return {
    type: 'OPEN_DRAWER',
    open: true,
  };
};
