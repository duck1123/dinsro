import { withStyles } from 'material-ui/styles';
import AppBar from 'material-ui/AppBar';
import Drawer from 'material-ui/Drawer';
import MenuItem from 'material-ui/MenuItem';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import Login from '../components/Login';
import { closeDrawerAction, openDrawerAction } from '../actions/drawer';

class Header extends Component {
  static propTypes = {
    open: PropTypes.bool.isRequired,
    closeDrawer: PropTypes.func.isRequired,
    openDrawer: PropTypes.func.isRequired,
  }

  componentDidMount() {
    this.props.openDrawer();
  }

  render() {
    return (
      <div>
        <AppBar
          title="Dinsro"
          iconElementRight={<Login />}
        />
        <Drawer
          docked={false}
          width={200}
          open={this.props.open}
        >
          <MenuItem onClick={this.props.closeDrawer}>Menu Item</MenuItem>
          <MenuItem onClick={this.props.closeDrawer}>Menu Item 2</MenuItem>
        </Drawer>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    open: state.open,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    closeDrawer: () => dispatch(closeDrawerAction()),
    openDrawer: () => dispatch(openDrawerAction()),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Header);
