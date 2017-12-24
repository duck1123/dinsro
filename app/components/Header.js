import { withStyles } from 'material-ui/styles';
import AppBar from 'material-ui/AppBar';
import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import Login from '../components/Login';

class Header extends Component {
  render() {
    return (
      <div>
        <AppBar
          title="Dinsro"
          iconElementRight={<Login />}
        />
        <ul>
          <li>
            <Link to="/" href="/">Home</Link>
          </li>
          <li>
            <Link to="/users" href="/users">Users</Link>
          </li>
        </ul>
      </div>
    );
  }
}

Header.propTypes = {
  classes: PropTypes.instanceOf(Object).isRequired,
};

export default withStyles(styles)(Header);
