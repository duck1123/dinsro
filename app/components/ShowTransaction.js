import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchModel } from '../actions/model';

class ShowTransaction extends Component {
  static propTypes = {
    fetchTransaction: PropTypes.func.isRequired,
    errored: PropTypes.bool.isRequired,
    loading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    transaction: PropTypes.instanceOf(Object).isRequired,
    id: PropTypes.number.isRequired,
  }

  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchTransaction(id, token);
  }

  render() {
    const { loading, errored, id } = this.props;
    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    const { transaction } = this.props;

    return (
      <div>
        <h1>Transaction: {transaction.value} ({id})</h1>
        <p>
          Account:
          <Link
            to={`/accounts/${transaction.accountId}`}
          >
            {transaction.accountId}
          </Link>
        </p>
        <p>
          User:
          <Link
            to={`/users/${transaction.userId}`}
          >
            {transaction.userId}
          </Link>
        </p>
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const id = parseInt(ownProps.match.params.id, 10);
  const model = state.models.transaction || {};
  const { data, errored, loading } = model[id] || {};
  return {
    errored: typeof errored === 'undefined' ? false : errored,
    id,
    loading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    transaction: typeof data === 'undefined' ? {} : data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchTransaction: (id, token) => dispatch(fetchModel('transaction', id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowTransaction);
