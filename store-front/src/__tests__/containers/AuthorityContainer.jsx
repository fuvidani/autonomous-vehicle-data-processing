import React from 'react'
import {mount} from 'enzyme'
import configureStore from 'redux-mock-store'
import {Provider} from 'react-redux'
import AuthorityContainer from "../../containers/AuthorityContainer";
import * as Enzyme from 'enzyme';
import ReactSixteenAdapter from 'enzyme-adapter-react-16';
import PropTypes from "prop-types";
import getMuiTheme from 'material-ui/styles/getMuiTheme';
import MockRouter from 'react-mock-router';

Enzyme.configure({adapter: new ReactSixteenAdapter()});

const reportObject = {
    id: "tDbdvAqCxpCQqqYXaRTC76Bm",
    accidentId: "QgYZY8ntPurzGDhxxAcVYbYb",
    vehicleMetaData: {
        identificationNumber: "9KXfzswrhxzKEuX9uiAWcsaw",
        model: "1995 Acura Integra"
    },
    location: {
        lat: 48.172450,
        lon: 16.376432
    },
    passengers: 4,
    emergencyResponseInMillis: 123456,
    durationOfSiteClearingInMillis: 654321
};

const initialState = {
    AuthorityReducer: {
        accidentReports: [reportObject]
    }
};

const mockStore = configureStore();
let wrapper = null;
let store = null;

beforeEach(() => {
    const muiTheme = getMuiTheme();

    store = mockStore(initialState);
    wrapper = mount(<MockRouter><Provider store={store}
                                          accidentReports={[reportObject]}><AuthorityContainer/></Provider></MockRouter>, {
        context: {muiTheme},
        childContextTypes: {muiTheme: PropTypes.object}
    });
});

describe('<AuthorityContainer />', () => {
    it('Basic render with an accident report', () => {
        expect(wrapper.find('.authorityContainer').length).toEqual(1);
        // expect(wrapper.instance().props.accidentReports).toEqual([reportObject]);
    });

    it('Basic render without an accident report', () => {
        const muiTheme = getMuiTheme();
        const emptyWrapper = mount(<MockRouter><Provider store={store}
                                                         accidentReports={[]}><AuthorityContainer/></Provider></MockRouter>, {
            context: {muiTheme},
            childContextTypes: {muiTheme: PropTypes.object}
        });

        expect(emptyWrapper.find('.authorityContainer').length).toEqual(1);
        // expect(emptyWrapper.instance().props.accidentReports).toEqual([]);
    })
});
