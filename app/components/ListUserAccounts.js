import List, { ListItem, ListItemText } from 'material-ui/List';
import PropTypes from 'prop-types';
import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { fetchSubcollection } from '../actions/model';

const modelKey = 'userAccounts';

class ListUserAccounts extends Component {
  componentDidMount() {
    const { id, token } = this.props;
    this.props.fetchSubcollection(modelKey, id, token);
  }

  render() {
    const {
      errored,
      loading,
      items,
    } = this.props;

    if (errored) {
      return <p>Sorry! There was an error loading the items</p>;
    }

    if (loading) {
      return <p>Loading…</p>;
    }

    return (
      <div>
        <h2>List User Accounts</h2>
        <List>
          { items.map(account => (
            <ListItem
              button
              component={Link}
              to={`/accounts/${account.id}`}
              key={account.id}
            >
              <ListItemText primary={account.name} />
            </ListItem>
          ))}
        </List>
      </div>
    );
  }
}

ListUserAccounts.propTypes = {
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
    loading,
    token: state.authentication.token,
    items: data,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    fetchSubcollection: (model, id, token) =>
      dispatch(fetchSubcollection(model, id, token)),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUserAccounts);
