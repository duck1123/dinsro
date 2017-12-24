import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { usersFetchData } from '../actions/users';

class ShowUser extends React.Component {
  static propTypes = {
    isLoading: PropTypes.bool.isRequired,
    fetchUsers: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    users: PropTypes.arrayOf(Object).isRequired,
  }

  componentDidMount() {
    console.log('ListUsers component mounted', this.props);
    this.props.fetchUsers();
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
        <p>List Users</p>
        <p>Loading?: {this.props.isLoading ? 'true' : 'false'}</p>
        <ul>
          {
            this.props.users.map(user => (
              <li key={user.id}>
                <a href={`/users/${user.id}`}>
                  {user.name}
                </a>
              </li>))
          }
        </ul>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    users: state.users,
    hasErrored: state.usersHasErrored,
    isLoading: state.usersIsLoading,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUsers: () => dispatch(usersFetchData()),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ShowUser));
