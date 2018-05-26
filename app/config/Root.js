import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import { connectedRouterRedirect } from 'redux-auth-wrapper/history4/redirect';
import App from '../containers/App';
import Header from '../components/Header';
import ListAccounts from '../components/ListAccounts';
import ListTransactions from '../components/ListTransactions';
import ListUsers from '../components/ListUsers';
import LoginPage from '../components/LoginPage';
import ShowAccount from '../components/ShowAccount';
import ShowTransaction from '../components/ShowTransaction';
import ShowUser from '../components/ShowUser';

const userIsAuthenticated = connectedRouterRedirect({
  // The url to redirect user to if they fail
  redirectPath: '/login',
  // If selector is true, wrapper will not redirect
  // For example let's check that state contains user data
  authenticatedSelector: state => state.authentication.authEmail !== null,
  // A nice display name for this check
  wrapperDisplayName: 'UserIsAuthenticated',
});

const Root = () => {
  return (
    <Router>
      <div>
        <Header />
        <Route exact path="/" component={App} />
        <Route exact path="/accounts" component={ListAccounts} />
        <Route exact path="/accounts/:id" component={ShowAccount} />
        <Route exact path="/login" component={LoginPage} />
        <Route exact path="/transactions" component={ListTransactions} />
        <Route exact path="/transactions/:id" component={ShowTransaction} />
        <Route exact path="/users" component={userIsAuthenticated(ListUsers)} />
        <Route path="/users/:id" component={ShowUser} />
      </div>
    </Router>
  );
};

export default Root;
