import { combineReducers } from 'redux';
import { reducer as form } from 'redux-form';
import authentication from './authentication';

export default combineReducers({
  authentication,
  form,
});
