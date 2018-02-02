// Link.react.test.js
import Enzyme, { mount } from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';
import React from 'react';
import renderer from 'react-test-renderer';
import sinon from 'sinon';
import { ListUsers } from './ListUsers';

Enzyme.configure({ adapter: new Adapter() });

function setup() {
  const props = {
    addTodo: jest.fn(),
    classes: {},
    fetchCollection: (model, token) => {
      console.log('fetchCollection', model, token);
    },
    loading: false,
    errored: false,
    users: [],
  };

  const enzymeWrapper = mount(<ListUsers {...props} />);

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

      expect(ListUsers.prototype.componentDidMount.calledOnce)
        .to.equal(true);
    });
  });
});
