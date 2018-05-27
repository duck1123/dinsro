import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import { usersFetchData } from '../actions/users';

class ListUsers extends React.Component {
  static propTypes = {
    fetchUsers: PropTypes.func.isRequired,
    hasErrored: PropTypes.bool.isRequired,
    isLoading: PropTypes.bool.isRequired,
    token: PropTypes.string,
    users: PropTypes.arrayOf(Object).isRequired,
  }

  static defaultProps = {
    token: null,
  }

  componentDidMount() {
    this.props.fetchUsers(this.props.token);
  }

  render() {
    return (
      <div>
        <h1>List Users</h1>
        { this.props.hasErrored ? <p>Sorry! There was an error loading the items</p> : null }
        { this.props.isLoading ? <p>Loading…</p> : (
          <ul>
            { this.props.users.map(user => (
              <li key={user.id}>
                <Link to={`/users/${user.id}`}>
                  {user.name}
                </Link>
              </li>))}
          </ul>
        )}
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    hasErrored: state.users.errored,
    isLoading: state.users.loading,
    token: state.authentication.token,
    users: state.users.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUsers: token => dispatch(usersFetchData(token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUsers);
