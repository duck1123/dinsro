import Enzyme, { mount } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import React from 'react';
import renderer from 'react-test-renderer';
import { MemoryRouter } from 'react-router';
import sinon from 'sinon';
import { ListUserAccountsUnconnected as ListUserAccounts } from './ListUserAccounts';

Enzyme.configure({ adapter: new Adapter() });

const token = 'atoken';

const fetchSubcollection = (model, id, token) => {
  console.log('fetchSubcollection', model, id, token);
};

const items = [
  {
    id: 1,
    name: 'foo',
  },
];

function setup() {
  const props = {
    classes: {},
    errored: false,
    fetchSubcollection,
    id: 1,
    loading: false,
    token,
    items,
  };

  const enzymeWrapper = mount(
    <MemoryRouter>
      <ListUserAccounts {...props} />
    </MemoryRouter>
  );

  return {
    props,
    enzymeWrapper
  };
}

describe('components', () => {

  const componentName = 'ListUserAccounts';

  describe(componentName, () => {
    it('should render self and subcomponents', () => {

      const { prototype } = ListUserAccounts;

      sinon.spy(prototype, 'componentDidMount');

      const { enzymeWrapper } = setup();

      expect(enzymeWrapper.find('h2').text()).toBe('List User Accounts');

      expect(prototype.componentDidMount.calledOnce).toBe(true);

      enzymeWrapper.find('ListItem').forEach((node, index) => {
        expect(node.text()).toBe(items[index].name);
      });
    });
  });
});
