import { withStyles } from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchSubcollection } from '../actions/model';

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

class ListAccountTransactions extends Component {
  componentDidMount() {
    const { token, id } = this.props;
    this.props.fetchSubcollection('accountTransactions', id, token);
  }

  render() {
    const { classes, transactions } = this.props;
    return (
      <div>
        <h1>List Account Transactions</h1>
        <Paper className={classes.root}>
          <Table className={classes.table}>
            <TableHead>
              <TableRow>
                <TableCell>Value</TableCell>
                <TableCell>Created</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              { transactions.map(transaction => (
                <TableRow key={transaction.id} >
                  <TableCell>
                    <Link
                      to={`/transactions/${transaction.id}`}
                    >
                      {transaction.value}
                    </Link>
                  </TableCell>
                  <TableCell>{transaction.created}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      </div>
    );
  }
}

ListAccountTransactions.propTypes = {
  classes: PropTypes.instanceOf(Object).isRequired,
  fetchSubcollection: PropTypes.func.isRequired,
  token: PropTypes.string.isRequired,
  transactions: PropTypes.arrayOf(Object).isRequired,
  id: PropTypes.number.isRequired,
};

const mapStateToProps = (state, ownProps) => {
  const id = ownProps.id || parseInt(ownProps.match.params.id, 10);
  const model = state.models.accountTransactions || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model[id] || {};
  return {
    errored,
    id,
    loading,
    token: state.authentication.token,
    transactions: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchSubcollection: (model, id, token) =>
      dispatch(fetchSubcollection(model, id, token)),
  };
};

const LAT = connect(mapStateToProps, mapDispatchToProps)(ListAccountTransactions);

export default withStyles(styles)(LAT);
