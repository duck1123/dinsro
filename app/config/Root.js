import Paper from 'material-ui/Paper';
import MuiThemeProvider from 'material-ui/styles/MuiThemeProvider';
import React from 'react';

const Root = () => {
  return (
    <MuiThemeProvider>
      <Paper>
        <p>Root Component</p>
      </Paper>
    </MuiThemeProvider>
  );
};

export default Root;
