import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import App from '../containers/App';
import Header from '../components/Header';
import ListUsers from '../components/ListUsers';
import LoginPage from '../components/LoginPage';

const Root = () => {
  return (
    <Router>
      <div>
        <Header />
        <Route exact path="/" component={App} />
        <Route exact path="/login" component={LoginPage} />
        <Route exact path="/users" component={ListUsers} />
      </div>
    </Router>
  );
};

export default Root;
