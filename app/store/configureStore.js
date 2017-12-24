import { createStore, applyMiddleware } from 'redux';
import { composeWithDevTools } from 'redux-devtools-extension';
import reduxCookiesMiddleware, { getStateFromCookies } from 'redux-cookies-middleware';
import thunk from 'redux-thunk';
import rootReducer from '../reducers';

const paths = {
  'authentication.token': { name: 'dinsro_token' },
  'authentication.email': { name: 'dinsro_email' },
};

const middleware = [
  thunk,
  reduxCookiesMiddleware(paths),
];

const enhancer = composeWithDevTools(applyMiddleware(...middleware));

export default function configureStore(initialState = {
  authentication: {
    email: null,
    token: null,
  },
}) {
  const cookieState = getStateFromCookies(initialState, paths);
  return createStore(rootReducer, cookieState, enhancer);
}
