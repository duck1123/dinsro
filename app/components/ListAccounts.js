import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchCollection } from '../actions/model';

class ListAccounts extends Component {
  static propTypes = {
    accounts: PropTypes.arrayOf(Object).isRequired,
    errored: PropTypes.bool.isRequired,
    loading: PropTypes.bool.isRequired,
    fetchCollection: PropTypes.func.isRequired,
    token: PropTypes.string.isRequired,
  };


  componentDidMount() {
    this.props.fetchCollection('accounts', this.props.token);
  }

  render() {
    const { accounts, errored, loading } = this.props;
    return (
      <div>
        <p>List Accounts {errored} {loading}</p>
        {
          accounts.map(account => (
            <p key={account.id}>
              Name:
              <Link
                to={`/accounts/${account.id}`}
              >
                {account.name}
              </Link>
              - Id: {account.id}
            </p>
          ))
        }
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const model = state.models.accounts || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model;
  return {
    errored,
    loading,
    token: state.authentication.token,
    accounts: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (model, token) => dispatch(fetchCollection(model, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListAccounts);
