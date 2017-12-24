import { withStyles } from 'material-ui/styles';
import AppBar from 'material-ui/AppBar';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import AppBarMenuButton from '../components/AppBarMenuButton';
import Login from '../components/Login';
import { toggleDrawerAction } from '../actions/drawer';

class Header extends Component {
  static propTypes = {
    open: PropTypes.bool.isRequired,
    toggleDrawer: PropTypes.func.isRequired,
    dismiss: PropTypes.func.isRequired,
  }

  componentDidMount() {
    this.props.toggleDrawer(false);
  }

  render() {
    return (
      <div>
        <AppBar
          title="Dinsro"
          iconElementLeft={<AppBarMenuButton />}
          iconElementRight={<Login />}
        />
        <Drawer
          docked={false}
          width={200}
          open={this.props.open}
        >
          <MenuItem onClick={this.props.dismiss}>Menu Item</MenuItem>
          <MenuItem onClick={this.props.dismiss}>Menu Item 2</MenuItem>
        </Drawer>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    open: state.drawerToggled,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleDrawer: opened => dispatch(toggleDrawerAction(opened)),
    dismiss: () => dispatch(toggleDrawerAction(false)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Header);
