import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { fetchAccount } from '../actions/account';
import ListAccountTransactions from './ListAccountTransactions';

class ShowAccount extends React.Component {
  static propTypes = {
    id: PropTypes.number.isRequired,
    fetchAccount: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    isLoading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    account: PropTypes.instanceOf(Object).isRequired,
  }

  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchAccount(id, token);
  }

  render() {
    const { isLoading, hasErrored } = this.props;
    if (hasErrored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (isLoading) {
      return <p>Loading…</p>;
    }

    const { account, id } = this.props;

    return (
      <div>
        <h1>Account: {account.name} ({id})</h1>
        <ListAccountTransactions id={id} />
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const id = parseInt(ownProps.match.params.id, 10);
  const { data, errored, loading } = state.account[id] || {};
  return {
    hasErrored: typeof errored === 'undefined' ? false : errored,
    isLoading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    account: typeof data === 'undefined' ? {} : data,
    id,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchAccount: (id, token) => dispatch(fetchAccount(id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowAccount);
