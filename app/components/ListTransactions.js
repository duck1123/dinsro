import { withStyles } from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
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

class ListTransactions extends Component {
  static propTypes = {
    classes: PropTypes.instanceOf(Object).isRequired,
    errored: PropTypes.bool.isRequired,
    fetchCollection: PropTypes.func.isRequired,
    loading: PropTypes.bool.isRequired,
    token: PropTypes.string.isRequired,
    transactions: PropTypes.arrayOf(Object).isRequired,
  };

  componentDidMount() {
    this.props.fetchCollection('transactions', this.props.token);
  }

  render() {
    const {
      classes,
      transactions,
      errored,
      loading,
    } = this.props;

    return (
      <div>
        <p>List Transactions {errored} {loading}</p>
        <Paper className={classes.root} >
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

const mapStateToProps = (state) => {
  const model = state.models.transactions || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model;
  return {
    errored,
    loading,
    token: state.authentication.token,
    transactions: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchCollection: (model, token) => dispatch(fetchCollection(model, token)),
  };
};

const LT = connect(mapStateToProps, mapDispatchToProps)(ListTransactions);

export default withStyles(styles)(LT);
