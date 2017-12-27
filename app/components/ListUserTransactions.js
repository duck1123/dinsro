import React, { Component } from 'react';
import { connect } from 'react-redux';

class ListUserTransactions extends Component {
  render() {
    return (
      <div>
        <h1>List User Transactions</h1>
      </div>
    );
  }
}

const mapStateToProps = () => {
  return {
  };
};

const mapDispatchToProps = () => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions);
