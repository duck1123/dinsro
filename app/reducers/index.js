import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import account from './account';
import accountTransactions from './accounttransactions';
import accounts from './accounts';
import authentication from './authentication';
import models from './models';
import transactions from './transactions';
import user from './user';
import useraccounts from './useraccounts';
import users from './users';

export default combineReducers({
  account,
  accountTransactions,
  accounts,
  authentication,
  form,
  models,
  transactions,
  user,
  useraccounts,
  users,
});
