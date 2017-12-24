import IconButton from 'material-ui/IconButton';
import NavigationClose from 'material-ui/svg-icons/navigation/close';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { toggleDrawerAction } from '../actions/drawer';

class AppBarMenuButton extends Component {
  static propTypes = {
    showDrawer: PropTypes.func.isRequired,
  }

  render() {
    return (
      <IconButton onClick={this.props.showDrawer}>
        <NavigationClose />
      </IconButton>
    );
  }
}

const mapStateToProps = (state) => {
  return {};
};

const mapDispatchToProps = (dispatch) => {
  return {
    showDrawer: () => dispatch(toggleDrawerAction(true)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(AppBarMenuButton);
