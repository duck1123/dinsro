import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React from 'react';
import { BrowserRouter as Router, Route } from 'react-router-dom';
import App from '../components/App';

const Root = () => {
  return (
    <MuiThemeProvider>
      <Router>
        <div>
          <Route exact path="/" component={App} />
        </div>
      </Router>
    </MuiThemeProvider>
  );
};

export default Root;
