import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom';
import { userFetchData } from '../actions/users';

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
    console.log('ListUsers component mounted', this.props);
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
        <p>Show User {this.props.user.id}</p>
        <p>Name: {this.props.user.name}</p>
        <p>Loading?: {this.props.isLoading ? 'true' : 'false'}</p>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state.user,
    hasErrored: state.userHasErrored,
    isLoading: state.userIsLoading,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUser: id => dispatch(userFetchData(id)),
  };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(ShowUser));
