import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import App from '../components/App';
import Header from '../components/Header';
import LoginPage from '../components/LoginPage';

const Root = () => {
  return (
    <Router>
      <div>
        <Header />
        <Route exact path="/" component={App} />
        <Route exact path="/login" component={LoginPage} />
      </div>
    </Router>
  );
};

export default Root;
