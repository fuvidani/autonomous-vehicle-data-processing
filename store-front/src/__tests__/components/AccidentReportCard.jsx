import {mount} from 'enzyme'
import * as Enzyme from 'enzyme';
import ReactSixteenAdapter from 'enzyme-adapter-react-16';
import AccidentReportCard from '../../components/authority/AccidentReportCard'
import React from 'react';
import PropTypes from 'prop-types'
import getMuiTheme from 'material-ui/styles/getMuiTheme';

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

describe('<AccidentReportCard />', () => {
    const muiTheme = getMuiTheme();

    it('Basic render with test object', () => {
        const wrapper = mount(<AccidentReportCard report={reportObject}/>, {
            context: {muiTheme},
            childContextTypes: {muiTheme: PropTypes.object}
        });
        expect(wrapper.find('ListItem').length).toEqual(7);
        expect(wrapper.find('List').length).toEqual(1);
        expect(wrapper.find('StaticMapComponent').length).toEqual(1);
    })
});
