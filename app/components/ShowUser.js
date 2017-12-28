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
    fetchUser: PropTypes.func.isRequired,
  }

  componentDidMount() {
    console.log('ShowUser component mounted', this.props);
    this.props.fetchUser(this.props.match.params.id);
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
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: id => dispatch(userFetchData(id)),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ShowUser));
