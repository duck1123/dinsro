import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import authentication from './authentication';
import { drawerToggled } from './drawer';
import user from './user';
import users from './users';

export default combineReducers({
  authentication,
  drawerToggled,
  form,
  user,
  users,
});
