import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { fetchAccounts } from '../actions/accounts';

class ListAccounts extends Component {
  static propTypes = {
    accounts: PropTypes.arrayOf(Object).isRequired,
    fetchAccounts: PropTypes.func.isRequired,
    token: PropTypes.string.isRequired,
  };


  componentDidMount() {
    this.props.fetchAccounts(this.props.token);
  }

  render() {
    return (
      <div>
        <p>ListAccounts</p>
        {
          this.props.accounts.map(a => (
            <p key={a.id}>{a.name} - {a.id}</p>
          ))
        }
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
    accounts: state.accounts.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchAccounts: token => dispatch(fetchAccounts(token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListAccounts);
