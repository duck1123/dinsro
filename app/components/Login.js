import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import PropTypes from 'prop-types';
import React from 'react';
import { Link } from 'react-router-dom';

const styles = theme => ({
  button: {
    margin: theme.spacing.unit,
  },
});

const Login = (props) => {
  const { classes } = props;

  return (
    <Link to="/login" href="/login">
      <Button
        color={props.color}
        className={classes.button}
      >
        Login
      </Button>
    </Link>
  );
};

Login.propTypes = {
  color: PropTypes.string,
  classes: PropTypes.instanceOf(Object).isRequired,
};

Login.defaultProps = {
  color: 'contrast',
};

export default withStyles(styles)(Login);
