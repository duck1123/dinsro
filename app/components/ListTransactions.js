import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';

class ListTransactions extends Component {
  static propTypes = {
    token: PropTypes.string,
  }

  render() {
    return (
      <div>
        <h1>List Transactions</h1>
        <p>{this.props.token}</p>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
  };
};

const mapDispatchToProps = () => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListTransactions);
