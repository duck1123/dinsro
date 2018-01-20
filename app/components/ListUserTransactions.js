import { withStyles } from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchUserTransactions } from '../actions/usertransactions';

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

class ListUserTransactions extends Component {
  componentDidMount() {
    const { id, token } = this.props;
    this.props.fetchCollection(id, token);
  }

  render() {
    const { classes, items } = this.props;
    return (
      <div>
        <h2>List User Transactions</h2>
        <Paper className={classes.root}>
          <Table className={classes.table}>
            <TableHead >
              <TableRow >
                <TableCell>Id</TableCell>
                <TableCell>Value</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              { items.map(item => (
                <TableRow key={item.id} >
                  <TableCell>
                    {item.id}
                  </TableCell>
                  <TableCell>
                    <Link
                      to={`/transactions/${item.id}`}
                    >
                      {item.value}
                    </Link>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </div>
    );
  }
}

ListUserTransactions.propTypes = {
  classes: PropTypes.instanceOf(Object).isRequired,
  fetchCollection: PropTypes.func.isRequired,
  id: PropTypes.number.isRequired,
  items: PropTypes.arrayOf(Object).isRequired,
  token: PropTypes.string.isRequired,
};

const mapStateToProps = (state) => {
  return {
    items: state.items.data,
    token: state.authentication.token,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (id, token) =>
      dispatch(fetchUserTransactions(id, token)),
  };
};

const LUT1 = connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions)

export default withStyles(styles)(LUT1);
