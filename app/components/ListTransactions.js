import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';

class ListTransactions extends Component {
  render() {
    return (
      <div>
        <h1>List Transactions</h1>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
  };
};

const mapDispatchToProps = () => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListTransactions);
