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
    this.props.fetchTransactions(this.props.userId, this.props.token);
  }

  render() {
    const { classes, transactions } = this.props;
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
              { transactions.map(transaction => (
                <TableRow key={transaction.id} >
                  <TableCell>
                    {transaction.id}
                  </TableCell>
                  <TableCell>
                    <Link
                      to={`/transactions/${transaction.id}`}
                    >
                      {transaction.value}
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
  fetchTransactions: PropTypes.func.isRequired,
  token: PropTypes.string.isRequired,
  transactions: PropTypes.arrayOf(Object).isRequired,
  userId: PropTypes.number.isRequired,
};

const mapStateToProps = (state) => {
  return {
    token: state.authentication.token,
    transactions: state.transactions.data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchTransactions: (userId, token) =>
      dispatch(fetchUserTransactions(userId, token)),
  };
};

const LUT1 = connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions)

export default withStyles(styles)(LUT1);
