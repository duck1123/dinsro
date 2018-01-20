import List, { ListItem, ListItemText } from 'material-ui/List';
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
        <h2>List User Accounts</h2>
        <List>
          { accounts.map(account => (
            <ListItem
              button
              component={Link}
              to={`/accounts/${account.id}`}
              key={account.id}
            >
              <ListItemText primary={account.name} />
            </ListItem>
          ))}
        </List>
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
