import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { fetchModel } from '../actions/model';
import ListAccountTransactions from './ListAccountTransactions';

class ShowAccount extends React.Component {
  static propTypes = {
    id: PropTypes.number.isRequired,
    fetchModel: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    isLoading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    account: PropTypes.instanceOf(Object).isRequired,
  }

  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchModel('account', id, token);
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
  const model = state.models.account || {};
  const { data, errored, loading } = model[id] || {};
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
    fetchModel: (model, id, token) => dispatch(fetchModel(model, id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowAccount);