import AppBar from 'material-ui/AppBar';
import React, { Component } from 'react';
import Login from '../components/Login';

class Header extends Component {
  render() {
    return (
      <div>
        <AppBar
          title="Dinsro"
          iconElementRight={<Login />}
        />
      </div>
    );
  }
}

export default Header;
