import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import authentication from './authentication';
import models from './models';

export default combineReducers({
  authentication,
  form,
  models,
});
