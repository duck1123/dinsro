import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { setAuthentication } from '../actions/authentication';

const styles = theme => ({
  button: {
    margin: theme.spacing.unit,
  },
  input: {
    display: 'none',
  },
});

const Login = (props) => {
  const {
    authEmail,
    classes,
    color,
    logout,
  } = props;

  if (authEmail) {
    return (
      <Button
        className={classes.button}
        color={color}
        onClick={logout}
      >
        Logout
      </Button>
    );
  }

  return (
    <Link to="/login">
      <Button
        className={classes.button}
        color={props.color}
      >
        Login
      </Button>
    </Link>
  );
};

Login.propTypes = {
  authEmail: PropTypes.string,
  classes: PropTypes.instanceOf(Object).isRequired,
  color: PropTypes.string,
  logout: PropTypes.func.isRequired,
};

Login.defaultProps = {
  authEmail: null,
  color: 'secondary',
};

const mapStateToProps = (state) => {
  return {
    authEmail: state.authentication.email,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    logout: () => dispatch(setAuthentication(null)),
  };
};

const StyledLogin = withStyles(styles)(Login);
const ConnectedLogin = connect(mapStateToProps, mapDispatchToProps)(StyledLogin);

export default ConnectedLogin;
