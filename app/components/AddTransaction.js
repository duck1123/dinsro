import { withStyles } from 'material-ui/styles';
import Button from 'material-ui/Button';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import {
  Field,
  reduxForm,
  propTypes,
  // SubmissionError,
} from 'redux-form';
import { TextField } from 'redux-form-material-ui';

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
    value: 0,
  },
};

class AddTransaction extends Component {
  static propTypes = {
    ...propTypes,
    userId: PropTypes.number.isRequired,
  }

  render() {
    const {
      classes,
      error,
      submitting,
    } = this.props;
    return (
      <form>
        <Field
          name="value"
          className={classes.inputField}
          component={TextField}
          error={error}
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

export default reduxForm(formData)(withStyles(styles)(AddTransaction));
