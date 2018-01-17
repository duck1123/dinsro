import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchAccountTransactions } from '../actions/accounttransactions';

class ListAccountTransactions extends Component {
  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchAccountTransactions(id, token);
  }

  render() {
    const { transactions } = this.props;
    return (
      <div>
        <h1>List Account Transactions</h1>
        <ul>
          { transactions.map(transaction => (
            <li key={transaction.id} >
              <p>
                Name:
                <Link
                  to={`/transactions/${transaction.id}`}
                >
                  {transaction.name}
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

ListAccountTransactions.propTypes = {
  fetchAccountTransactions: PropTypes.func.isRequired,
  token: PropTypes.string.isRequired,
  transactions: PropTypes.arrayOf(Object).isRequired,
  id: PropTypes.number.isRequired,
};

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
    transactions: state.accountTransactions.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchAccountTransactions: (id, token) =>
      dispatch(fetchAccountTransactions(id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListAccountTransactions);
