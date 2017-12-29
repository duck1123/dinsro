import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { userFetchData } from '../actions/users';
import ListTransactions from './ListTransactions';

class ShowUser extends React.Component {
  static propTypes = {
    isLoading: PropTypes.bool.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    users: PropTypes.arrayOf(Object).isRequired,
    match: PropTypes.instanceOf(Object).isRequired,
    user: PropTypes.instanceOf(Object).isRequired,
    transactions: PropTypes.instanceOf(Object).isRequired,
    fetchUser: PropTypes.func.isRequired,
    token: PropTypes.string.isRequired,
  }

  componentDidMount() {
    console.log('ShowUser component mounted', this.props);
    const { id } = this.props.match.params;
    const { token } = this.props;
    this.props.fetchUser(id, token);
  }

  render() {
    if (this.props.hasErrored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (this.props.isLoading) {
      return <p>Loading…</p>;
    }

    return (
      <div>
        <h1>Show User {this.props.user.id}</h1>
        <p>Name: {this.props.user.name}</p>
        <p>Loading?: {this.props.isLoading ? 'true' : 'false'}</p>
        <ListTransactions />
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state.user.data,
    hasErrored: state.user.errored,
    isLoading: state.user.loading,
    token: state.authentication.token,
    transactions: state.transactions,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: (userId, token) => dispatch(userFetchData(userId, token)),
    loadTransactions: (userId, token) => dispatch(fetchUserTransactions(userId, token)),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ShowUser));
