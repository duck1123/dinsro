import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import App from '../components/App';
import Header from '../components/Header';

const Root = () => {
  return (
    <Router>
      <div>
        <Header />
        <Route exact path="/" component={App} />
      </div>
    </Router>
  );
};

export default Root;
