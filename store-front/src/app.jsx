import React from 'react';
import 'normalize.css';
import 'styles/index.scss';
import {MuiThemeProvider} from "material-ui";
import Provider from "react-redux/es/components/Provider";
import Routes from "./routes";
import store from "./store/store";

const App = () => (
  <Provider store={store}>
    <MuiThemeProvider>
      <Routes/>
    </MuiThemeProvider>
  </Provider>
);

export default App;
