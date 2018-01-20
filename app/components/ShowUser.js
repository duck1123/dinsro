import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { fetchModel } from '../actions/model';
import AddTransaction from './AddTransaction';
import ListUserAccounts from './ListUserAccounts';
import ListUserTransactions from './ListUserTransactions';

class ShowUser extends React.Component {
  static propTypes = {
    authEmail: PropTypes.string.isRequired,
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
    const {
      authEmail,
      loading,
      errored,
      id,
      user,
    } = this.props;

    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    return (
      <div>
        <h1>User: {user.name} ({id})</h1>
        <ListUserAccounts userId={id} />
        {
          (authEmail === user.email) ? (
            <AddTransaction userId={id} />
          ) : null
        }
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
    authEmail: state.authentication.email,
    errored: typeof errored === 'undefined' ? false : errored,
    id,
    loading: typeof loading === 'undefined' ? false : loading,
    token: state.authentication.token,
    user: typeof data === 'undefined' ? {} : data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: (userId, token) => dispatch(fetchModel('user', userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShowUser);
