import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchUserTransactions } from '../actions/usertransactions';

class ListTransactions extends Component {
  componentDidMount() {
    this.props.fetchTransactions(this.props.userId, this.props.token);
  }

  render() {
    const { transactions } = this.props;
    return (
      <div>
        <h1>List Transactions</h1>
        <ul>
          { transactions.map(transaction => (
            <li key={transaction.id} >
              <p>Id: {transaction.id}</p>
              <p>Value: {transaction.value}</p>
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListTransactions.propTypes = {
  fetchTransactions: PropTypes.func.isRequired,
  token: PropTypes.string,
  transactions: PropTypes.arrayOf(Object).isRequired,
  userId: PropTypes.number.isRequired,
};

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
    transactions: state.transactions.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchTransactions: (userId, token) =>
      dispatch(fetchUserTransactions(userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListTransactions);
