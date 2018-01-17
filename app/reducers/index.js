import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import account from './account';
import accounts from './accounts';
import authentication from './authentication';
import transactions from './transactions';
import user from './user';
import useraccounts from './useraccounts';
import users from './users';

export default combineReducers({
  account,
  accounts,
  authentication,
  form,
  transactions,
  user,
  useraccounts,
  users,
});
