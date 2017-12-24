import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import authentication from './authentication';
import {
  drawerHasClosed,
  drawerHasOpened,
} from './drawer';
import user from './user';
import users from './users';

export default combineReducers({
  authentication,
  drawerHasClosed,
  drawerHasOpened,
  form,
  user,
  users,
});
