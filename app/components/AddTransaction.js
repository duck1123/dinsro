import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import MenuItem from 'material-ui/Menu/MenuItem';
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
};

const formData = {
  form: 'addTransaction',
  initialValues: {
    value: 43,
  },
};

const submissionError = () => {
  throw new SubmissionError({
    _error: 'Submission Failed!',
  });
};

const currencies = [
  {
    value: 'USD',
    label: '$',
  },
  {
    value: 'EUR',
    label: '€',
  },
  {
    value: 'BTC',
    label: '฿',
  },
  {
    value: 'JPY',
    label: '¥',
  },
];

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
      if (response.status !== 200) {
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
    id: PropTypes.number.isRequired,
  };

  handleChange = name => (event) => {
    this.setState({
      [name]: event.target.value,
    });
  };


  render() {
    const {
      classes,
      error,
      errored,
      handleSubmit,
      loading,
      submitting,
      token,
      id,
    } = this.props;

    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    return (
      <form
        className={classes.container}
        onSubmit={handleSubmit(submit(id, token))}
      >
        <Field
          name="value"
          className={classes.inputField}
          component={TextField}
          error={error != null}
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
        <Field
          id="select-currency"
          select
          component={TextField}
          label="Select"
          className={classes.textField}
          value={this.props.currency}
          onChange={this.handleChange('currency')}
          SelectProps={{
            MenuProps: {
              className: classes.menu,
            },
          }}
          helperText="Please select your currency"
          margin="normal"
        >
          {currencies.map(option => (
            <MenuItem key={option.value} value={option.value}>
              {option.label}
            </MenuItem>
          ))}
        </Field>
        { this.props.userAccounts.map(account => (
          <p>{account.name}</p>
        ))}
      </form>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const modelKey = 'userAccounts';
  const id = ownProps.id || parseInt(ownProps.match.params.id, 10);
  const model = state.models[modelKey] || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model[id] || {};
  return {
    errored,
    loading,
    token: state.authentication.token,
    userAccounts: data,
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
