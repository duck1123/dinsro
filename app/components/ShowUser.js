import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { userFetchData } from '../actions/user';
import AddTransaction from './AddTransaction';
import ListUserTransactions from './ListUserTransactions';

class ShowUser extends React.Component {
  static propTypes = {
    fetchUser: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    isLoading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    user: PropTypes.instanceOf(Object).isRequired,
    userId: PropTypes.number.isRequired,
  }

  componentDidMount() {
    const { token, userId } = this.props;
    this.props.fetchUser(userId, token);
  }

  render() {
    const { isLoading, hasErrored, userId } = this.props;
    if (hasErrored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (isLoading) {
      return <p>Loading…</p>;
    }

    const { user } = this.props;

    return (
      <div>
        <h1>User: {user.name} ({userId})</h1>
        <AddTransaction userId={userId} />
        <ListUserTransactions userId={userId} />
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const userId = parseInt(ownProps.match.params.id, 10);
  const { data, errored, loading } = state.user[userId] || {};
  return {
    hasErrored: typeof errored === 'undefined' ? false : errored,
    isLoading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    user: typeof data === 'undefined' ? {} : data,
    userId,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: (userId, token) => dispatch(userFetchData(userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowUser);
