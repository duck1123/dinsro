import AppBar from 'material-ui/AppBar';
import React from 'react';
import Login from '../components/Login';

const Header = () => {
  return (
    <div>
      <AppBar
        title="Dinsro"
        iconElementRight={<Login />}
      />
    </div>
  );
};

export default Header;
