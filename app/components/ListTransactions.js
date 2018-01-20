import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchCollection } from '../actions/model';

class ListTransactions extends Component {
  static propTypes = {
    transactions: PropTypes.arrayOf(Object).isRequired,
    errored: PropTypes.bool.isRequired,
    loading: PropTypes.bool.isRequired,
    fetchCollection: PropTypes.func.isRequired,
    token: PropTypes.string.isRequired,
  };

  componentDidMount() {
    this.props.fetchCollection('transactions', this.props.token);
  }

  render() {
    const { transactions, errored, loading } = this.props;
    return (
      <div>
        <p>List Transactions {errored} {loading}</p>
        {
          transactions.map(transaction => (
            <p key={transaction.id}>
              Transaction:
              <Link
                to={`/transactions/${transaction.id}`}
              >
                {transaction.value}
              </Link>
              - Id: {transaction.id}
            </p>
          ))
        }
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const model = state.models.transactions || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model;
  return {
    errored,
    loading,
    token: state.authentication.token,
    transactions: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (model, token) => dispatch(fetchCollection(model, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListTransactions);
