import { createStore, applyMiddleware } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import thunk from 'redux-thunk';
import rootReducer from '../reducers';

const middleware = [
  thunk,
];

const enhancer = composeWithDevTools(applyMiddleware(...middleware));

export default function configureStore(initialState) {
  return createStore(rootReducer, initialState, enhancer);
}
