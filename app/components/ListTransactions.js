import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';

class ListTransactions extends Component {
  static propTypes = {
    transactions: PropTypes.arrayOf(Object).isRequired,
  }

  render() {
    const { transactions } = this.props;
    return (
      <div>
        <h1>List Transactions</h1>
        <ul>
          { transactions.map(transaction => (
            <li key={transaction.id} >
              {transaction.id}
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListTransactions.propTypes = {
  transactions: PropTypes.arrayOf(Object).isRequired,
};

const mapStateToProps = (state) => {
  return {
    transactions: state.transactions.data,
  };
};

const mapDispatchToProps = () => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListTransactions);
