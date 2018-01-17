import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchUserAccounts } from '../actions/useraccounts';

class ListUserAccounts extends Component {
  componentDidMount() {
    this.props.fetchUserAccounts(this.props.userId, this.props.token);
  }

  render() {
    const { accounts } = this.props;
    return (
      <div>
        <h1>List User Accounts</h1>
        <ul>
          { accounts.map(account => (
            <li key={account.id} >
              <p>
                Name:
                <Link
                  to={`/accounts/${account.id}`}
                >
                  {account.name}
                </Link>
                - Id: {account.id}
              </p>
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListUserAccounts.propTypes = {
  fetchUserAccounts: PropTypes.func.isRequired,
  token: PropTypes.string.isRequired,
  accounts: PropTypes.arrayOf(Object).isRequired,
  userId: PropTypes.number.isRequired,
};

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
    accounts: state.useraccounts.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUserAccounts: (userId, token) =>
      dispatch(fetchUserAccounts(userId, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUserAccounts);
