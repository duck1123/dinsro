import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchUserTransactions } from '../actions/usertransactions';

class ListUserTransactions extends Component {
  componentDidMount() {
    const { id, token } = this.props;
    this.props.fetchCollection(id, token);
  }

  render() {
    const { items } = this.props;
    return (
      <div>
        <h1>List User Transactions</h1>
        <ul>
          { items.map(item => (
            <li key={item.id} >
              <p>Id: {item.id}</p>
              <p>Value: {item.value}</p>
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListUserTransactions.propTypes = {
  fetchCollection: PropTypes.func.isRequired,
  id: PropTypes.number.isRequired,
  items: PropTypes.arrayOf(Object).isRequired,
  token: PropTypes.string,
};

const mapStateToProps = (state) => {
  return {
    items: state.items.data,
    token: state.authentication.token,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (id, token) =>
      dispatch(fetchUserTransactions(id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions);
