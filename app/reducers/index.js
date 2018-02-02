import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import accountTransactions from './accounttransactions';
import authentication from './authentication';
import models from './models';
import transactions from './transactions';
import useraccounts from './useraccounts';

export default combineReducers({
  accountTransactions,
  authentication,
  form,
  models,
  transactions,
  useraccounts,
});
