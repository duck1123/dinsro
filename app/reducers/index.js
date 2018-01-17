import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import accounts from './accounts';
import authentication from './authentication';
import transactions from './transactions';
import user from './user';
import users from './users';

export default combineReducers({
  accounts,
  authentication,
  form,
  transactions,
  user,
  users,
});
