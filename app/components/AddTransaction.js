import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
  Field,
  reduxForm,
  propTypes,
  SubmissionError,
} from 'redux-form';
import { connect } from 'react-redux';
import { TextField } from 'redux-form-material-ui';
import { fetchUserTransactions } from '../actions/usertransactions';

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
  },
  submitButton: {
  },
};

const formData = {
  form: 'addTransaction',
  initialValues: {
    value: 43,
  },
};

const submissionError = () => {
  throw new SubmissionError({
    _error: 'Login Failed!',
  });
};

const submit = (userId, token) => {
  return (values, dispatch) => {
    return fetch('/api/v1/transactions', {
      method: 'POST',
      body: JSON.stringify(values),
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    }).then((response) => {
      if (response.status !== 201) {
        submissionError();
      }
      return response.json();
    }, () => {
      submissionError();
    }).then(() => {
      dispatch(fetchUserTransactions(userId, token));
    });
  };
};

class AddTransaction extends Component {
  static propTypes = {
    ...propTypes,
    userId: PropTypes.number.isRequired,
  };

  render() {
    const {
      classes,
      error,
      handleSubmit,
      submitting,
      token,
      userId,
    } = this.props;
    return (
      <form
        className={classes.container}
        onSubmit={handleSubmit(submit(userId, token))}
      >
        <Field
          name="value"
          className={classes.inputField}
          component={TextField}
          error={error}
          parse={parseInt}
          label="Value"
        />
        <Button
          type="submit"
          className={classes.submitButton}
          disabled={submitting}
          color="primary"
        >
          Add
        </Button>

      </form>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
  };
};

const mapDispatchToProps = () => {
  return {
  };
};


export default connect(
  mapStateToProps,
  mapDispatchToProps,
)(reduxForm(formData)(withStyles(styles)(AddTransaction)));
