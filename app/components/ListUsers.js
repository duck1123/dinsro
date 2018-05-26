import { withStyles } from 'material-ui/styles';
import List, { ListItem, ListItemText } from 'material-ui/List';
import Paper from 'material-ui/Paper';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchCollection } from '../actions/model';

const styles = theme => ({
  root: {
    width: '100%',
    marginTop: theme.spacing.unit * 3,
    overflowX: 'auto',
  },
  table: {
    minWidth: 700,
  },
});

export class ListUsersUnconnected extends Component {
  static propTypes = {
    classes: PropTypes.instanceOf(Object).isRequired,
    errored: PropTypes.bool.isRequired,
    fetchCollection: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    token: PropTypes.string,
    users: PropTypes.arrayOf(Object).isRequired,
  };

  static defaultProps = {
    token: null,
  };

  componentDidMount() {
    this.props.fetchCollection('users', this.props.token);
  }

  render() {
    const {
      classes,
      errored,
      loading,
    } = this.props;

    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    return (
      <div>
        <h1>List Users</h1>
        <Paper className={classes.root}>
          <List>
            { this.props.users.map(user => (
              <ListItem
                key={user.id}
                component={Link}
                to={`/users/${user.id}`}
              >
                <ListItemText primary={user.name} />
              </ListItem>
            ))}
          </List>
        </Paper>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  const model = state.models.users || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model;
  return {
    errored,
    loading,
    token: state.authentication.token,
    users: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (model, token) => dispatch(fetchCollection(model, token)),
  };
};

const LU = connect(mapStateToProps, mapDispatchToProps)(ListUsersUnconnected);

export default withStyles(styles)(LU);
