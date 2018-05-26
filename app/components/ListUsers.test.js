// Link.react.test.js
import Enzyme, { mount } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import React from 'react';
import renderer from 'react-test-renderer';
import { MemoryRouter } from 'react-router';
import sinon from 'sinon';
import { ListUsersUnconnected as ListUsers } from './ListUsers';

Enzyme.configure({ adapter: new Adapter() });

const token = 'atoken';

const users = [
  {
    id: 1,
    name: 'foo',
  },
  {
    id: 2,
    name: 'bar',
  },
];

const fetchCollection = (model, token) => {
  console.log('fetchCollection', model, token);
};

function setup() {
  const props = {
    classes: {},
    errored: false,
    fetchCollection,
    loading: false,
    token,
    users,
  };

  const enzymeWrapper = mount(
    <MemoryRouter>
      <ListUsers {...props} />
    </MemoryRouter>
  );

  return {
    props,
    enzymeWrapper
  };
}

describe('components', () => {

  const componentName = 'ListUsers';

  describe(componentName, () => {
    it('should render self and subcomponents', () => {

      sinon.spy(ListUsers.prototype, 'componentDidMount');

      const { enzymeWrapper } = setup();

      expect(enzymeWrapper.find('h1').text()).toBe('List Users');

      expect(ListUsers.prototype.componentDidMount.calledOnce).toBe(true);

      enzymeWrapper.find('ListItem').forEach((node, index) => {
        expect(node.text()).toBe(users[index].name);
      });
    });
  });
});
