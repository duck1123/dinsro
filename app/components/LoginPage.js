import { withStyles } from 'material-ui/styles';
import Grid from 'material-ui/Grid';
import Paper from 'material-ui/Paper';
import PropTypes from 'prop-types';
import React from 'react';
import LoginForm from './LoginForm';

const styles = {
  root: {
    flexGrow: 1,
    height: 500,
  },
};

const LoginPage = (props) => {
  const { classes } = props;
  return (
    <div>
      <Grid
        container
        alignItems="center"
        justify="center"
        className={classes.root}
      >
        <Grid item xs={3}>
          <Paper>
            <LoginForm />
          </Paper>
        </Grid>
      </Grid>
    </div>
  );
};

LoginPage.propTypes = {
  classes: PropTypes.shape({
    root: PropTypes.string,
  }).isRequired,
};

export default withStyles(styles)(LoginPage);
