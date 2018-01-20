import { withStyles } from 'material-ui/styles';
import List, { ListItem, ListItemText } from 'material-ui/List';
import Paper from 'material-ui/Paper';
import PropTypes from 'prop-types';
import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { usersFetchData } from '../actions/users';

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

class ListUsers extends React.Component {
  static propTypes = {
    classes: PropTypes.instanceOf(Object).isRequired,
    errored: PropTypes.bool.isRequired,
    fetchUsers: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    token: PropTypes.string,
    users: PropTypes.arrayOf(Object).isRequired,
  }

  static defaultProps = {
    token: null,
  }

  componentDidMount() {
    this.props.fetchUsers(this.props.token);
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
  return {
    errored: state.users.errored,
    loading: state.users.loading,
    token: state.authentication.token,
    users: state.users.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchUsers: token => dispatch(usersFetchData(token)),
  };
};

const LU = connect(mapStateToProps, mapDispatchToProps)(ListUsers);

export default withStyles(styles)(LU);
