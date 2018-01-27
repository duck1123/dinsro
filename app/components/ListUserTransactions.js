import { withStyles } from 'material-ui/styles';
import Paper from 'material-ui/Paper';
import Table, { TableBody, TableCell, TableHead, TableRow } from 'material-ui/Table';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchSubcollection } from '../actions/model';

const modelKey = 'userTransactions';

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
    this.props.fetchSubcollection(modelKey, id, token);
  }

  render() {
    const {
      classes,
      errored,
      items,
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
  errored: PropTypes.bool.isRequired,
  fetchSubcollection: PropTypes.func.isRequired,
  id: PropTypes.number.isRequired,
  items: PropTypes.arrayOf(Object).isRequired,
  loading: PropTypes.bool.isRequired,
  token: PropTypes.string.isRequired,
};

const mapStateToProps = (state, ownProps) => {
  const id = ownProps.id || parseInt(ownProps.match.params.id, 10);
  const model = state.models[modelKey] || {};
  const {
    data = [],
    errored = false,
    loading = false,
  } = model[id] || {};
  return {
    errored,
    id,
    items: data,
    loading,
    token: state.authentication.token,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchSubcollection: (model, id, token) =>
      dispatch(fetchSubcollection(model, id, token)),
  };
};

const LUT1 = connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions);

export default withStyles(styles)(LUT1);
