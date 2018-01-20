import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchUserTransactions } from '../actions/usertransactions';

class ListUserTransactions extends Component {
  componentDidMount() {
    this.props.fetchTransactions(this.props.userId, this.props.token);
  }

  render() {
    const { transactions } = this.props;
    return (
      <div>
        <h1>List User Transactions</h1>
        <ul>
          { transactions.map(transaction => (
            <li key={transaction.id} >
              <p>
                Value:
                <Link
                  to={`/transactions/${transaction.id}`}
                >
                  {transaction.value}
                </Link>
                - Id: {transaction.id}
              </p>
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListUserTransactions.propTypes = {
  fetchTransactions: PropTypes.func.isRequired,
  token: PropTypes.string.isRequired,
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

export default connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions);
