import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import App from '../components/App';
import Header from '../components/Header';

const Root = () => {
  return (
    <MuiThemeProvider>
      <Router>
        <div>
          <Header />
          <Route exact path="/" component={App} />
        </div>
      </Router>
    </MuiThemeProvider>
  );
};

export default Root;
