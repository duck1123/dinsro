import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { fetchModel } from '../actions/model';
import AddTransaction from './AddTransaction';
import ListUserAccounts from './ListUserAccounts';
import ListUserTransactions from './ListUserTransactions';

class ShowUser extends React.Component {
  static propTypes = {
    fetchUser: PropTypes.func.isRequired,
    errored: PropTypes.bool.isRequired,
    loading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    user: PropTypes.instanceOf(Object).isRequired,
    id: PropTypes.number.isRequired,
  }

  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchUser(id, token);
  }

  render() {
    const { loading, errored, id } = this.props;
    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    const { user } = this.props;

    return (
      <div>
        <h1>User: {user.name} ({id})</h1>
        <ListUserAccounts userId={id} />
        <AddTransaction userId={id} />
        <ListUserTransactions userId={id} />
      </div>
    );
  }
}

const mapStateToProps = (state, ownProps) => {
  const id = parseInt(ownProps.match.params.id, 10);
  const model = state.models.user || {};
  const { data, errored, loading } = model[id] || {};
  return {
    hasErrored: typeof errored === 'undefined' ? false : errored,
    isLoading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    user: typeof data === 'undefined' ? {} : data,
    id,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: (userId, token) => dispatch(fetchModel('user', userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowUser);
