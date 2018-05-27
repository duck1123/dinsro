import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import authentication from './authentication';
import user from './user';
import users from './users';

export default combineReducers({
  authentication,
  form,
  user,
  users,
});
