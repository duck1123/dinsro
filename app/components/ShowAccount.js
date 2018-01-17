import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { fetchAccount } from '../actions/account';

class ShowAccount extends React.Component {
  static propTypes = {
    accountId: PropTypes.number.isRequired,
    fetchAccount: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    isLoading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    account: PropTypes.instanceOf(Object).isRequired,
  }

  componentDidMount() {
    const { token, accountId } = this.props;
    this.props.fetchAccount(accountId, token);
  }

  render() {
    const { isLoading, hasErrored } = this.props;
    if (hasErrored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (isLoading) {
      return <p>Loading…</p>;
    }

    const { account } = this.props;

    return (
      <div>
        <h1>Account: {account.name}</h1>
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const accountId = parseInt(ownProps.match.params.id, 10);
  const { data, errored, loading } = state.account[accountId] || {};
  return {
    hasErrored: typeof errored === 'undefined' ? false : errored,
    isLoading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    account: typeof data === 'undefined' ? {} : data,
    accountId,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchAccount: (userId, token) => dispatch(fetchAccount(userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowAccount);
