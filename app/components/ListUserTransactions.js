import React, { Component } from 'react';
import { connect } from 'react-redux';

class ListUserTransactions extends Component {
  static propTypes = {
    items: PropTypes.arrayOf(Object).isRequired,
  }

  render() {
    const { items } = this.props;
    return (
      <div>
        <h1>List User Transactions</h1>
        <ul>
          { items.map(item => (
            <li key={item.id} >
              {item.id}
            </li>
          ))}
        </ul>
      </div>
    );
  }
}

ListUserTransactions.propTypes = {
  items: PropTypes.arrayOf(Object).isRequired,
};

const mapStateToProps = (state) => {
  return {
    items: state.items.data,
  };
};

const mapDispatchToProps = () => {
  return {
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ListUserTransactions);
