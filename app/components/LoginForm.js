import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import Grid from 'material-ui/Grid';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import {
  Field,
  reduxForm,
  propTypes,
  SubmissionError,
} from 'redux-form';
import { TextField } from 'redux-form-material-ui';
import { setAuthentication } from '../actions/authentication';

const formData = {
  form: 'login',
  initialValues: {
    email: '',
    password: '',
  },
};

const styles = {
  container: {
    display: 'flex',
    flexWrap: 'wrap',
    padding: 15,
  },
  headerGrid: {
    textAlign: 'center',
  },
  inputField: {
    width: '100%',
  },
  submitButton: {
    width: '100%',
  },
};

const submissionError = () => {
  throw new SubmissionError({
    _error: 'Login Failed!',
  });
};

const submit = (values, dispatch) => {
  const { email } = values;
  const formProperties = new FormData();
  formProperties.append('email', values.email);
  formProperties.append('password', values.password);
  return fetch('/api/v1/authenticate', {
    method: 'POST',
    body: formProperties,
    credentials: 'same-origin',
  }).then((response) => {
    if (response.status !== 201) {
      submissionError();
    }
    return response.json();
  }, () => {
    submissionError();
  }).then((data) => {
    const auth = {
      email,
      token: data.token,
    };
    dispatch(setAuthentication(auth));
  });
};

const LoginForm = (props) => {
  const {
    classes,
    error,
    handleSubmit,
    submitting,
  } = props;
  return (
    <form
      className={classes.container}
      onSubmit={handleSubmit(submit)}
    >
      <Grid
        container
        justify="center"
        alignItems="center"
        alignContent="center"
        className={classes.mainGrid}
      >
        <Grid
          item
          className={classes.headerGrid}
          xs={12}
        >
          <h1>Log In</h1>
        </Grid>
        <Grid item className={classes.bodyGrid} xs={12}>
          <Grid
            container
            direction="column"
            spacing={24}
          >
            <Grid
              item
              hidden={{ xsUp: !error }}
              className={classes.errorLine}
              xs={12}
            >
              <strong>Error: {error}</strong>
            </Grid>
            <Grid item xs={12}>
              <Field
                name="email"
                className={classes.inputField}
                component={TextField}
                error={error != null}
                label="Email"
              />
            </Grid>
            <Grid item xs={12}>
              <Field
                name="password"
                className={classes.inputField}
                component={TextField}
                error={error != null}
                label="Password"
                type="password"
              />
            </Grid>
            <Grid item xs={12}>
              <Button
                type="submit"
                className={classes.submitButton}
                disabled={submitting}
                color="primary"
              >
                Login
              </Button>
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </form>
  );
};

LoginForm.propTypes = {
  ...propTypes,
  email: PropTypes.string.isRequired,
  password: PropTypes.string.isRequired,
  authEmail: PropTypes.string,
};

LoginForm.defaultProps = {
  authEmail: null,
};

const mapStateToProps = (state) => {
  return {
    email: state.form.login.values.email,
    password: state.form.login.values.password,
    authEmail: state.authentication.email,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    setAuthentication: data => dispatch(setAuthentication(data)),
  };
};

const ConnectedLoginForm = connect(mapStateToProps, mapDispatchToProps)(LoginForm);
const ReduxLoginForm = reduxForm(formData)(ConnectedLoginForm);
const StyledLoginForm = withStyles(styles)(ReduxLoginForm);
export default StyledLoginForm;
